package com.kyper.yarn;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.kyper.yarn.Lexer.Token;
import com.kyper.yarn.Lexer.TokenType;
import com.kyper.yarn.Library.FunctionInfo;
import com.kyper.yarn.Parser.Operator.OperatorInfo;
import com.kyper.yarn.Program.ParseException;

public class Parser {

	//we will be consuming tokens fast
	//TODO: i think becuase im not using a queue some issues might be caused by 
	//TODO: lexer function that reverses the tokens array
	protected Array<Token> tokens;
	protected Library library;

	public Parser(Array<Token> tokens, Library library) {
		this.tokens = tokens;
		//TODO:================================
		//TODO: fix? this.tokens.reverse();
		//this.tokens.reverse(); 
		///TODO: ===============================
		this.library = library;
	}

	public Node parse() {
		//kick off the parsing process by trying to parse a whole node;
		return new Node("Start", null, this);
	}

	/**
	 * return true if the next symbol is part of the valid types;
	 * 
	 * @return
	 */
	public boolean nextSymbolIs(TokenType... valid_types) {
		
		TokenType t = this.tokens.first().type;
		for (TokenType valid_type : valid_types) {
			if (t == valid_type)
				return true;
		}
		return false;
	}

	/**
	 * used to look ahead - return true if the symbols are of the valid_types. good
	 * for when looking for '<<' 'else' ect.
	 * 
	 * @param valid_types
	 * @return
	 */
	public boolean nextSymbolsAre(TokenType... valid_types) {
		Array<Token> temp = new Array<Lexer.Token>(tokens);
		//temp.reverse();
		for (TokenType type : valid_types) {
			if (temp.removeIndex(0).type != type)
				return false;
		}
		return true;
	}

	/**
	 * return the next token,which must be of the 'type' or throw an exception
	 * 
	 * @param type
	 * @return
	 */
	public Token expectSymbol(TokenType type) {
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
	public Token expectSymbol() {
		Token t = this.tokens.removeIndex(0);
		if (t.type == TokenType.EndOfInput) {
			throw ParseException.make(t, "unexpected end of input.");
		}
		return t;
	}

	/**
	 * return the next token, which must be one of th e valid_types. or throws an
	 * exception
	 * 
	 * @param valid_types
	 * @return
	 */
	public Token expectSymbol(TokenType... valid_types) {
		Token t = this.tokens.removeIndex(0);
		for (TokenType valid_type : valid_types) {
			if (t.type == valid_type)
				return t;
		}
		throw ParseException.make(t, valid_types);
	}

	//indents are 'input' String 'indentLevel' times;
	private static String tab(int indent_level, String input, boolean newline) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent_level; i++) {
			sb.append("| ");
		}
		sb.append(input);

