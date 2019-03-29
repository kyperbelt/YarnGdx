package com.kyper.yarn;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

  // single-line comments, If this is encountered at any point, the rest of the line is just skipped
  public static final String LINE_COMMENT  = "//";
  public static final char   FORWARD_SLASH = '/';

  public static final String LINE_SEPARATOR = "\n";

  //states consts
  private static final String BASE        = "base";
  private static final String DASH        = "-";
  private static final String COMMAND     = "command";
  private static final String LINK        = "link";
  private static final String SHORTCUT    = "shortcut";
  private static final String TAG         = "tag";
  private static final String EXPRESSION  = "expression";
  private static final String ASSIGNMENT  = "assignment";
  private static final String OPTION      = "option";
  private static final String OR          = "or";
  private static final String DESTINATION = "destination";

  private static final Regex WHITESPACE = new Regex("\\s*");

  protected ObjectMap<String, LexerState> states;

  protected LexerState defaultState;
  protected LexerState currentState;

  protected Array<IntBoolPair> indentationStack;
  protected boolean            shouldTrackNextIndent;
  Regex initialIndentRegex;

  public Lexer(){
    createStates();
  }

  public static String padRight(String s, int n){
    return String.format("%1$-" + n + "s", s);
  }

  public static String padLeft(String s, int n){
    return String.format("%1$" + n + "s", s);
  }

  private void createStates(){
    ObjectMap<TokenType, String> patterns = new ObjectMap<Lexer.TokenType, String>();

    patterns.put(TokenType.Text, ".*");

    patterns.put(TokenType.Number, "\\-?[0-9]+(\\.[0-9+])?");
    patterns.put(TokenType.Str, "\"([^\"\"\\\\]*(?:\\.[^\"\"\\\\]*)*)\"");
    patterns.put(TokenType.TagMarker, "\\#");
    patterns.put(TokenType.LeftParen, "\\(");
    patterns.put(TokenType.RightParen, "\\)");
    patterns.put(TokenType.EqualTo, "(==|is(?!\\w)|eq(?!\\w))");
    patterns.put(TokenType.EqualToOrAssign, "(=|to(?!\\w))");
    patterns.put(TokenType.NotEqualTo, "(\\!=|neq(?!\\w))");
    patterns.put(TokenType.GreaterThanOrEqualTo, "(\\>=|gte(?!\\w))");
    patterns.put(TokenType.GreaterThan, "(\\>|gt(?!\\w))");
    patterns.put(TokenType.LessThanOrEqualTo, "(\\<=|lte(?!\\w))");
    patterns.put(TokenType.LessThan, "(\\<|lt(?!\\w))");
    patterns.put(TokenType.AddAssign, "\\+=");
    patterns.put(TokenType.MinusAssign, "\\-=");
    patterns.put(TokenType.MultiplyAssign, "\\*=");
    patterns.put(TokenType.DivideAssign, "\\/=");
    patterns.put(TokenType.Add, "\\+");
    patterns.put(TokenType.Minus, "\\-");
    patterns.put(TokenType.Multiply, "\\*");
    patterns.put(TokenType.Divide, "\\/");
    patterns.put(TokenType.Modulo, "\\%");
    patterns.put(TokenType.And, "(\\&\\&|and(?!\\w))");
    patterns.put(TokenType.Or, "(\\|\\||or(?!\\w))");
    patterns.put(TokenType.Xor, "(\\^|xor(?!\\w))");
    patterns.put(TokenType.Not, "(\\!|not(?!\\w))");
    patterns.put(TokenType.Variable, "\\$([A-Za-z0-9_\\.])+");
    patterns.put(TokenType.Comma, "\\,");
    patterns.put(TokenType.True, "true(?!\\w)");
    patterns.put(TokenType.False, "false(?!\\w)");
    patterns.put(TokenType.Null, "null(?!\\w)");

    patterns.put(TokenType.BeginCommand, "\\<\\<");
    patterns.put(TokenType.EndCommand, "\\>\\>");

    patterns.put(TokenType.OptionStart, "\\[\\[");
    patterns.put(TokenType.OptionEnd, "\\]\\]");
    patterns.put(TokenType.OptionDelimit, "\\|");

    patterns.put(TokenType.Identifier, "[a-zA-Z0-9_:\\.]+");

    patterns.put(TokenType.If, "if(?!\\w)");
    patterns.put(TokenType.Else, "else(?!\\w)");
    patterns.put(TokenType.ElseIf, "elseif(?!\\w)");
    patterns.put(TokenType.EndIf, "endif(?!\\w)");
    patterns.put(TokenType.Set, "set(?!\\w)");

    patterns.put(TokenType.ShortcutOption, "\\-\\>");

    //compound states
    String shortcutOption      = SHORTCUT + DASH + OPTION;
    String shortcutOptionTag   = shortcutOption + DASH + TAG;
    String commandOrExpression = COMMAND + DASH + OR + DASH + EXPRESSION;
    String linkDestination     = LINK + DASH + DESTINATION;

    states = new ObjectMap<String, Lexer.LexerState>();

    states.put(BASE, new LexerState(patterns));
    states.get(BASE).addTransition(TokenType.BeginCommand, COMMAND, true);
    states.get(BASE).addTransition(TokenType.OptionStart, LINK, true);
    states.get(BASE).addTransition(TokenType.ShortcutOption, shortcutOption);
    states.get(BASE).addTransition(TokenType.TagMarker, TAG, true);
    states.get(BASE).addTextRule(TokenType.Text);

    states.put(TAG, new LexerState(patterns));
    states.get(TAG).addTransition(TokenType.Identifier, BASE);

    states.put(shortcutOption, new LexerState(patterns));
    states.get(shortcutOption).trackNextIndentation = true;
    states.get(shortcutOption).addTransition(TokenType.BeginCommand, EXPRESSION, true);
    states.get(shortcutOption).addTransition(TokenType.TagMarker, shortcutOptionTag, true);
    states.get(shortcutOption).addTextRule(TokenType.Text, BASE);

    states.put(shortcutOptionTag, new LexerState(patterns));
    states.get(shortcutOptionTag).addTransition(TokenType.Identifier, shortcutOption);

    states.put(COMMAND, new LexerState(patterns));
    states.get(COMMAND).addTransition(TokenType.If, EXPRESSION);
    states.get(COMMAND).addTransition(TokenType.Else);
    states.get(COMMAND).addTransition(TokenType.ElseIf, EXPRESSION);
    states.get(COMMAND).addTransition(TokenType.EndIf);
    states.get(COMMAND).addTransition(TokenType.Set, ASSIGNMENT);
    states.get(COMMAND).addTransition(TokenType.EndCommand, BASE, true);
    states.get(COMMAND).addTransition(TokenType.Identifier, commandOrExpression);
    states.get(COMMAND).addTextRule(TokenType.Text);

    states.put(commandOrExpression, new LexerState(patterns));
    states.get(commandOrExpression).addTransition(TokenType.LeftParen, EXPRESSION);
    states.get(commandOrExpression).addTransition(TokenType.EndCommand, BASE, true);
    states.get(commandOrExpression).addTextRule(TokenType.Text);

    states.put(ASSIGNMENT, new LexerState(patterns));
    states.get(ASSIGNMENT).addTransition(TokenType.Variable);
    states.get(ASSIGNMENT).addTransition(TokenType.EqualToOrAssign, EXPRESSION);
    states.get(ASSIGNMENT).addTransition(TokenType.AddAssign, EXPRESSION);
    states.get(ASSIGNMENT).addTransition(TokenType.MinusAssign, EXPRESSION);
    states.get(ASSIGNMENT).addTransition(TokenType.MultiplyAssign, EXPRESSION);
    states.get(ASSIGNMENT).addTransition(TokenType.DivideAssign, EXPRESSION);

    states.put(EXPRESSION, new LexerState(patterns));
    states.get(EXPRESSION).addTransition(TokenType.EndCommand, BASE);
    states.get(EXPRESSION).addTransition(TokenType.Number);
    states.get(EXPRESSION).addTransition(TokenType.Str);
    states.get(EXPRESSION).addTransition(TokenType.LeftParen);
    states.get(EXPRESSION).addTransition(TokenType.RightParen);
    states.get(EXPRESSION).addTransition(TokenType.EqualTo);
    states.get(EXPRESSION).addTransition(TokenType.EqualToOrAssign);
    states.get(EXPRESSION).addTransition(TokenType.NotEqualTo);
    states.get(EXPRESSION).addTransition(TokenType.GreaterThanOrEqualTo);
    states.get(EXPRESSION).addTransition(TokenType.GreaterThan);
    states.get(EXPRESSION).addTransition(TokenType.LessThanOrEqualTo);
    states.get(EXPRESSION).addTransition(TokenType.LessThan);
    states.get(EXPRESSION).addTransition(TokenType.Add);
    states.get(EXPRESSION).addTransition(TokenType.Minus);
    states.get(EXPRESSION).addTransition(TokenType.Multiply);
    states.get(EXPRESSION).addTransition(TokenType.Divide);
    states.get(EXPRESSION).addTransition(TokenType.Modulo);
    states.get(EXPRESSION).addTransition(TokenType.And);
    states.get(EXPRESSION).addTransition(TokenType.Or);
    states.get(EXPRESSION).addTransition(TokenType.Xor);
    states.get(EXPRESSION).addTransition(TokenType.Not);
    states.get(EXPRESSION).addTransition(TokenType.Variable);
    states.get(EXPRESSION).addTransition(TokenType.Comma);
    states.get(EXPRESSION).addTransition(TokenType.True);
    states.get(EXPRESSION).addTransition(TokenType.False);
    states.get(EXPRESSION).addTransition(TokenType.Null);
    states.get(EXPRESSION).addTransition(TokenType.Identifier);

    states.put(LINK, new LexerState(patterns));
    states.get(LINK).addTransition(TokenType.OptionEnd, BASE, true);
    states.get(LINK).addTransition(TokenType.OptionDelimit, linkDestination, true);
    states.get(LINK).addTextRule(TokenType.Text);

    states.put(linkDestination, new LexerState(patterns));
    states.get(linkDestination).addTransition(TokenType.Identifier);
    states.get(linkDestination).addTransition(TokenType.OptionEnd, BASE);

    defaultState = states.get(BASE);

    for (Entry<String, LexerState> entry : states) {
      entry.value.name = entry.key;
    }

  }

  public TokenList tokenise(String text){

    //setup
    indentationStack = new Array<Lexer.IntBoolPair>();
    indentationStack.add(new IntBoolPair(0, false));
    shouldTrackNextIndent = false;

    TokenList tokens = new TokenList();

    currentState = defaultState;

    //parse each line
    Array<String> lines = new Array<String>(text.split(LINE_SEPARATOR));
    //blank line to ensure 0 indentation end
    lines.add("");

    int lineNumber = 1;

    for (String line : lines) {
      tokens.addAll(tokeniseLine(line, lineNumber));
      lineNumber++;
    }

    Token endOfInput = new Token(TokenType.EndOfInput, currentState, lineNumber, 0);
    //tokens.insert(0,endOfInput);
    tokens.add(endOfInput);
    //=====================================
    //TODO: TO REVERSE INPUT PLEAS EUSE THIS
    //tokens.reverse();
    //TODO: REVERSE TOKENS
    //====================================


    return tokens;
  }

  public TokenList tokeniseLine(String line, int lineNumber){
    Array<Token> lineTokensStack = new Array<Token>();

    //replace tabs with four spaces
    line = line.replaceAll("\t", "    ");

    //strip out \r's
    line = line.replaceAll("\r", "");

    //record the indentation level if previous state wants us to
    int         thisIndentation     = lineIndentation(line);
    IntBoolPair previousIndentation = indentationStack.peek();

    if (shouldTrackNextIndent && thisIndentation > previousIndentation.key) {
      //if we are more indented than before, emit an
      //indent token and record this indent level
      indentationStack.add(new IntBoolPair(thisIndentation, true));

      Token indent = new Token(TokenType.Indent, currentState, lineNumber, previousIndentation.key);
      indent.value = padLeft("", thisIndentation - previousIndentation.key);


      shouldTrackNextIndent = false;

      lineTokensStack.add(indent);

    } else if (thisIndentation < previousIndentation.key) {

      //if we are less indented, emit a dedent for every
      //indentation level that we passed on the way back to 0 that also
      //emitted an indentation token.
      //at the same time, remove those indent levels from the stack

      while (thisIndentation < indentationStack.peek().key) {

        IntBoolPair topLevel = indentationStack.pop();

        if (topLevel.value) {
          Token dedent = new Token(TokenType.Dedent, currentState, lineNumber, 0);
          lineTokensStack.add(dedent);
        }
      }
    }

    //now we start finding tokens
    int columnNumber = thisIndentation;

    Regex whitespace = WHITESPACE;

    while (columnNumber < line.length()) {

      //if we are about to hit a line comment, abort processing line
      //asap
      if (line.substring(columnNumber).startsWith(LINE_COMMENT)) break;

      boolean matched = false;

      for (TokenRule rule : currentState.tokenRules) {

        Matcher myMatch = rule.altRegex.match(line, columnNumber);
        Matcher match   = rule.regex.match(line);

        if (!myMatch.find(0)) continue;
        String tokenText;

        if (rule.type == TokenType.Text) {
          //if this is text, then back up to the most recent text
          //delimiting token, and treat everything fromt here as
          //the text.
          //we do this because we dont want this:
          //  <<flip Harley3 +1>>
          //to get matched as this:
          //   BeginCiommand identifier("flip") Text("Harley3 +1") EndCommand
          //instead, we want to match as this
          //  BeginCommand text("flip Harley3 +1") EndCommand

          int textStartIndex = thisIndentation;

          if (lineTokensStack.size > 0) {
            while (lineTokensStack.peek().type == TokenType.Identifier) {
              lineTokensStack.pop();
            }

            Token startDelimiterToken = lineTokensStack.peek();
            textStartIndex = startDelimiterToken.columnNumber;
            if (startDelimiterToken.type == TokenType.Indent) textStartIndex += startDelimiterToken.value.length();
            if (startDelimiterToken.type == TokenType.Dedent) textStartIndex = thisIndentation;
          }

          columnNumber = textStartIndex;


          match.find(columnNumber);

          //TODO: ====
          //THIS IS PROBABLY WRONG
          int textEndIndex = match.start() + match.group().length();
          tokenText = line.substring(textStartIndex, textEndIndex);

        } else {
          tokenText = myMatch.group();

        }

        columnNumber += tokenText.length();


        //if this was a string, lop off the quotes at the start and end
        //and un-escape the quotes and slashes
        if (rule.type == TokenType.Str) {

          tokenText = tokenText.substring(1, tokenText.length() - 1);
          tokenText = tokenText.replaceAll("\\\\", "\\");
          tokenText = tokenText.replaceAll("\\\"", "\"");
        }

        //System.out.println("line:"+lineNumber+" col:"+columnNumber+" text:"+tokenText);

        Token token = new Token(rule.type, currentState, lineNumber, columnNumber, tokenText);
        token.delimitsText = rule.delimitsText;

        lineTokensStack.add(token);

        if (rule.enterState != null) {
          if (!states.containsKey(rule.enterState))
            throw new TokeniserException(lineNumber, columnNumber, "Unkown tokeniser state " + rule.enterState);


          enterState(states.get(rule.enterState));

          if (shouldTrackNextIndent == true) {
            if (indentationStack.peek().key < thisIndentation) {
              indentationStack.add(new IntBoolPair(thisIndentation, false));
            }
          }
        }

        matched = true;
        break;
      }


      if (!matched) {
        throw TokeniserException.expectedTokens(lineNumber, columnNumber, currentState);
      }

      Matcher lastWhiteSpace = whitespace.match(line);
      if (lastWhiteSpace.find(columnNumber)) {
        columnNumber = lastWhiteSpace.end();
      }
    }

    TokenList listToReturn = new TokenList(lineTokensStack);
    //listToReturn.reverse();

    return listToReturn;
  }

  private int lineIndentation(String line){
    if (initialIndentRegex == null) initialIndentRegex = new Regex("^(\\s*)");
    Matcher match = initialIndentRegex.match(line);

    if (!match.find() || match.group(0) == null) return 0;
    return match.group(0).length();
  }

  private void enterState(LexerState state){
    currentState = state;
    if (currentState.trackNextIndentation) shouldTrackNextIndent = true;
  }

  protected enum TokenType {

    // Special tokens
    Whitespace,
    Indent,
    Dedent,
    EndOfLine,
    EndOfInput,

    // Numbers. Everybody loves a number
    Number,

    // Strings. Everybody also loves a string
    Str,

    // '#'
    TagMarker,

    // Command syntax ("<<foo>>")
    BeginCommand,
    EndCommand,

    // Variables ("$foo")
    Variable,

    // Shortcut syntax ("->")
    ShortcutOption,

    // Option syntax ("[[Let's go here|Destination]]")
    OptionStart, // [[
    OptionDelimit, // |
    OptionEnd, // ]]

    // Command types (specially recognised command word)
    If,
    ElseIf,
    Else,
    EndIf,
    Set,

    // Boolean values
    True,
    False,

    // The null value
    Null,

    // Parentheses
    LeftParen,
    RightParen,

    // Parameter delimiters
    Comma,

    // Operators
    EqualTo, // ==, eq, is
    GreaterThan, // >, gt
    GreaterThanOrEqualTo, // >=, gte
    LessThan, // <, lt
    LessThanOrEqualTo, // <=, lte
    NotEqualTo, // !=, neq

    // Logical operators
    Or, // ||, or
    And, // &&, and
    Xor, // ^, xor
    Not, // !, not

    // this guy's special because '=' can mean either 'equal to'
    // or 'becomes' depending on context
    EqualToOrAssign, // =, to

    UnaryMinus, // -; this is differentiated from Minus
    // when parsing expressions

    Add, // +
    Minus, // -
    Multiply, // *
    Divide, // /
    Modulo, // %

    AddAssign, // +=
    MinusAssign, // -=
    MultiplyAssign, // *=
    DivideAssign, // /=

    Comment, // a run of text that we ignore

    Identifier, // a single word (used for functions)

    Text // a run of text until we hit other syntax
  }

  protected static class TokeniserException extends IllegalStateException {
    private static final long serialVersionUID = 337269479504244415L;

    public int lineNumber;
    public int columnNumber;

    public TokeniserException(String message){
      super(message);
    }

    public TokeniserException(int lineNumber, int columnNumber, String message){
      super(String.format("%1$s : %2$s : %3$s", lineNumber, columnNumber, message));
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
    }

    public static TokeniserException expectedTokens(int lineNumber, int columnNumber, LexerState state){
      Array<String> names = new Array<String>();
      for (TokenRule rule : state.tokenRules) {
        names.add(rule.type.name());
      }

      String nameList;

      if (names.size > 1) {
        String lastItem = names.pop();
        nameList = String.join(", ", names);
        nameList += ", or " + lastItem;
      } else {
        nameList = names.first();
      }

      String message = String.format("Expected %s", nameList);

      return new TokeniserException(lineNumber, columnNumber, message);
    }

  }

  protected static class Regex {

    private Pattern       pattern;
    private StringBuilder stringBuilder;

    public Regex(String pattern){
      this.pattern = Pattern.compile(pattern);
      this.stringBuilder = new StringBuilder();
    }

    public Regex(Pattern pattern){
      this.pattern = pattern;
    }

    public Pattern getPattern(){
      return pattern;
    }

    public void setPattern(String pattern){
      this.pattern = Pattern.compile(pattern);
    }

    public void setPattern(Pattern pattern){
      this.pattern = pattern;
    }

    @Override
    public String toString(){
      return pattern.toString();
    }

    public Matcher match(CharSequence text){
      return pattern.matcher(text);
    }

    public Matcher match(String text, int beginIndex){
      stringBuilder.setLength(0);
      stringBuilder.append(text, beginIndex, text.length());
      return match(stringBuilder);
    }

    public Matcher match(String text, int beginIndex, int endIndex){
      stringBuilder.setLength(0);
      stringBuilder.append(text, beginIndex, endIndex);
      return match(stringBuilder);
    }
  }

  /**
   *
   */
  public class TokenList extends Array<Token> {

    public TokenList(){
      super();
    }

    public TokenList(Token[] tokens){
      super(tokens);
    }

    public TokenList(Array<Token> tokens){
      super(tokens);
    }
  }

  protected class Token {

    //the token itself
    public TokenType type;
    public String    value; //optional

    //where we found this token
    public int    lineNumber;
    public int    columnNumber;
    public String context;

    public boolean delimitsText = false;

    //if this is a function in an expression
    //this is the number of parameters
    public int parameterCount;

    //the state that the lexer was in when the token was emitted
    public String lexerState;

    public Token(TokenType type, LexerState lexerState, int lineNumber, int columnNumber, String value){
      this.type = type;
      this.lexerState = lexerState.name;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
      this.value = value;
    }

    public Token(TokenType type, LexerState lexerState, int lineNumber, int columnNumber){
      this(type, lexerState, lineNumber, columnNumber, null);
    }

    public Token(TokenType type, LexerState lexerState, int lineNumber){
      this(type, lexerState, lineNumber, -1);
    }

    public Token(TokenType type, LexerState lexerState){
      this(type, lexerState, -1);
    }

    @Override
    public String toString(){
      if (value != null) {
        return String.format("%1$s (%2$s) at %3$s:%4$s (state: %5$s)",
                             type.name(),
                             value,
                             lineNumber,
                             columnNumber,
                             lexerState);
      }
      return String.format("%1$s at %2$s :%3$s (state: %4$s)", type, lineNumber, columnNumber, lexerState);
    }

  }

  protected class LexerState {

    public  String                       name;
    public  Array<TokenRule>             tokenRules;
    public  boolean                      trackNextIndentation;
    private ObjectMap<TokenType, String> patterns;

    public LexerState(ObjectMap<TokenType, String> patterns){

      this.patterns = patterns;
      tokenRules = new Array<Lexer.TokenRule>();
      trackNextIndentation = false;

    }

    public TokenRule addTransition(TokenType type, String enterState, boolean delimitsText){

      String    pattern = String.format("\\G%1$s", patterns.get(type));
      TokenRule rule    = new TokenRule(type, new Regex(pattern), enterState, delimitsText);
      tokenRules.add(rule);
      return rule;
    }

    public TokenRule addTransition(TokenType type, String enterState){
      return addTransition(type, enterState, false);
    }

    public TokenRule addTransition(TokenType type){
      return addTransition(type, null);
    }

    /**
     * a text rule matches everything that it possibly can, up to Any of the rules that already exist.
     *
     * @return {@link TokenRule} - text rule
     */
    public TokenRule addTextRule(TokenType type, String enterState){
      if (containsTextRule()) {
        throw new IllegalStateException("State already contains a text rule");
      }
      Array<String> delimiterRules = new Array<String>();

      for (TokenRule otherRule : tokenRules) {
        if (otherRule.delimitsText)
          delimiterRules.add(String.format("(%1$s)", otherRule.regex.toString().substring(2)));
      }

      //create a regex that matches all text up to but not including
      //any of the delimiter rules

      String pattern = String.format("\\G((?!%1$s).)*", String.join("|", delimiterRules));

      TokenRule rule = addTransition(type, enterState);
      rule.regex = new Regex(pattern);
      rule.isTextRule = true;

      return rule;

    }

    public TokenRule addTextRule(TokenType type){
      return addTextRule(type, null);
    }

    public boolean containsTextRule(){
      for (TokenRule rule : tokenRules) {
        if (rule.isTextRule) return true;
      }
      return false;
    }

  }

  protected class TokenRule {
    public Regex regex    = null;
    public Regex altRegex = null;

    //set to null if should stay in same state
    public String    enterState;
    public TokenType type;
    public boolean   isTextRule   = false;
    public boolean   delimitsText = false;

    public TokenRule(TokenType type, Regex regex, String enterState, boolean delimitsText){
      this.regex = regex;
      this.enterState = enterState;
      this.type = type;
      this.delimitsText = delimitsText;
      altRegex = new Regex(regex.pattern.toString().replace("\\G", "^"));
    }

    @Override
    public String toString(){
      return String.format("[TokenRule: %s - %s ]", type, this.regex);
    }
  }

  public class IntBoolPair extends Entry<Integer, Boolean> {
    public IntBoolPair(int key, boolean value){
      this.key = key;
      this.value = value;
    }
  }

}
