package com.kyper.yarn;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.kyper.yarn.Lexer.Token;
import com.kyper.yarn.Lexer.TokenType;
import com.kyper.yarn.Library.FunctionInfo;
import com.kyper.yarn.Parser.Operator.OperatorInfo;
import com.kyper.yarn.YarnProgram.ParseException;

public class Parser {

  //we will be consuming tokens fast
  //TODO: i think becuase im not using a queue some issues might be caused by
  //TODO: lexer function that reverses the tokens array
  protected Array<Token> tokens;
  protected Library      library;

  public Parser(Array<Token> tokens, Library library){
    this.tokens = tokens;
    //TODO:================================
    //TODO: fix? this.tokens.reverse();
    //this.tokens.reverse();
    ///TODO: ===============================
    this.library = library;
  }

  //indents are 'input' String 'indentLevel' times;
  private static String tab(int indentLevel, String input, boolean newline){
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indentLevel; i++) {
      sb.append("| ");
    }
    sb.append(input);

    if (newline) sb.append("\n");
    return sb.toString();
  }

  private static String tab(int indentLevel, String input){
    return tab(indentLevel, input, true);
  }

  public static int indexOf(Object[] array, Object value){
    for (int i = 0; i < array.length; i++) {
      if (array[i] == value) return i;
    }
    return -1;
  }

  public Node parse(){
    //kick off the parsing process by trying to parse a whole node;
    return new Node("Start", null, this);
  }

  /**
   * return true if the next symbol is part of the valid types;
   *
   * @return
   */
  public boolean nextSymbolIs(TokenType... validTypes){

    TokenType t = this.tokens.first().type;
    for (TokenType validType : validTypes) {
      if (t == validType) return true;
    }
    return false;
  }

  /**
   * used to look ahead - return true if the symbols are of the validTypes. good for when looking for '<<' 'else' ect.
   *
   * @param validTypes
   *
   * @return
   */
  public boolean nextSymbolsAre(TokenType... validTypes){
    Array<Token> temp = new Array<Lexer.Token>(tokens);
    //temp.reverse();
    for (TokenType type : validTypes) {
      if (temp.removeIndex(0).type != type) return false;
    }
    return true;
  }

  /**
   * return the next token,which must be of the 'type' or throw an exception
   *
   * @param type
   *
   * @return
   */
  public Token expectSymbol(TokenType type){
    Token t = this.tokens.removeIndex(0);
    if (t.type != type) {
      throw ParseException.make(t, type);
    }
    return t;
  }

  /**
   * return the next token which an only be of any type except endOfInput
   *
   * @return
   */
  public Token expectSymbol(){
    Token t = this.tokens.removeIndex(0);
    if (t.type == TokenType.EndOfInput) {
      throw ParseException.make(t, "unexpected end of input.");
    }
    return t;
  }

  /**
   * return the next token, which must be one of th e validTypes. or throws an exception
   *
   * @param validTypes
   *
   * @return
   */
  public Token expectSymbol(TokenType... validTypes){
    Token t = this.tokens.removeIndex(0);
    for (TokenType validType : validTypes) {
      if (t.type == validType) return t;
    }
    throw ParseException.make(t, validTypes);
  }

  public static abstract class ParseNode {
    protected ParseNode parent;

    //the line that this prase node begins on.
    protected int lineNumber;

    protected Array<String> tags;

    //ParseNodes do their parsing by consuming tokens from the Parser.
    //You parse tokens into ParseNode by using its constructor
    protected ParseNode(ParseNode parent, Parser p){
      this.parent = parent;
      if (p.tokens.size > 0) this.lineNumber = p.tokens.first().lineNumber;
      else this.lineNumber = -1;
      tags = new Array<String>();
    }

    /**
     * recursively prints the ParseNode and all of its child ParseNodes
     *
     * @param indentLevel
     *
     * @return
     */
    public abstract String printTree(int indentLevel);

    public String tagsToString(int indentLevel){
      if (tags.size > 0) {
        StringBuilder s = new StringBuilder();

        s.append(tab(indentLevel + 1, "Tags:"));
        for (String tag : this.tags) {
          s.append(tab(indentLevel + 2, "#" + tag));
        }
        return s.toString();
      } else {
        return "";
      }
    }

    @Override
    public String toString(){
      return this.getClass().getSimpleName();
    }

    /** the closest parent to this ParseNode that is a Node */
    protected Node nodeParent(){
      ParseNode node = this;
      do {
        if (node instanceof Node) {
          return (Node) node;
        }
        node = node.parent;
      } while (node != null);

      return null;
    }

  }

  //top level unit of parsing.
  //node = (statement)*endofinput
  public static class Node extends ParseNode {
    private String name;
    private String source;

    //defined in the yarn editor
    private Array<String> nodeTags;

    private Array<Statement> statements = new Array<Statement>();

    protected Node(String name, ParseNode parent, Parser p){
      super(parent, p);
      this.name = name;
      //consume statements until we run out of input or hit a dedent
      while (p.tokens.size > 0 && !p.nextSymbolIs(TokenType.Dedent, TokenType.EndOfInput)) {
        statements.add(new Statement(this, p));
      }
    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder sb = new StringBuilder();
      sb.append(tab(indentLevel, "Node " + name + " {"));
      for (Statement statement : statements) {
        sb.append(statement.printTree(indentLevel + 1));
      }
      sb.append(tab(indentLevel, "}"));
      return sb.toString();
    }

    /** readOnly private accesor for statements */
    public Array<Statement> getStatements(){
      return statements;
    }

    public Array<String> getNodeTags(){
      return nodeTags;
    }

    public void setNodeTags(Array<String> nodeTags){
      this.nodeTags = nodeTags;
    }

    public String getName(){
      return name;
    }

    public void setName(String name){
      this.name = name;
    }

    public String getSource(){
      return source;
    }

    public void setSource(String source){
      this.source = source;
    }

  }

  //Statements are the items of execution in nodes.
  //Statements = block
  //Statements = ifStatement
  //Statemens = OptionStatement
  //Statements = ShortcutOptionGroup
  //Statements = CustomCommand
  //Statements = AssignmentStatement
  //Statements = <TEXT>
  protected static class Statement extends ParseNode {

    private Type                type;
    //possible types of statements we can have
    private Block               block;
    private IfStatement         ifStatement;
    private OptionStatement     optionSatement;
    private AssignmentStatement assignmentStatement;
    private CustomCommand       customCommand;
    private String              line;
    private ShortcutOptionGroup shortcutOptionGroup;

    protected Statement(ParseNode parent, Parser p){
      super(parent, p);

      if (Block.canParse(p)) {
        type = Type.Block;
        block = new Block(this, p);
      } else if (IfStatement.canParse(p)) {
        type = Type.IfStatement;
        ifStatement = new IfStatement(this, p);
      } else if (OptionStatement.canParse(p)) {
        type = Type.OptionStatement;
        optionSatement = new OptionStatement(this, p);
      } else if (AssignmentStatement.canParse(p)) {
        type = Type.AssignmentStatement;
        assignmentStatement = new AssignmentStatement(this, p);
      } else if (ShortcutOptionGroup.canParse(p)) {
        type = Type.ShortcutOptionGroup;
        shortcutOptionGroup = new ShortcutOptionGroup(this, p);
      } else if (CustomCommand.canParse(p)) {
        type = Type.CustomCommand;
        customCommand = new CustomCommand(this, p);
      } else if (p.nextSymbolIs(TokenType.Text)) {
        line = p.expectSymbol(TokenType.Text).value;
        type = Type.Line;
      } else {
        throw ParseException.make(p.tokens.first(),
                                  "Expected a statement here but got " + p.tokens.first().toString() + " instead " +
                                          "(was" + " there an unbalanced if statement earlier?)");
      }
      //parse the optional tags that follow this statement
      Array<String> tags = new Array<String>();

      while (p.nextSymbolIs(TokenType.TagMarker)) {
        p.expectSymbol(TokenType.TagMarker);
        String tag = p.expectSymbol(TokenType.Identifier).value;
        tags.add(tag);
      }

      if (tags.size > 0) this.tags = tags;
    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder s = new StringBuilder();
      switch (type) {
        case Block:
          s.append(block.printTree(indentLevel));
          break;
        case IfStatement:
          s.append(ifStatement.printTree(indentLevel));
          break;
        case OptionStatement:
          s.append(optionSatement.printTree(indentLevel));
          break;
        case AssignmentStatement:
          s.append(assignmentStatement.printTree(indentLevel));
          break;
        case ShortcutOptionGroup:
          s.append(shortcutOptionGroup.printTree(indentLevel));
          break;
        case CustomCommand:
          s.append(customCommand.printTree(indentLevel));
          break;
        case Line:
          s.append(tab(indentLevel, "Line: " + line));
          break;
        default:
          throw new IllegalArgumentException();
      }

      s.append(tagsToString(indentLevel));

      return s.toString();
    }

    public Type getType(){
      return type;
    }

    public Block getBlock(){
      return block;
    }

    public IfStatement getIfStatement(){
      return ifStatement;
    }

    public OptionStatement getOptionStatement(){
      return optionSatement;
    }

    public AssignmentStatement getAssignmentStatement(){
      return assignmentStatement;
    }

    public CustomCommand getCustomCommand(){
      return customCommand;
    }

    public String getLine(){
      return line;
    }

    public ShortcutOptionGroup getShortcutOptionGroup(){
      return shortcutOptionGroup;
    }

    public enum Type {
      CustomCommand,
      ShortcutOptionGroup,
      Block,
      IfStatement,
      OptionStatement,
      AssignmentStatement,
      Line
    }

  }

  // Shortcut option groups are groups of shortcut options,
  // followed by the node that they rejoin.
  // ShortcutOptionGroup = ShortcutOption+ Node

  //Custom commands are meant to be interpreted by whatever
  // system that owns this dialogue sytem. eg <<stand>>
  // CustomCommand = BeginCommand <ANY>* EndCommand
  protected static class CustomCommand extends ParseNode {

    protected Type       type;
    private   Expression expression;
    private   String     clientCommand;

    protected CustomCommand(ParseNode parent, Parser p){
      super(parent, p);
      p.expectSymbol(TokenType.BeginCommand);

      //custom commands can have any token in them, Read them all until we hit the
      //end of command token
      Array<Token> commandTokens = new Array<Lexer.Token>();
      do {
        commandTokens.add(p.expectSymbol());
      } while (!p.nextSymbolIs(TokenType.EndCommand));
      p.expectSymbol(TokenType.EndCommand);

      //if the first token is an identifier and the second is
      //a left paren, it may be a function call expression;
      //evaluate it as such
      if (commandTokens.size > 1 && commandTokens.get(0).type == TokenType.Identifier && commandTokens.get(1).type == TokenType.LeftParen) {

        Parser     parser     = new Parser(commandTokens, p.library);
        Expression expression = Expression.parse(this, parser);
        type = Type.Expression;
        this.expression = expression;
      } else {
        //otherwise, evaluate it as a command
        type = Type.ClientCommand;
        this.clientCommand = commandTokens.get(0).value;

      }

    }

    protected static boolean canParse(Parser p){
      return p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Text) || p.nextSymbolsAre(TokenType.BeginCommand,
                                                                                          TokenType.Identifier);
    }

    @Override
    public String printTree(int indentLevel){
      switch (type) {
        case Expression:
          return tab(indentLevel, "Expression: ") + expression.printTree(indentLevel + 1);
        case ClientCommand:
          return tab(indentLevel, "Command: " + clientCommand);
      }
      return "";
    }

    public Expression getExpression(){
      return expression;
    }

    public String getClientCommand(){
      return clientCommand;
    }

    protected enum Type {
      Expression,
      ClientCommand
    }

  }

  protected static class ShortcutOptionGroup extends ParseNode {
    private Array<ShortcutOption> options = new Array<Parser.ShortcutOption>();

    protected ShortcutOptionGroup(ParseNode parent, Parser p){
      super(parent, p);

      //keep parsing options until we cant, but expect at least one (otherwise its
      //not actually a list of options)
      int shortcutIndex = 1;//give each option a number so it can name itself
      do {
        options.add(new ShortcutOption(shortcutIndex++, this, p));
      } while (p.nextSymbolIs(TokenType.ShortcutOption));
    }

    protected static boolean canParse(Parser p){
      return p.nextSymbolIs(TokenType.ShortcutOption);
    }

    protected Array<ShortcutOption> getOptions(){
      return options;
    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder sb = new StringBuilder();
      sb.append(tab(indentLevel, "Shortcut option group {"));

      for (ShortcutOption option : options) {
        sb.append(option.printTree(indentLevel + 1));
      }
      sb.append(tab(indentLevel, "}"));

      return sb.toString();
    }
  }

  // Shortcut options are a convenient way to define new options.
  // ShortcutOption = -> <text> [BeginCommand If Expression EndCommand] [Block]
  protected static class ShortcutOption extends ParseNode {

    private String     label;
    private Expression condition;
    private Node       optionNode;

    protected ShortcutOption(int optionIndex, ParseNode parent, Parser p){
      super(parent, p);
      p.expectSymbol(TokenType.ShortcutOption);
      label = p.expectSymbol(TokenType.Text).value;

      //parse the conditional ("<< if $foo >>) if its there

      Array<String> tags = new Array<String>();
      while (p.nextSymbolsAre(TokenType.BeginCommand, TokenType.If) || p.nextSymbolIs(TokenType.TagMarker)) {

        if (p.nextSymbolsAre(TokenType.BeginCommand, TokenType.If)) {
          p.expectSymbol(TokenType.BeginCommand);
          p.expectSymbol(TokenType.If);
          condition = Expression.parse(this, p);
          p.expectSymbol(TokenType.EndCommand);
        } else if (p.nextSymbolIs(TokenType.TagMarker)) {

          p.expectSymbol(TokenType.TagMarker);
          String tag = p.expectSymbol(TokenType.Identifier).value;
          tags.add(tag);
        }
      }

      this.tags = tags;

      //parse the statements belonging to this option if has any
      if (p.nextSymbolIs(TokenType.Indent)) {
        p.expectSymbol(TokenType.Indent);
        optionNode = new Node(nodeParent().name + "." + optionIndex, this, p);
        p.expectSymbol(TokenType.Dedent);
      }

    }

    public Expression getCondition(){
      return condition;
    }

    public Node getOptionNode(){
      return optionNode;
    }

    protected String getLabel(){
      return label;
    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder sb = new StringBuilder();
      sb.append(tab(indentLevel, "Option \"" + label + "\""));

      if (condition != null) {
        sb.append(tab(indentLevel + 1, "(when:"));
        sb.append(condition.printTree(indentLevel + 2));
        sb.append(tab(indentLevel + 1, "),"));
      }

      if (optionNode != null) {
        sb.append(tab(indentLevel, "{"));
        sb.append(optionNode.printTree(indentLevel + 1));
        sb.append(tab(indentLevel, "}"));
      }

      sb.append(tagsToString(indentLevel));

      return sb.toString();
    }
  }

  // Blocks are indented groups of statements
  // Block = Indent Statement* Dedent
  protected static class Block extends ParseNode {

    //readonly
    private Array<Statement> statements = new Array<Parser.Statement>();

    protected Block(ParseNode parent, Parser p){
      super(parent, p);

      //read the indent token
      p.expectSymbol(TokenType.Indent);

      //keep readin satements until we hit a dedent
      while (!p.nextSymbolIs(TokenType.Dedent)) {
        //fun fact!because Blocks are a type of statement,
        //we get nested block parsing for free! --includeded in original comment: (\:D/) -- lol
        statements.add(new Statement(this, p));
      }

      //tidy up by reading the dedent
      p.expectSymbol(TokenType.Dedent);
    }

    protected static boolean canParse(Parser p){
      return p.nextSymbolIs(TokenType.Indent);
    }

    public Array<Statement> getStatements(){
      return statements;
    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder sb = new StringBuilder();
      sb.append(tab(indentLevel, "Block {"));
      for (Statement statement : getStatements()) {
        sb.append(statement.printTree(indentLevel + 1));
      }
      sb.append(tab(indentLevel, "}"));

      return sb.toString();
    }
  }

  // Options are links to other nodes
  // OptionStatement = OptionStart <Text> OptionEnd
  // OptionStatement = OptionStart <Text> OptionDelimit <Text>|<Identifier> OptionEnd
  protected static class OptionStatement extends ParseNode {

    private String destination;
    private String label;

    protected OptionStatement(ParseNode parent, Parser p){
      super(parent, p);

      //the meaning of the string(s) we have changes
      //depending on whether we have one or two, so
      //keep them both and decide their meaning once
      //we know more

      String firstString;
      String secondString;

      //Parse "[[LABEL"
      p.expectSymbol(TokenType.OptionStart);
      firstString = p.expectSymbol(TokenType.Text).value;

      //if there's a | in there, get the string that comes after it
      if (p.nextSymbolIs(TokenType.OptionDelimit)) {

        p.expectSymbol(TokenType.OptionDelimit);
        secondString = p.expectSymbol(TokenType.Text, TokenType.Identifier).value;

        //two strings mean that the first is the label, and the second
        //is the name of the node that we should head to if the option
        //is selected
        label = firstString;
        destination = secondString;

      } else {
        //one string means we dont have a label
        label = null;
        destination = firstString;
      }

      //parse the closing "]]"
      p.expectSymbol(TokenType.OptionEnd);
    }

    protected static boolean canParse(Parser p){
      return p.nextSymbolIs(TokenType.OptionStart);
    }

    public String getDestination(){
      return destination;
    }

    public String getLabel(){
      return label;
    }

    @Override
    public String printTree(int indentLevel){
      if (label != null) {
        return tab(indentLevel, StringUtils.format("Option: \"%1$s\" -> %2$s", label, destination));
      } else {
        return tab(indentLevel, StringUtils.format("Option: -> %s", destination));
      }
    }

  }

  // If statements are the usual if-else-elseif-endif business.
  // If = BeginCommand If Expression EndCommand Statement* BeginCommand EndIf EndCommand
  // TODO: elseif
  protected static class IfStatement extends ParseNode {

    protected Array<Clause> clauses = new Array<Parser.IfStatement.Clause>();

    protected IfStatement(ParseNode parent, Parser p){
      super(parent, p);

      //all if statements begin with "<<if EXPRESSION>>", so parse that
      Clause primaryClause = new Clause();

      p.expectSymbol(TokenType.BeginCommand);
      p.expectSymbol(TokenType.If);
      primaryClause.setExpression(Expression.parse(this, p));
      p.expectSymbol(TokenType.EndCommand);

      //read the statements for this clause until we hit an <<endif or <<else
      //(which could be an "<<else>>" or an <<else if)
      Array<Statement> statements = new Array<Parser.Statement>();
      while (!p.nextSymbolsAre(TokenType.BeginCommand, TokenType.EndIf) && !p.nextSymbolsAre(TokenType.BeginCommand,
                                                                                             TokenType.Else) && !p.nextSymbolsAre(
              TokenType.BeginCommand,
              TokenType.ElseIf)) {

        statements.add(new Statement(this, p));

        //ignore any dedents
        while (p.nextSymbolIs(TokenType.Dedent)) p.expectSymbol(TokenType.Dedent);

      }

      primaryClause.setStatements(statements);
      clauses.add(primaryClause);

      //Handle as many <<elseif clauses as we find
      while (p.nextSymbolsAre(TokenType.BeginCommand, TokenType.ElseIf)) {
        Clause elseIfClause = new Clause();

        //parse the syntax for this clauses condition
        p.expectSymbol(TokenType.BeginCommand);
        p.expectSymbol(TokenType.ElseIf);
        elseIfClause.setExpression(Expression.parse(this, p));
        p.expectSymbol(TokenType.EndCommand);

        //read statements until we hit an <<endif, <<else or another <<elseif
        Array<Statement> clauseStatements = new Array<Parser.Statement>();
        while (!p.nextSymbolsAre(TokenType.BeginCommand, TokenType.EndIf) && !p.nextSymbolsAre(TokenType.BeginCommand,
                                                                                               TokenType.Else) && !p.nextSymbolsAre(
                TokenType.BeginCommand,
                TokenType.ElseIf)) {
          clauseStatements.add(new Statement(this, p));

          //ignore any dedents
          while (p.nextSymbolIs(TokenType.Dedent)) p.expectSymbol(TokenType.Dedent);

        }

        elseIfClause.setStatements(clauseStatements);
        clauses.add(elseIfClause);
      }

      //handle <<else>> if we have one
      if (p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Else, TokenType.EndCommand)) {

        //parse the syntax (no expression this time, just <<else>>
        p.expectSymbol(TokenType.BeginCommand);
        p.expectSymbol(TokenType.Else);
        p.expectSymbol(TokenType.EndCommand);

        //and parse statements until we hit <<endif
        Clause           elseClause       = new Clause();
        Array<Statement> clauseStatements = new Array<Parser.Statement>();

        while (!p.nextSymbolsAre(TokenType.BeginCommand, TokenType.EndIf)) {
          clauseStatements.add(new Statement(this, p));
        }

        elseClause.setStatements(clauseStatements);
        clauses.add(elseClause);

        //ignore any dedents
        while (p.nextSymbolIs(TokenType.Dedent)) p.expectSymbol(TokenType.Dedent);
      }

      //finish up by reading the endif
      p.expectSymbol(TokenType.BeginCommand);
      p.expectSymbol(TokenType.EndIf);
      p.expectSymbol(TokenType.EndCommand);

    }

    protected static boolean canParse(Parser p){
      return p.nextSymbolsAre(TokenType.BeginCommand, TokenType.If);
    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder sb    = new StringBuilder();
      boolean       first = true;
      for (Clause clause : clauses) {
        if (first) {
          sb.append(tab(indentLevel, "If:"));
          first = false;
        } else if (clause.expression != null) {
          sb.append(tab(indentLevel, "Else If:"));
        } else {
          sb.append(tab(indentLevel, "Else:"));
        }
        sb.append(clause.printTree(indentLevel + 1));
      }

      return sb.toString();
    }

    // Clauses are collections of statements with an
    // optional conditional that determines whether they're run
    // or not. The condition is used by the If and ElseIf parts of
    // an if statement, and not used by the Else statement.
    protected static class Clause {
      private Expression       expression;
      private Array<Statement> statements;

      protected Clause(){
        expression = null;
        statements = null;
      }

      protected Clause(Expression expression, Array<Statement> statements){
        this.expression = expression;
        this.statements = statements;
      }

      protected Expression getExpression(){
        return expression;
      }

      protected void setExpression(Expression expression){
        this.expression = expression;
      }

      protected Array<Statement> getStatements(){
        return statements;
      }

      protected void setStatements(Array<Statement> statements){
        this.statements = statements;
      }

      public String printTree(int indentLevel){
        StringBuilder sb = new StringBuilder();
        if (expression != null) sb.append(expression.printTree(indentLevel));
        sb.append(tab(indentLevel, "{"));
        for (Statement statement : statements) {
          sb.append(statement.printTree(indentLevel + 1));
        }
        sb.append(tab(indentLevel, "}"));
        return sb.toString();
      }

    }
  }

  // A value, which forms part of an expression.
  protected static class ValueNode extends ParseNode {

    private Value value;

    protected ValueNode(ParseNode parent, Token t, Parser p){
      super(parent, p);
      useToken(t);
    }

    protected ValueNode(ParseNode parent, Parser p){
      super(parent, p);
      Token t = p.expectSymbol(TokenType.Number, TokenType.Variable, TokenType.Str);
      useToken(t);
    }

    @Override
    public String printTree(int indentLevel){

      switch (value.getType()) {
        case NUMBER:
          return tab(indentLevel, "" + value.getNumberValue());
        case STRING:
          return tab(indentLevel, StringUtils.format("\"%s\"", value.getStringValue()));
        case BOOL:
          return tab(indentLevel, value.asString());
        case VARNAME:
          return tab(indentLevel, value.getVarName());
        case NULL:
          return tab(indentLevel, "(null)");
      }
      throw new IllegalArgumentException();
    }

    public Value getValue(){
      return value;
    }

    private void useToken(Token t){
      // Store the value depending on token's type
      switch (t.type) {
        case Number:
          value = new Value(Float.parseFloat(t.value));
          break;
        case Str:
          value = new Value(t.value);
          break;
        case False:
          value = new Value(false);
          break;
        case True:
          value = new Value(true);
          break;
        case Variable:
          value = new Value();
          value.setType(Value.Type.VARNAME);
          value.setVarName(t.value);
          break;
        case Null:
          value = Value.NULL;
          break;
        default:
          throw ParseException.make(t, "Invalid token type " + t.toString());
      }
    }

  }

  // Expressions are things like "1 + 2 * 5 / 2 - 1"
  // Expression = Expression Operator Expression
  // Expression = Identifier ( Expression [, Expression]* )
  // Expression = Value
  protected static class Expression extends ParseNode {

    protected Type              type;
    protected ValueNode         value;
    protected FunctionInfo      function;
    protected Array<Expression> params;

    protected Expression(ParseNode parent, ValueNode value, Parser p){
      super(parent, p);
      this.type = Type.Value;
      this.value = value;
    }

    protected Expression(ParseNode parent, FunctionInfo function, Array<Expression> params, Parser p){
      super(parent, p);
      this.type = Type.FunctionCall;
      this.function = function;
      this.params = params;
    }

    protected static Expression parse(ParseNode parent, Parser p){

      // Applies Djikstra's "shunting-yard" algorithm to convert the
      // stream of infix expressions into postfix notation; we then
      // build a tree of expressions from the result
      // https://en.wikipedia.org/wiki/Shunting-yardAlgorithm

      Array<Token> expression_RPN = new Array<Lexer.Token>();
      Array<Token> operatorStack  = new Array<Lexer.Token>();

      //used for keeping count of parameters for each function
      Array<Token> functionStack = new Array<Lexer.Token>();

      Array<TokenType> validTokenTypes = new Array<Lexer.TokenType>(Operator.operatorTypes());
      validTokenTypes.add(TokenType.Number);
      validTokenTypes.add(TokenType.Variable);
      validTokenTypes.add(TokenType.Str);
      validTokenTypes.add(TokenType.LeftParen);
      validTokenTypes.add(TokenType.RightParen);
      validTokenTypes.add(TokenType.Identifier);
      validTokenTypes.add(TokenType.Comma);
      validTokenTypes.add(TokenType.True);
      validTokenTypes.add(TokenType.False);
      validTokenTypes.add(TokenType.Null);

      Token lastToken = null;

      //read all the contents of the expression
      while (p.tokens.size > 0 && p.nextSymbolIs(validTokenTypes.toArray())) {
        Token nextToken = p.expectSymbol(validTokenTypes.toArray());

        if (nextToken.type == TokenType.Number || nextToken.type == TokenType.Variable || nextToken.type == TokenType.Str || nextToken.type == TokenType.True || nextToken.type == TokenType.False || nextToken.type == TokenType.Null) {

          //primitive values go straight to output
          expression_RPN.add(nextToken);
        } else if (nextToken.type == TokenType.Identifier) {
          operatorStack.add(nextToken);//push
          functionStack.add(nextToken);//push

          //next token must be a left paren, so process that immediately
          nextToken = p.expectSymbol(TokenType.LeftParen);

          //enter that sub expression
          operatorStack.add(nextToken); //push

        } else if (nextToken.type == TokenType.Comma) {

          //Resolve this sub expression before moving on
          try {
            //pop operators until we reach a left paren
            while (operatorStack.peek().type != TokenType.LeftParen) expression_RPN.add(operatorStack.pop());

          } catch (IllegalStateException e) {
            //we reached end of stack too early
            //this means unbalanced parenthesis
            throw ParseException.make(nextToken, "Error parsing expression:  unbalanced parentheses");
          }

          //we expect the top of the stack to now contain the left paren that
          //begain the list of parameters
          if (operatorStack.peek().type != TokenType.LeftParen) {
            throw ParseException.make(operatorStack.peek(),
                                      "Expression parser got " + "confused dealing with a function");
          }

          //the next token is not allowed to be a right paren or comma
          //(that is, you cant say "foo(2,,)")
          if (p.nextSymbolIs(TokenType.RightParen, TokenType.Comma)) {
            throw ParseException.make(p.tokens.first(), "Expected expression");
          }

          //find the closest function on the stack
          //and increment the number of params
          functionStack.peek().parameterCount++;

        } else if (Operator.isOperator(nextToken.type)) {
          //this is an operator

          //if this is a minus, we need to determine if it is a
          //unary minus or a binary minus.
          //unary minus looks like this : -1
          //binary minus looks like this 2 - 3
          //thins get complex when we say stuff like: 1 + -1
          //but its easier when we realize that a minus
          //is only unary when the last token was a left paren,
          //an operator, or its the first token.
          if (nextToken.type == TokenType.Minus) {

            if (lastToken == null || lastToken.type == TokenType.LeftParen || Operator.isOperator(lastToken.type)) {

              //this is unary minus
              nextToken.type = TokenType.UnaryMinus;

            }
          }

          //we cannot assign values inside an expression. That is,
          //saying "foo = 2" in an expression does not assign foo to 2
          //and then evaluate to 2. instead, yarn defines this
          //to mean "foo == 2"
          if (nextToken.type == TokenType.EqualToOrAssign) {
            nextToken.type = TokenType.EqualTo;
          }

          // O1 = this operator
          // O2 = the token at the top of the stack
          // While O2 is an operator, and EITHER: 1. O1 is left-associative and
          // has precedence <= O2, or 2. O1 is right-associative and
          // has precedence > O2:
          while (shouldApplyPrecedence(nextToken.type, operatorStack)) {
            Token o = operatorStack.pop();
            expression_RPN.add(o);
          }

          operatorStack.add(nextToken);

        } else if (nextToken.type == TokenType.LeftParen) {

          //Record that we have entered a paren delimited
          //subexpression
          operatorStack.add(nextToken);

        } else if (nextToken.type == TokenType.RightParen) {

          //we are leaving a subexpression; time to resolve the
          //order of operations that we saw in between the parens;
          try {
            while (operatorStack.peek().type != TokenType.LeftParen) {
              expression_RPN.add(operatorStack.pop());
            }
            //pop left paren
            operatorStack.pop();
          } catch (IllegalStateException e) {
            throw ParseException.make(nextToken, "Error parsing expression: unbalanced parentheses");
          }

          if (operatorStack.peek().type == TokenType.Identifier) {
            //this whole paren-delimited subexpression is actually
            //a function call

            //if the last token was a left paren, then this
            //was a function with no params; otherwise, we
            //have an additional parameter (on top of the ones we counter
            //while encountering commas)
            if (lastToken.type != TokenType.LeftParen) {
              functionStack.peek().parameterCount++;
            }

            expression_RPN.add(operatorStack.pop());
            functionStack.pop();
          }

        }

        //record this as the last token we saw; well use it
        //to figure out if the minuses are unary or not
        lastToken = nextToken;

      }

      //no more tokens; pop all operators onto the output queue
      while (operatorStack.size > 0) {
        expression_RPN.add(operatorStack.pop());
      }

      //if the output queue is empty, then this is not an expression
      if (expression_RPN.size == 0) {
        throw new ParseException("Error parsing expression: no expression found!");
      }

      //we now have this in more easly parsed RPN form;
      //time to build the expression tree
      Token             firstToken      = expression_RPN.first();
      Array<Expression> evaluationStack = new Array<Parser.Expression>();
      while (expression_RPN.size > 0) {

        Token next = expression_RPN.removeIndex(0);
        if (Operator.isOperator(next.type)) {

          //this is an operation

          OperatorInfo info = Operator.infoForOperator(next.type);
          if (evaluationStack.size < info.arguments) {
            throw ParseException.make(next,
                                      "Error parsing expression: not enough " + "arguments for operator " + next.type.name());
          }

          Array<Expression> params = new Array<Parser.Expression>();
          for (int i = 0; i < info.arguments; i++) {
            params.add(evaluationStack.pop());
          }
          params.reverse();

          FunctionInfo operatorFunction = p.library.getFunction(next.type.name());

          Expression exp = new Expression(parent, operatorFunction, params, p);

          evaluationStack.add(exp);

        } else if (next.type == TokenType.Identifier) {
          //thhis is a function call

          FunctionInfo info = null;

          //if we are a lib, use it to check if the
          //number of parameters proveded is correct
          if (p.library != null) {
            info = p.library.getFunction(next.value);

            //ensure that this call has the right number of params;
            if (!info.isParamCountCorrect(next.parameterCount)) {
              String error = StringUtils.format("Error parsing expression: " + "Unsupported number of parameters for "
                                                        + "function %1$s (expected %2$s, got %3$s)",
                                                next.value,
                                                info.getParamCount(),
                                                next.parameterCount);
              throw ParseException.make(next, error);
            }

          } else {

            //use a dummy FunctionInfo to represent info about
            //the fact that a function is called; note that
            //attempting to call this will fail
            info = new FunctionInfo(next.value, next.parameterCount);
          }

          Array<Expression> paramList = new Array<Parser.Expression>();
          for (int i = 0; i < next.parameterCount; i++) {
            paramList.add(evaluationStack.pop());
          }

          paramList.reverse();

          Expression exp = new Expression(parent, info, paramList, p);

          evaluationStack.add(exp);

        } else {
          //this is a raw value
          ValueNode  v   = new ValueNode(parent, next, p);
          Expression exp = new Expression(parent, v, p);
          evaluationStack.add(exp);
        }

      }

      //we should now have a single expression in this stack, which is the root
      //of the expressions tree. if we have more than one, then we have a problem
      if (evaluationStack.size != 1) {
        throw ParseException.make(firstToken, "Error parsing expression (stack did not reduce correctly)");
      }

      //return it
      return evaluationStack.pop();

    }

    /**
     * used to determine weather shunting-yard algorithm should pop operators from the operator stack
     */
    private static boolean shouldApplyPrecedence(TokenType o1, Array<Token> operatorStack){
      if (operatorStack.size == 0) return false;

      if (!Operator.isOperator(o1)) throw new ParseException("Internal error parsing expression");

      TokenType o2 = operatorStack.peek().type;

      if (Operator.isOperator(o2) == false) return false;

      OperatorInfo o1Info = Operator.infoForOperator(o1);
      OperatorInfo o2Info = Operator.infoForOperator(o2);

      if (o1Info.associativity == Operator.Associativity.Left && o1Info.precedence <= o2Info.precedence) {
        return true;
      }

      return o1Info.associativity == Operator.Associativity.Right && o1Info.precedence < o2Info.precedence;

    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder sb = new StringBuilder();
      switch (type) {
        case Value:
          return value.printTree(indentLevel);
        case FunctionCall:

          if (params.size == 0) {
            sb.append(tab(indentLevel, "Function call to " + function.getName() + " (no parameters)"));
          } else {
            sb.append(tab(indentLevel,
                          "Function call to " + function.getName() + " (" + params.size + " parameters) {"));
            for (Expression param : params) {
              sb.append(param.printTree(indentLevel + 1));
            }
            sb.append(tab(indentLevel, "}"));
          }
          return sb.toString();

      }

      return tab(indentLevel, "<error printing expression!>");
    }

    protected enum Type {
      Value,
      FunctionCall
    }
  }

  // AssignmentStatements are things like <<set $foo = 1>>
  // AssignmentStatement = BeginCommand Set <variable> <operation> Expression EndCommand
  protected static class AssignmentStatement extends ParseNode {

    private String     destinationVariable;
    private Expression valueExpression;
    private TokenType  operation;

    protected AssignmentStatement(ParseNode parent, Parser p){
      super(parent, p);
      p.expectSymbol(TokenType.BeginCommand);
      p.expectSymbol(TokenType.Set);
      destinationVariable = p.expectSymbol(TokenType.Variable).value;
      operation = p.expectSymbol(validOperators()).type;
      valueExpression = Expression.parse(this, p);
      p.expectSymbol(TokenType.EndCommand);
    }

    private static TokenType[] validOperators(){
      return new TokenType[]{TokenType.EqualToOrAssign, TokenType.AddAssign, TokenType.MinusAssign,
              TokenType.DivideAssign, TokenType.MultiplyAssign};
    }

    protected static boolean canParse(Parser p){
      return p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Set);
    }

    protected String getDestinationVariable(){
      return destinationVariable;
    }

    protected Expression getValueExpression(){
      return valueExpression;
    }

    protected TokenType getOperation(){
      return operation;
    }

    @Override
    public String printTree(int indentLevel){
      StringBuilder sb = new StringBuilder();
      sb.append(tab(indentLevel, "Set:"));
      sb.append(tab(indentLevel + 1, destinationVariable));
      sb.append(tab(indentLevel + 1, operation.toString()));
      sb.append(getValueExpression().printTree(indentLevel + 1));
      return sb.toString();
    }

  }

  // Operators are used in expressions - things like + - / * != neq
  protected static class Operator extends ParseNode {

    private TokenType operatorType;

    protected Operator(ParseNode parent, TokenType t, Parser p){
      super(parent, p);
      operatorType = t;
    }

    protected Operator(ParseNode parent, Parser p){
      super(parent, p);
      operatorType = p.expectSymbol(operatorTypes()).type;
    }

    protected static OperatorInfo infoForOperator(TokenType op){
      if (indexOf(operatorTypes(), op) == -1) throw new ParseException(op.name() + " is not a valid operator");

      //determine the precedence, associativity and
      //number of operands that each operator has
      switch (op) {

        case Not:
        case UnaryMinus:
          return new OperatorInfo(Associativity.Right, 30, 1);

        case Multiply:
        case Divide:
        case Modulo:
          return new OperatorInfo(Associativity.Left, 20, 2);
        case Add:
        case Minus:
          return new OperatorInfo(Associativity.Left, 15, 2);
        case GreaterThan:
        case LessThan:
        case GreaterThanOrEqualTo:
        case LessThanOrEqualTo:
          return new OperatorInfo(Associativity.Left, 10, 2);
        case EqualTo:
        case EqualToOrAssign:
        case NotEqualTo:
          return new OperatorInfo(Associativity.Left, 5, 2);
        case And:
          return new OperatorInfo(Associativity.Left, 4, 2);
        case Or:
          return new OperatorInfo(Associativity.Left, 3, 2);
        case Xor:
          return new OperatorInfo(Associativity.Left, 2, 2);
        default:
          break;

      }
      throw new IllegalStateException("Unknown operator " + op.name());

    }

    protected static boolean isOperator(TokenType type){
      return indexOf(operatorTypes(), type) != -1;
    }

    protected static TokenType[] operatorTypes(){
      TokenType[] t = new TokenType[]{TokenType.Not, TokenType.UnaryMinus,

              TokenType.Add, TokenType.Minus, TokenType.Divide, TokenType.Multiply, TokenType.Modulo,

              TokenType.EqualToOrAssign, TokenType.EqualTo, TokenType.GreaterThan, TokenType.GreaterThanOrEqualTo,
              TokenType.LessThan, TokenType.LessThanOrEqualTo, TokenType.NotEqualTo,

              TokenType.And, TokenType.Or,

              TokenType.Xor};

      return t;
    }

    @Override
    public String printTree(int indentLevel){
      return tab(indentLevel, operatorType.name());
    }

    protected enum Associativity {
      Left, // resolve leftmost operand first
      Right, // resolve rightmost operand first
      None // special-case (like "(", ")", ","
    }

    //info used during expression parsing
    protected static class OperatorInfo {
      protected Associativity associativity;
      protected int           precedence;
      protected int           arguments;

      protected OperatorInfo(Associativity associativity, int precedence, int arguments){
        this.associativity = associativity;
        this.precedence = precedence;
        this.arguments = arguments;
      }
    }
  }

}