		if (newline)
			sb.append("\n");
		return sb.toString();
	}

	private static String tab(int indent_level, String input) {
		return tab(indent_level, input, true);
	}

	public static abstract class ParseNode {
		protected ParseNode parent;

		//the line that this prase node begins on.
		protected int line_number;

		protected Array<String> tags;

		//ParseNodes do their parsing by consuming tokens from the Parser.
		//You parse tokens into ParseNode by using its constructor
		protected ParseNode(ParseNode parent, Parser p) {
			this.parent = parent;
			if (p.tokens.size > 0)
				this.line_number = p.tokens.first().line_number;
			else
				this.line_number = -1;
			tags = new Array<String>();
		}

		/**
		 * recursively prints the ParseNode and all of its child ParseNodes
		 * 
		 * @param indent_level
		 * @return
		 */
		public abstract String printTree(int indent_level);

		public String tagsToString(int indent_level) {
			if (tags.size > 0) {
				StringBuilder s = new StringBuilder();

				s.append(tab(indent_level + 1, "Tags:"));
				for (String tag : this.tags) {
					s.append(tab(indent_level + 2, "#" + tag));
				}
				return s.toString();
			} else {
				return "";
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName();
		}

		/** the closest parent to this ParseNode that is a Node */
		protected Node nodeParent() {
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
		private Array<String> node_tags;

		private Array<Statement> statements = new Array<Statement>();

		protected Node(String name, ParseNode parent, Parser p) {
			super(parent, p);
			this.name = name;
			//consume statements until we run out of input or hit a dedent
			while (p.tokens.size > 0 && !p.nextSymbolIs(TokenType.Dedent, TokenType.EndOfInput)) {
				statements.add(new Statement(this, p));
			}
		}

		@Override
		public String printTree(int indent_level) {
			StringBuilder sb = new StringBuilder();
			sb.append(tab(indent_level, "Node " + name + " {"));
			for (Statement statement : statements) {
				sb.append(statement.printTree(indent_level + 1));
			}
			sb.append(tab(indent_level, "}"));
			return sb.toString();
		}

		/** read_only private accesor for statements */
		public Array<Statement> getStatements() {
			return statements;
		}

		public Array<String> getNodeTags() {
			return node_tags;
		}

		public void setNodeTags(Array<String> node_tags) {
			this.node_tags = node_tags;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
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

		public enum Type {
			CustomCommand, ShortcutOptionGroup, Block, IfStatement, OptionStatement, AssignmentStatement, Line
		}

		private Type type;

		//possible types of statements we can have
		private Block block;
		private IfStatement if_statement;
		private OptionStatement option_satement;
		private AssignmentStatement assignment_statement;
		private CustomCommand custom_command;
		private String line;
		private ShortcutOptionGroup shortcut_option_group;

		protected Statement(ParseNode parent, Parser p) {
			super(parent, p);
			
			if (Block.canParse(p)) {
                type = Type.Block;
                block = new Block(this, p);
            } else if (IfStatement.canParse(p)) {
                type = Type.IfStatement;
                if_statement = new IfStatement(this, p);
            } else if (OptionStatement.canParse(p)) {
                type = Type.OptionStatement;
                option_satement = new OptionStatement(this, p);
            } else if (AssignmentStatement.canParse(p)) {
                type = Type.AssignmentStatement;
                assignment_statement = new AssignmentStatement(this, p);
            } else if (ShortcutOptionGroup.canParse(p)) {
                type = Type.ShortcutOptionGroup;
                shortcut_option_group = new ShortcutOptionGroup(this, p);
            } else if (CustomCommand.canParse(p)) {
                type = Type.CustomCommand;
                custom_command = new CustomCommand(this, p);
            } else if (p.nextSymbolIs(TokenType.Text)) {
                line = p.expectSymbol(TokenType.Text).value;
                type = Type.Line;
            } else {
				throw ParseException.make(p.tokens.first(), "Expected a statement here but got " + p.tokens.first().toString() +" instead (was there an unbalanced if statement earlier?)");
            }
			//parse the optional tags that follow this statement
			Array<String> tags = new Array<String>();
			
			while(p.nextSymbolIs(TokenType.TagMarker)) {
				p.expectSymbol(TokenType.TagMarker);
				String tag = p.expectSymbol(TokenType.Identifier).value;
				tags.add(tag);
			}
			
			if(tags.size>0)
				this.tags = tags;
		}

		@Override
		public String printTree(int indent_level) {
			  StringBuilder s = new StringBuilder ();
              switch (type) {
              case Block:
                  s.append(block.printTree (indent_level));
                  break;
              case IfStatement:
                  s.append (if_statement.printTree (indent_level));
                  break;
              case OptionStatement:
                  s.append (option_satement.printTree (indent_level));
                  break;
              case AssignmentStatement:
                  s.append (assignment_statement.printTree (indent_level));
                  break;
              case ShortcutOptionGroup:
                  s.append (shortcut_option_group.printTree (indent_level));
                  break;
              case CustomCommand:
                  s.append (custom_command.printTree (indent_level));
                  break;
              case Line:
                  s.append (tab (indent_level, "Line: " + line));
                  break;
              default:
                  throw new IllegalArgumentException ();
              }

              s.append (tagsToString(indent_level));

              return s.toString ();
		}

		public Type getType() {
			return type;
		}

		public Block getBlock() {
			return block;
		}

		public IfStatement getIfStatement() {
			return if_statement;
		}

		public OptionStatement getOptionStatement() {
			return option_satement;
		}

		public AssignmentStatement getAssignmentStatement() {
			return assignment_statement;
		}

		public CustomCommand getCustomCommand() {
			return custom_command;
		}

		public String getLine() {
			return line;
		}

		public ShortcutOptionGroup getShortcutOptionGroup() {
			return shortcut_option_group;
		}

	}

	//Custom commands are meant to be interpreted by whatever
	// system that owns this dialogue sytem. eg <<stand>>
	// CustomCommand = BeginCommand <ANY>* EndCommand
	protected static class CustomCommand extends ParseNode {

		protected enum Type {
			Expression, ClientCommand
		}

		protected Type type;

		private Expression expression;
		private String client_command;

		protected CustomCommand(ParseNode parent, Parser p) {
			super(parent, p);
			p.expectSymbol(TokenType.BeginCommand);

			//custom commands can have any token in them, Read them all until we hit the 
			//end of command token
			Array<Token> command_tokens = new Array<Lexer.Token>();
			do {
				command_tokens.add(p.expectSymbol());
			} while (!p.nextSymbolIs(TokenType.EndCommand));
			p.expectSymbol(TokenType.EndCommand);

			//if the first token is an identifier and the second is
			//a left paren, it may be a function call expression;
			//evaluate it as such
			if (command_tokens.size > 1 && command_tokens.get(0).type == TokenType.Identifier
					&& command_tokens.get(1).type == TokenType.LeftParen) {
				
				Parser parser = new Parser(command_tokens, p.library);
				Expression expression = Expression.parse(this, parser);
				type = Type.Expression;
				this.expression = expression;
			} else {
				//otherwise, evaluate it as a command
				type = Type.ClientCommand;
				this.client_command = command_tokens.get(0).value;

			}

		}

		@Override
		public String printTree(int indent_level) {
			switch (type) {
			case Expression:
				return tab(indent_level, "Expression: ") + expression.printTree(indent_level + 1);
			case ClientCommand:
				return tab(indent_level, "Command: " + client_command);
			}
			return "";
		}

		public Expression getExpression() {
			return expression;
		}

		public String getClientCommand() {
			return client_command;
		}

		protected static boolean canParse(Parser p) {
			return p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Text)
					|| p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Identifier);
		}

	}

	// Shortcut option groups are groups of shortcut options,
	// followed by the node that they rejoin.
	// ShortcutOptionGroup = ShortcutOption+ Node

	protected static class ShortcutOptionGroup extends ParseNode {
		private Array<ShortcutOption> options = new Array<Parser.ShortcutOption>();

		protected ShortcutOptionGroup(ParseNode parent, Parser p) {
			super(parent, p);

			//keep parsing options until we cant, but expect at least one (otherwise its 
			//not actually a list of options)
			int shortcut_index = 1;//give each option a number so it can name itself
			do {
				options.add(new ShortcutOption(shortcut_index++, this, p));
			} while (p.nextSymbolIs(TokenType.ShortcutOption));
		}

		protected Array<ShortcutOption> getOptions() {
			return options;
		}

		@Override
		public String printTree(int indent_level) {
			StringBuilder sb = new StringBuilder();
			sb.append(tab(indent_level, "Shortcut option group {"));

			for (ShortcutOption option : options) {
				sb.append(option.printTree(indent_level + 1));
			}
			sb.append(tab(indent_level, "}"));

			return sb.toString();
		}

		protected static boolean canParse(Parser p) {
			return p.nextSymbolIs(TokenType.ShortcutOption);
		}
	}

	// Shortcut options are a convenient way to define new options.
	// ShortcutOption = -> <text> [BeginCommand If Expression EndCommand] [Block]
	protected static class ShortcutOption extends ParseNode {

		private String label;
		private Expression condition;
		private Node option_node;

		protected ShortcutOption(int option_index, ParseNode parent, Parser p) {
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
				option_node = new Node(nodeParent().name + "." + option_index, this, p);
				p.expectSymbol(TokenType.Dedent);
			}

		}

		public Expression getCondition() {
			return condition;
		}

		public Node getOptionNode() {
			return option_node;
		}

		protected String getLabel() {
			return label;
		}

		@Override
		public String printTree(int indent_level) {
			StringBuilder sb = new StringBuilder();
			sb.append(tab(indent_level, "Option \"" + label + "\""));

			if (condition != null) {
				sb.append(tab(indent_level + 1, "(when:"));
				sb.append(condition.printTree(indent_level + 2));
				sb.append(tab(indent_level + 1, "),"));
			}

			if (option_node != null) {
				sb.append(tab(indent_level, "{"));
				sb.append(option_node.printTree(indent_level + 1));
				sb.append(tab(indent_level, "}"));
			}

			sb.append(tagsToString(indent_level));

			return sb.toString();
		}
	}

	// Blocks are indented groups of statements
	// Block = Indent Statement* Dedent
	protected static class Block extends ParseNode {

		//readonly
		private Array<Statement> statements = new Array<Parser.Statement>();

		protected Block(ParseNode parent, Parser p) {
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

		public Array<Statement> getStatements() {
			return statements;
		}

		@Override
		public String printTree(int indent_level) {
			StringBuilder sb = new StringBuilder();
			sb.append(tab(indent_level, "Block {"));
			for (Statement statement : getStatements()) {
				sb.append(statement.printTree(indent_level + 1));
			}
			sb.append(tab(indent_level, "}"));

			return sb.toString();
		}

		protected static boolean canParse(Parser p) {
			return p.nextSymbolIs(TokenType.Indent);
		}
	}

	// Options are links to other nodes
	// OptionStatement = OptionStart <Text> OptionEnd
	// OptionStatement = OptionStart <Text> OptionDelimit <Text>|<Identifier> OptionEnd
	protected static class OptionStatement extends ParseNode {

		private String destination;
		private String label;

		protected OptionStatement(ParseNode parent, Parser p) {
			super(parent, p);

			//the meaning of the string(s) we have changes
			//depending on whether we have one or two, so 
			//keep them both and decide their meaning once
			//we know more

			String first_string;
			String second_string;

			//Parse "[[LABEL"
			p.expectSymbol(TokenType.OptionStart);
			first_string = p.expectSymbol(TokenType.Text).value;

			//if there's a | in there, get the string that comes after it
			if (p.nextSymbolIs(TokenType.OptionDelimit)) {

				p.expectSymbol(TokenType.OptionDelimit);
				second_string = p.expectSymbol(TokenType.Text, TokenType.Identifier).value;

				//two strings mean that the first is the label, and the second
				//is the name of the node that we should head to if the option
				//is selected
				label = first_string;
				destination = second_string;

			} else {
				//one string means we dont have a label
				label = null;
				destination = first_string;
			}

			//parse the closing "]]"
			p.expectSymbol(TokenType.OptionEnd);
		}

		public String getDestination() {
			return destination;
		}

		public String getLabel() {
			return label;
		}

		@Override
		public String printTree(int indent_level) {
			if (label != null) {
				return tab(indent_level, String.format("Option: \"%1$s\" -> %2$s", label, destination));
			} else {
				return tab(indent_level, String.format("Option: -> %s", destination));
			}
		}

		protected static boolean canParse(Parser p) {
			return p.nextSymbolIs(TokenType.OptionStart);
		}

	}

	// If statements are the usual if-else-elseif-endif business.
	// If = BeginCommand If Expression EndCommand Statement* BeginCommand EndIf EndCommand
	// TODO: elseif
	protected static class IfStatement extends ParseNode {

		protected Array<Clause> clauses = new Array<Parser.IfStatement.Clause>();

		protected IfStatement(ParseNode parent, Parser p) {
			super(parent, p);

			//all if statements begin with "<<if EXPRESSION>>", so parse that
			Clause primary_clause = new Clause();

			p.expectSymbol(TokenType.BeginCommand);
			p.expectSymbol(TokenType.If);
			primary_clause.setExpression(Expression.parse(this, p));
			p.expectSymbol(TokenType.EndCommand);

			//read the statements for this clause until we hit an <<endif or <<else
			//(which could be an "<<else>>" or an <<else if)
			Array<Statement> statements = new Array<Parser.Statement>();
			while (!p.nextSymbolsAre(TokenType.BeginCommand, TokenType.EndIf)
					&& !p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Else)
					&& !p.nextSymbolsAre(TokenType.BeginCommand, TokenType.ElseIf)) {

				statements.add(new Statement(this, p));

				//ignore any dedents
				while (p.nextSymbolIs(TokenType.Dedent))
					p.expectSymbol(TokenType.Dedent);

			}

			primary_clause.setStatements(statements);
			clauses.add(primary_clause);

			//Handle as many <<elseif clauses as we find
			while (p.nextSymbolsAre(TokenType.BeginCommand, TokenType.ElseIf)) {
				Clause else_if_clause = new Clause();

				//parse the syntax for this clauses condition
				p.expectSymbol(TokenType.BeginCommand);
				p.expectSymbol(TokenType.ElseIf);
				else_if_clause.setExpression(Expression.parse(this, p));
				p.expectSymbol(TokenType.EndCommand);

				//read statements until we hit an <<endif, <<else or another <<elseif
				Array<Statement> clause_statements = new Array<Parser.Statement>();
				while (!p.nextSymbolsAre(TokenType.BeginCommand, TokenType.EndIf)
						&& !p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Else)
						&& !p.nextSymbolsAre(TokenType.BeginCommand, TokenType.ElseIf)) {
					clause_statements.add(new Statement(this, p));

					//ignore any dedents
					while (p.nextSymbolIs(TokenType.Dedent))
						p.expectSymbol(TokenType.Dedent);

				}

				else_if_clause.setStatements(clause_statements);
				clauses.add(else_if_clause);
			}

			//handle <<else>> if we have one
			if (p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Else, TokenType.EndCommand)) {

				//parse the syntax (no expression this time, just <<else>>
				p.expectSymbol(TokenType.BeginCommand);
				p.expectSymbol(TokenType.Else);
				p.expectSymbol(TokenType.EndCommand);

				//and parse statements until we hit <<endif
				Clause else_clause = new Clause();
				Array<Statement> clause_statements = new Array<Parser.Statement>();

				while (!p.nextSymbolsAre(TokenType.BeginCommand, TokenType.EndIf)) {
					clause_statements.add(new Statement(this, p));
				}

				else_clause.setStatements(clause_statements);
				clauses.add(else_clause);

				//ignore any dedents
				while (p.nextSymbolIs(TokenType.Dedent))
					p.expectSymbol(TokenType.Dedent);
			}

			//finish up by reading the endif
			p.expectSymbol(TokenType.BeginCommand);
			p.expectSymbol(TokenType.EndIf);
			p.expectSymbol(TokenType.EndCommand);

		}

		@Override
		public String printTree(int indent_level) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Clause clause : clauses) {
				if (first) {
					sb.append(tab(indent_level, "If:"));
					first = false;
				} else if (clause.expression != null) {
					sb.append(tab(indent_level, "Else If:"));
				} else {
					sb.append(tab(indent_level, "Else:"));
				}
				sb.append(clause.printTree(indent_level + 1));
			}

			return sb.toString();
		}

		protected static boolean canParse(Parser p) {
			return p.nextSymbolsAre(TokenType.BeginCommand, TokenType.If);
		}

		// Clauses are collections of statements with an
		// optional conditional that determines whether they're run
		// or not. The condition is used by the If and ElseIf parts of
		// an if statement, and not used by the Else statement.
		protected static class Clause {
			private Expression expression;
			private Array<Statement> statements;

			protected Clause() {
				expression = null;
				statements = null;
			}

			protected Clause(Expression expression, Array<Statement> statements) {
				this.expression = expression;
				this.statements = statements;
			}

			protected void setExpression(Expression expression) {
				this.expression = expression;
			}

			protected void setStatements(Array<Statement> statements) {
				this.statements = statements;
			}

			protected Expression getExpression() {
				return expression;
			}

			protected Array<Statement> getStatements() {
				return statements;
			}

			public String printTree(int indent_level) {
				StringBuilder sb = new StringBuilder();
				if (expression != null)
					sb.append(expression.printTree(indent_level));
				sb.append(tab(indent_level, "{"));
				for (Statement statement : statements) {
					sb.append(statement.printTree(indent_level + 1));
				}
				sb.append(tab(indent_level, "}"));
				return sb.toString();
			}

		}
	}

	// A value, which forms part of an expression.
	protected static class ValueNode extends ParseNode {

		private Value value;

		protected ValueNode(ParseNode parent, Token t, Parser p) {
			super(parent, p);
			useToken(t);
		}

		protected ValueNode(ParseNode parent, Parser p) {
			super(parent, p);
			Token t = p.expectSymbol(TokenType.Number, TokenType.Variable, TokenType.Str);
			useToken(t);
		}

		@Override
		public String printTree(int indent_level) {

			switch (value.getType()) {
			case NUMBER:
				return tab(indent_level, "" + value.getNumberValue());
			case STRING:
				return tab(indent_level, String.format("\"%s\"", value.getStringValue()));
			case BOOL:
				return tab(indent_level, value.asString());
			case VARNAME:
				return tab(indent_level, value.getVarName());
			case NULL:
				return tab(indent_level, "(null)");
			}
			throw new IllegalArgumentException();
		}

		public Value getValue() {
			return value;
		}

		private void useToken(Token t) {
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

		protected enum Type {
			Value, FunctionCall
		}

		protected Type type;

		protected ValueNode value;

		protected FunctionInfo function;
		protected Array<Expression> params;

		protected Expression(ParseNode parent, ValueNode value, Parser p) {
			super(parent, p);
			this.type = Type.Value;
			this.value = value;
		}

		protected Expression(ParseNode parent, FunctionInfo function, Array<Expression> params, Parser p) {
			super(parent, p);
			this.type = Type.FunctionCall;
			this.function = function;
			this.params = params;
		}

		@Override
		public String printTree(int indent_level) {
			StringBuilder sb = new StringBuilder();
			switch (type) {
			case Value:
				return value.printTree(indent_level);
			case FunctionCall:

				if (params.size == 0) {
					sb.append(tab(indent_level, "Function call to " + function.getName() + " (no parameters)"));
				} else {
					sb.append(tab(indent_level,
							"Function call to " + function.getName() + " (" + params.size + " parameters) {"));
					for (Expression param : params) {
						sb.append(param.printTree(indent_level + 1));
					}
					sb.append(tab(indent_level, "}"));
				}
				return sb.toString();

			}

			return tab(indent_level, "<error printing expression!>");
		}

		protected static Expression parse(ParseNode parent, Parser p) {

			// Applies Djikstra's "shunting-yard" algorithm to convert the
			// stream of infix expressions into postfix notation; we then
			// build a tree of expressions from the result
			// https://en.wikipedia.org/wiki/Shunting-yard_algorithm

			Array<Token> expression_RPN = new Array<Lexer.Token>();
			Array<Token> operator_stack = new Array<Lexer.Token>();

			//used for keeping count of parameters for each function
			Array<Token> function_stack = new Array<Lexer.Token>();
			
			Array<TokenType> valid_token_types = new Array<Lexer.TokenType>(Operator.operatorTypes());
			valid_token_types.add(TokenType.Number);
			valid_token_types.add(TokenType.Variable);
			valid_token_types.add(TokenType.Str);
			valid_token_types.add(TokenType.LeftParen);
			valid_token_types.add(TokenType.RightParen);
			valid_token_types.add(TokenType.Identifier);
			valid_token_types.add(TokenType.Comma);
			valid_token_types.add(TokenType.True);
			valid_token_types.add(TokenType.False);
			valid_token_types.add(TokenType.Null);

			Token last_token = null;

			//read all the contents of the expression
			while (p.tokens.size > 0 && p.nextSymbolIs(valid_token_types.toArray())) {
				Token next_token = p.expectSymbol(valid_token_types.toArray());

				if (next_token.type == TokenType.Number 
						|| next_token.type == TokenType.Variable
						|| next_token.type == TokenType.Str 
						|| next_token.type == TokenType.True
						|| next_token.type == TokenType.False 
						|| next_token.type == TokenType.Null) {

					//primitive values go straight to output
					expression_RPN.add(next_token);
				} else if (next_token.type == TokenType.Identifier) {
					operator_stack.add(next_token);//push 
					function_stack.add(next_token);//push

					//next token must be a left paren, so process that immediately
					next_token = p.expectSymbol(TokenType.LeftParen);
					
					//enter that sub expression
					operator_stack.add(next_token); //push
					
				} else if (next_token.type == TokenType.Comma) {

					//Resolve this sub expression before moving on
					try {
						//pop operators until we reach a left paren
						while (operator_stack.peek().type != TokenType.LeftParen)
							expression_RPN.add(operator_stack.pop());

					} catch (IllegalStateException e) {
						//we reached end of stack too early
						//this means unbalanced parenthesis
						throw ParseException.make(next_token, "Error parsing expression:  unbalanced parentheses");
					}

					//we expect the top of the stack to now contain the left paren that
					//begain the list of parameters
					if (operator_stack.peek().type != TokenType.LeftParen) {
						throw ParseException.make(operator_stack.peek(),
								"Expression parser got " + "confused dealing with a function");
					}

					//the next token is not allowed to be a right paren or comma
					//(that is, you cant say "foo(2,,)")
					if (p.nextSymbolIs(TokenType.RightParen, TokenType.Comma)) {
						throw ParseException.make(p.tokens.first(), "Expected expression");
					}

					//find the closest function on the stack
					//and increment the number of params
					function_stack.peek().parameter_count++;

				} else if (Operator.isOperator(next_token.type)) {
					//this is an operator

					//if this is a minus, we need to determine if it is a
					//unary minus or a binary minus.
					//unary minus looks like this : -1
					//binary minus looks like this 2 - 3
					//thins get complex when we say stuff like: 1 + -1
					//but its easier when we realize that a minus
					//is only unary when the last token was a left paren,
					//an operator, or its the first token.
					if (next_token.type == TokenType.Minus) {
						
						if (last_token == null ||
								last_token.type == TokenType.LeftParen
								|| Operator.isOperator(last_token.type)) {

							//this is unary minus
							next_token.type = TokenType.UnaryMinus;

						}
					}

					//we cannot assign values inside an expression. That is,
					//saying "foo = 2" in an expression does not assign foo to 2
					//and then evaluate to 2. instead, yarn defines this
					//to mean "foo == 2"
					if (next_token.type == TokenType.EqualToOrAssign) {
						next_token.type = TokenType.EqualTo;
					}

					// O1 = this operator
					// O2 = the token at the top of the stack
					// While O2 is an operator, and EITHER: 1. O1 is left-associative and
					// has precedence <= O2, or 2. O1 is right-associative and
					// has precedence > O2:
					while (shouldApplyPrecedence(next_token.type, operator_stack)) {
						Token o = operator_stack.pop();
						expression_RPN.add(o);
					}

					operator_stack.add(next_token);

				} else if (next_token.type == TokenType.LeftParen) {

					//Record that we have entered a paren delimited
					//subexpression
					operator_stack.add(next_token);

				} else if (next_token.type == TokenType.RightParen) {

					//we are leaving a subexpression; time to resolve the
					//order of operations that we saw in between the parens;
					try {
						while (operator_stack.peek().type != TokenType.LeftParen) {
							expression_RPN.add(operator_stack.pop());
						}
						//pop left paren
						operator_stack.pop();
					} catch (IllegalStateException e) {
						throw ParseException.make(next_token, "Error parsing expression: unbalanced parentheses");
					}

					if (operator_stack.peek().type == TokenType.Identifier) {
						//this whole paren-delimited subexpression is actually
						//a function call

						//if the last token was a left paren, then this
						//was a function with no params; otherwise, we
						//have an additional parameter (on top of the ones we counter
						//while encountering commas)
						if (last_token.type != TokenType.LeftParen) {
							function_stack.peek().parameter_count++;
						}

						expression_RPN.add(operator_stack.pop());
						function_stack.pop();
					}

				}

				//record this as the last token we saw; well use it
				//to figure out if the minuses are unary or not
				last_token = next_token;

			}

			//no more tokens; pop all operators onto the output queue
			while (operator_stack.size > 0) {
				expression_RPN.add(operator_stack.pop());
			}

			//if the output queue is empty, then this is not an expression
			if (expression_RPN.size == 0) {
				throw new ParseException("Error parsing expression: no expression found!");
			}

			//we now have this in more easly parsed RPN form;
			//time to build the expression tree
			Token first_token = expression_RPN.first();
			Array<Expression> evaluation_stack = new Array<Parser.Expression>();
			while (expression_RPN.size > 0) {

				Token next = expression_RPN.removeIndex(0);
				if (Operator.isOperator(next.type)) {

					//this is an operation

					OperatorInfo info = Operator.infoForOperator(next.type);
					if (evaluation_stack.size < info.arguments) {
						throw ParseException.make(next,
								"Error parsing expression: not enough " + "arguments for operator " + next.type.name());
					}

					Array<Expression> params = new Array<Parser.Expression>();
					for (int i = 0; i < info.arguments; i++) {
						params.add(evaluation_stack.pop());
					}
					params.reverse();

					FunctionInfo operator_function = p.library.getFunction(next.type.name());

					Expression exp = new Expression(parent, operator_function, params, p);

					evaluation_stack.add(exp);

				} else if (next.type == TokenType.Identifier) {
					//thhis is a function call

					FunctionInfo info = null;

					//if we are a lib, use it to check if the
					//number of parameters proveded is correct
					if (p.library != null) {
						info = p.library.getFunction(next.value);

						//ensure that this call has the right number of params;
						if (!info.isParamCountCorrect(next.parameter_count)) {
							String error = String.format("Error parsing expression: "
									+ "Unsupported number of parameters for function %1$s (expected %2$s, got %3$s)",
									next.value, info.getParamCount(), next.parameter_count);
							throw ParseException.make(next, error);
						}

					} else {

						//use a dummy FunctionInfo to represent info about
						//the fact that a function is called; note that
						//attempting to call this will fail
						info = new FunctionInfo(next.value, next.parameter_count);
					}

					Array<Expression> param_list = new Array<Parser.Expression>();
					for (int i = 0; i < next.parameter_count; i++) {
						param_list.add(evaluation_stack.pop());
					}

					param_list.reverse();

					Expression exp = new Expression(parent, info, param_list, p);

					evaluation_stack.add(exp);

				} else {
					//this is a raw value
					ValueNode v = new ValueNode(parent, next, p);
					Expression exp = new Expression(parent, v, p);
					evaluation_stack.add(exp);
				}

			}
			
			//we should now have a single expression in this stack, which is the root
			//of the expressions tree. if we have more than one, then we have a problem
			if (evaluation_stack.size != 1) {
				throw ParseException.make(first_token, "Error parsing expression (stack did not reduce correctly)");
			}

			//return it
			return evaluation_stack.pop();

		}

		/**
		 * used to determine weather shunting-yard algorithm should pop operators from
		 * the operator stack
		 */
		private static boolean shouldApplyPrecedence(TokenType o1, Array<Token> operator_stack) {
			if (operator_stack.size == 0)
				return false;

			if (!Operator.isOperator(o1))
				throw new ParseException("Internal error parsing expression");

			TokenType o2 = operator_stack.peek().type;

			if (Operator.isOperator(o2) == false)
				return false;

			OperatorInfo o1_info = Operator.infoForOperator(o1);
			OperatorInfo o2_info = Operator.infoForOperator(o2);

			if (o1_info.associativity == Operator.Associativity.Left && o1_info.precedence <= o2_info.precedence) {
				return true;
			}

			if (o1_info.associativity == Operator.Associativity.Right && o1_info.precedence < o2_info.precedence) {
				return true;
			}

			return false;

		}
	}

	// AssignmentStatements are things like <<set $foo = 1>>
	// AssignmentStatement = BeginCommand Set <variable> <operation> Expression EndCommand
	protected static class AssignmentStatement extends ParseNode {

		private String destination_variable;
		private Expression value_expression;
		private TokenType operation;

		protected AssignmentStatement(ParseNode parent, Parser p) {
			super(parent, p);
			p.expectSymbol(TokenType.BeginCommand);
			p.expectSymbol(TokenType.Set);
			destination_variable = p.expectSymbol(TokenType.Variable).value;
			operation = p.expectSymbol(validOperators()).type;
			value_expression = Expression.parse(this, p);
			p.expectSymbol(TokenType.EndCommand);
		}

		protected String getDestinationVariable() {
			return destination_variable;
		}

		protected Expression getValueExpression() {
			return value_expression;
		}

		protected TokenType getOperation() {
			return operation;
		}

		private static TokenType[] validOperators() {
			return new TokenType[] { TokenType.EqualToOrAssign, TokenType.AddAssign, TokenType.MinusAssign,
					TokenType.DivideAssign, TokenType.MultiplyAssign };
		}

		@Override
		public String printTree(int indent_level) {
			StringBuilder sb = new StringBuilder();
			sb.append(tab(indent_level, "Set:"));
			sb.append(tab(indent_level + 1, destination_variable));
			sb.append(tab(indent_level + 1, operation.toString()));
			sb.append(getValueExpression().printTree(indent_level + 1));
			return sb.toString();
		}

		protected static boolean canParse(Parser p) {
			return p.nextSymbolsAre(TokenType.BeginCommand, TokenType.Set);
		}

	}

	// Operators are used in expressions - things like + - / * != neq
	protected static class Operator extends ParseNode {

		private TokenType operator_type;

		protected enum Associativity {
			Left, // resolve leftmost operand first
			Right, // resolve rightmost operand first
			None // special-case (like "(", ")", ","
		}

		protected Operator(ParseNode parent, TokenType t, Parser p) {
			super(parent, p);
			operator_type = t;
		}

		protected Operator(ParseNode parent, Parser p) {
			super(parent, p);
			operator_type = p.expectSymbol(operatorTypes()).type;
		}

		@Override
		public String printTree(int indent_level) {
			return tab(indent_level, operator_type.name());
		}

		protected static OperatorInfo infoForOperator(TokenType op) {
			if (indexOf(operatorTypes(), op) == -1)
				throw new ParseException(op.name() + " is not a valid operator");

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

		protected static boolean isOperator(TokenType type) {
			return indexOf(operatorTypes(), type) != -1;
		}

		protected static TokenType[] operatorTypes() {
			TokenType[] t = new TokenType[] { 
					TokenType.Not,
					TokenType.UnaryMinus,

					TokenType.Add, 
					TokenType.Minus, 
					TokenType.Divide,
					TokenType.Multiply, 
					TokenType.Modulo,

					TokenType.EqualToOrAssign,
					TokenType.EqualTo, 
					TokenType.GreaterThan,
					TokenType.GreaterThanOrEqualTo,
					TokenType.LessThan,
					TokenType.LessThanOrEqualTo,
					TokenType.NotEqualTo,

					TokenType.And,
					TokenType.Or,

					TokenType.Xor };

			return t;
		}

		//info used during expression parsing
		protected static class OperatorInfo {
			protected Associativity associativity;
			protected int precedence;
			protected int arguments;

			protected OperatorInfo(Associativity associativity, int precedence, int arguments) {
				this.associativity = associativity;
				this.precedence = precedence;
				this.arguments = arguments;
			}
		}
	}

	public static int indexOf(Object[] array, Object value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value)
				return i;
		}
		return -1;
	}

}
