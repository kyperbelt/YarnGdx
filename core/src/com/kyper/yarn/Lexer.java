package com.kyper.yarn;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class Lexer {

	// single-line comments, If this is encountered at any point, the rest of the line is just skipped
	public static final String LINE_COMMENT = "//";
	public static final char FORWARD_SLASH = '/';

	public static final String LINE_SEPARATOR = "\n";

	//states consts 
	private static final String BASE = "base";
	private static final String DASH = "-";
	private static final String COMMAND = "command";
	private static final String LINK = "link";
	private static final String SHORTCUT = "shortcut";
	private static final String TAG = "tag";
	private static final String EXPRESSION = "expression";
	private static final String ASSIGNMENT = "assignment";
	private static final String OPTION = "option";
	private static final String OR = "or";
	private static final String DESTINATION = "destination";

	private static final Regex WHITESPACE = new Regex("\\s*");

	protected ObjectMap<String, LexerState> states;

	protected LexerState default_state;
	protected LexerState current_state;

	protected Array<IntBoolPair> indentation_stack;
	protected boolean should_track_next_indent;

	public Lexer() {
		createStates();
	}

	private void createStates() {
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
		String shortcut_option = SHORTCUT + DASH + OPTION;
		String shortcut_option_tag = shortcut_option + DASH + TAG;
		String command_or_expression = COMMAND + DASH + OR + DASH + EXPRESSION;
		String link_destination = LINK + DASH + DESTINATION;

		states = new ObjectMap<String, Lexer.LexerState>();

		states.put(BASE, new LexerState(patterns));
		states.get(BASE).addTransition(TokenType.BeginCommand, COMMAND, true);
		states.get(BASE).addTransition(TokenType.OptionStart, LINK, true);
		states.get(BASE).addTransition(TokenType.ShortcutOption, shortcut_option);
		states.get(BASE).addTransition(TokenType.TagMarker, TAG, true);
		states.get(BASE).addTextRule(TokenType.Text);

		states.put(TAG, new LexerState(patterns));
		states.get(TAG).addTransition(TokenType.Identifier, BASE);

		states.put(shortcut_option, new LexerState(patterns));
		states.get(shortcut_option).track_next_indentation = true;
		states.get(shortcut_option).addTransition(TokenType.BeginCommand, EXPRESSION, true);
		states.get(shortcut_option).addTransition(TokenType.TagMarker, shortcut_option_tag, true);
		states.get(shortcut_option).addTextRule(TokenType.Text, BASE);

		states.put(shortcut_option_tag, new LexerState(patterns));
		states.get(shortcut_option_tag).addTransition(TokenType.Identifier, shortcut_option);

		states.put(COMMAND, new LexerState(patterns));
		states.get(COMMAND).addTransition(TokenType.If, EXPRESSION);
		states.get(COMMAND).addTransition(TokenType.Else);
		states.get(COMMAND).addTransition(TokenType.ElseIf, EXPRESSION);
		states.get(COMMAND).addTransition(TokenType.EndIf);
		states.get(COMMAND).addTransition(TokenType.Set, ASSIGNMENT);
		states.get(COMMAND).addTransition(TokenType.EndCommand, BASE, true);
		states.get(COMMAND).addTransition(TokenType.Identifier, command_or_expression);
		states.get(COMMAND).addTextRule(TokenType.Text);

		states.put(command_or_expression, new LexerState(patterns));
		states.get(command_or_expression).addTransition(TokenType.LeftParen, EXPRESSION);
		states.get(command_or_expression).addTransition(TokenType.EndCommand, BASE, true);
		states.get(command_or_expression).addTextRule(TokenType.Text);

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
		states.get(LINK).addTransition(TokenType.OptionDelimit, link_destination, true);
		states.get(LINK).addTextRule(TokenType.Text);

		states.put(link_destination, new LexerState(patterns));
		states.get(link_destination).addTransition(TokenType.Identifier);
		states.get(link_destination).addTransition(TokenType.OptionEnd, BASE);

		default_state = states.get(BASE);

		for (Entry<String, LexerState> entry : states) {
			entry.value.name = entry.key;
		}

	}

	public TokenList tokenise(String text) {

		//setup
		indentation_stack = new Array<Lexer.IntBoolPair>();
		indentation_stack.add(new IntBoolPair(0, false));
		should_track_next_indent = false;

		TokenList tokens = new TokenList();

		current_state = default_state;

		//parse each line
		Array<String> lines = new Array<String>(text.split(LINE_SEPARATOR));
		//blank line to ensure 0 indentation end
		lines.add("");

		int line_number = 1;
		
		for (String line : lines) {
			tokens.addAll(tokeniseLine(line, line_number));
			line_number++;
		}
		
		Token end_of_input = new Token(TokenType.EndOfInput, current_state,line_number,0);
		//tokens.insert(0,end_of_input);
		tokens.add(end_of_input);
		//=====================================
		//TODO: TO REVERSE INPUT PLEAS EUSE THIS
		//tokens.reverse();
		//TODO: REVERSE TOKENS 
		//====================================
		

		return tokens;
	}

	public TokenList tokeniseLine(String line, int line_number) {
		Array<Token> line_tokens_stack = new Array<Token>();

		//replace tabs with four spaces
		line = line.replaceAll("\t", "    ");

		//strip out \r's
		line = line.replaceAll("\r", "");

		//record the indentation level if previous state wants us to
		int this_indentation = lineIndentation(line);
		IntBoolPair previous_indentation = indentation_stack.peek();
		
		if (should_track_next_indent && this_indentation > previous_indentation.key) {
			//if we are more indented than before, emit an
			//indent token and record this indent level
			indentation_stack.add(new IntBoolPair(this_indentation, true));

			Token indent = new Token(TokenType.Indent, current_state, line_number, previous_indentation.key);
			indent.value = padLeft("", this_indentation - previous_indentation.key);
			

			should_track_next_indent = false;

			line_tokens_stack.add(indent);
			
		} else if (this_indentation < previous_indentation.key) {

			//if we are less indented, emit a dedent for every 
			//indentation level that we passed on the way back to 0 that also
			//emitted an indentation token.
			//at the same time, remove those indent levels from the stack

			while (this_indentation < indentation_stack.peek().key) {
				
				IntBoolPair top_level = indentation_stack.pop();

				if (top_level.value) {
					Token dedent = new Token(TokenType.Dedent, current_state, line_number, 0);
					line_tokens_stack.add(dedent);
				}
			}
		}

		//now we start finding tokens
		int column_number = this_indentation;
		
		Regex whitespace = WHITESPACE;
		
		while (column_number < line.length()) {

			//if we are about to hit a line comment, abort processing line
			//asap
			if (line.substring(column_number).startsWith(LINE_COMMENT))
				break;

			boolean matched = false;

			for (TokenRule rule : current_state.token_rules) {

				Matcher myMatch = rule.altRegex.match(line,column_number);
				Matcher match = rule.regex.match(line);
				
				if (!myMatch.find(0))
					continue;
				String token_text;

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

					int text_start_index = this_indentation;

					if (line_tokens_stack.size > 0) {
						while (line_tokens_stack.peek().type == TokenType.Identifier) {
							line_tokens_stack.pop();
						}

						Token start_delimiter_token = line_tokens_stack.peek();
						text_start_index = start_delimiter_token.column_number;
						if (start_delimiter_token.type == TokenType.Indent)
							text_start_index += start_delimiter_token.value.length();
						if (start_delimiter_token.type == TokenType.Dedent)
							text_start_index = this_indentation;
					}

					column_number = text_start_index;
					

					match.find(0);

					//TODO: ====
					//THIS IS PROBABLY WRONG
					int text_end_index = match.start()+match.group().length();
					token_text = line.substring(text_start_index,text_end_index);

				}else {
					token_text = myMatch.group();
					
				}
				
				column_number+=token_text.length();
				
				
				
				//if this was a string, lop off the quotes at the start and end
				//and un-escape the quotes and slashes
				if(rule.type == TokenType.Str) {
					
					token_text = token_text.substring(1, token_text.length()-1);
					token_text = token_text.replaceAll("\\\\", "\\");
					token_text = token_text.replaceAll("\\\"", "\"");
				}
				
				//System.out.println("line:"+line_number+" col:"+column_number+" text:"+token_text);
				
				Token token = new Token(rule.type, current_state,line_number,column_number,token_text);
				token.delimits_text = rule.delimits_text;
				
				line_tokens_stack.add(token);
				
				if(rule.enter_state!=null) {
					if(!states.containsKey(rule.enter_state))
						throw new TokeniserException(line_number, column_number, "Unkown tokeniser state "+rule.enter_state);
					
					
					enterState(states.get(rule.enter_state));
					
					if(should_track_next_indent == true) {
						if(indentation_stack.peek().key < this_indentation) {
							indentation_stack.add(new IntBoolPair(this_indentation, false));
						}
					}
				}
				
				matched = true;
				break;
			}
			
			
			if(!matched) {
				throw TokeniserException.expectedTokens(line_number, column_number, current_state);
			}
			
			Matcher last_white_space = whitespace.match(line);
			if(last_white_space.find(column_number)) {
				column_number=last_white_space.end();
			}
		}
		
		TokenList list_to_return = new TokenList(line_tokens_stack);
		//list_to_return.reverse();
		
		return list_to_return;
	}

	Regex initial_indent_regex;

	private int lineIndentation(String line) {
		if (initial_indent_regex == null)
			initial_indent_regex = new Regex("^(\\s*)");
		Matcher match = initial_indent_regex.match(line);
		
		if (!match.find() || match.group(0) == null)
			return 0;
		return match.group(0).length();
	}

	private void enterState(LexerState state) {
		current_state = state;
		if (current_state.track_next_indentation)
			should_track_next_indent = true;
	}

	protected static class TokeniserException extends IllegalStateException {
		private static final long serialVersionUID = 337269479504244415L;

		public int line_number;
		public int column_number;

		public TokeniserException(String message) {
			super(message);
		}

		public TokeniserException(int line_number, int column_number, String message) {
			super(String.format("%1$s : %2$s : %3$s", line_number, column_number, message));
			this.line_number = line_number;
			this.column_number = column_number;
		}
		
		public static TokeniserException expectedTokens(int line_number,int column_number,LexerState state) {
			Array<String> names = new Array<String>();
			for (TokenRule rule : state.token_rules) {
				names.add(rule.type.name());
			}
			
			String name_list;
			
			if(names.size > 1) {
				String last_item = names.pop();
				name_list = String.join(", ", names);
				name_list+= ", or "+last_item;
			}else {
				name_list = names.first();
			}
			
			String message = String.format("Expected %s", name_list);
			
			return new TokeniserException(line_number,column_number,message);
		}

	}

	/**
	 *  
	 */
	public class TokenList extends Array<Token> {

		public TokenList() {
			super();
		}

		public TokenList(Token[] tokens) {
			super(tokens);
		}

		public TokenList(Array<Token> tokens) {
			super(tokens);
		}
	}

	protected enum TokenType {

		// Special tokens
		Whitespace, Indent, Dedent, EndOfLine, EndOfInput,

		// Numbers. Everybody loves a number
		Number,

		// Strings. Everybody also loves a string
		Str,

		// '#'
		TagMarker,

		// Command syntax ("<<foo>>")
		BeginCommand, EndCommand,

		// Variables ("$foo")
		Variable,

		// Shortcut syntax ("->")
		ShortcutOption,

		// Option syntax ("[[Let's go here|Destination]]")
		OptionStart, // [[
		OptionDelimit, // |
		OptionEnd, // ]]

		// Command types (specially recognised command word)
		If, ElseIf, Else, EndIf, Set,

		// Boolean values
		True, False,

		// The null value
		Null,

		// Parentheses
		LeftParen, RightParen,

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

	protected class Token {

		//the token itself
		public TokenType type;
		public String value; //optional

		//where we found this token
		public int line_number;
		public int column_number;
		public String context;

		public boolean delimits_text = false;

		//if this is a function in an expression
		//this is the number of parameters
		public int parameter_count;

		//the state that the lexer was in when the token was emitted
		public String lexer_state;

		public Token(TokenType type, LexerState lexer_state, int line_number, int column_number, String value) {
			this.type = type;
			this.lexer_state = lexer_state.name;
			this.line_number = line_number;
			this.column_number = column_number;
			this.value = value;
		}

		public Token(TokenType type, LexerState lexer_state, int line_number, int column_number) {
			this(type, lexer_state, line_number, column_number, null);
		}

		public Token(TokenType type, LexerState lexer_state, int line_number) {
			this(type, lexer_state, line_number, -1);
		}

		public Token(TokenType type, LexerState lexer_state) {
			this(type, lexer_state, -1);
		}

		@Override
		public String toString() {
			if (value != null) {
				 return String.format("%1$s (%2$s) at %3$s:%4$s (state: %5$s)", type.name(), value.toString(), line_number, column_number, lexer_state);
			}
			return String.format("%1$s at %2$s :%3$s (state: %4$s)", type, line_number, column_number, lexer_state);
		}

	}

	protected class LexerState {

		public String name;
		private ObjectMap<TokenType, String> patterns;
		public Array<TokenRule> token_rules;
		public boolean track_next_indentation;

		public LexerState(ObjectMap<TokenType, String> patterns) {

			this.patterns = patterns;
			token_rules = new Array<Lexer.TokenRule>();
			track_next_indentation = false;

		}

		public TokenRule addTransition(TokenType type, String enter_state, boolean delimits_text) {

			String pattern = String.format("\\G%1$s", patterns.get(type));
			TokenRule rule = new TokenRule(type, new Regex(pattern), enter_state, delimits_text);
			token_rules.add(rule);
			return rule;
		}

		public TokenRule addTransition(TokenType type, String enter_state) {
			return addTransition(type, enter_state, false);
		}

		public TokenRule addTransition(TokenType type) {
			return addTransition(type, null);
		}

		/**
		 * a text rule matches everything that it possibly can, up to Any of the rules
		 * that already exist.
		 * 
		 * @return {@link TokenRule} - text rule
		 */
		public TokenRule addTextRule(TokenType type, String enter_state) {
			if (containsTextRule()) {
				throw new IllegalStateException("State already contains a text rule");
			}
			Array<String> delimiter_rules = new Array<String>();

			for (TokenRule other_rule : token_rules) {
				if (other_rule.delimits_text)
					delimiter_rules.add(String.format("(%1$s)", other_rule.regex.toString().substring(2)));
			}

			//create a regex that matches all text up to but not including
			//any of the delimiter rules

			String pattern = String.format("\\G((?!%1$s).)*", String.join("|", delimiter_rules));

			TokenRule rule = addTransition(type, enter_state);
			rule.regex = new Regex(pattern);
			rule.is_text_rule = true;

			return rule;

		}

		public TokenRule addTextRule(TokenType type) {
			return addTextRule(type, null);
		}

		public boolean containsTextRule() {
			for (TokenRule rule : token_rules) {
				if (rule.is_text_rule)
					return true;
			}
			return false;
		}

	}

	protected class TokenRule {
		public Regex regex = null;
		public Regex altRegex = null;
		
		//set to null if should stay in same state
		public String enter_state;
		public TokenType type;
		public boolean is_text_rule = false;
		public boolean delimits_text = false;

		public TokenRule(TokenType type, Regex regex, String enter_state, boolean delimits_text) {
			this.regex = regex;
			this.enter_state = enter_state;
			this.type = type;
			this.delimits_text = delimits_text;
			altRegex = new Regex(regex.pattern.toString().replace("\\G","^"));
		}

		@Override
		public String toString() {
			return String.format("[TokenRule: %s - %s ]", type, this.regex);
		}
	}

	protected static class Regex {

		private Pattern pattern;
		private StringBuilder stringBuilder;

		public Regex(String pattern) {
			this.pattern = Pattern.compile(pattern);
			this.stringBuilder = new StringBuilder();
		}

		public Regex(Pattern pattern) {
			this.pattern = pattern;
		}

		public void setPattern(String pattern) {
			this.pattern = Pattern.compile(pattern);
		}

		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}

		public Pattern getPattern() {
			return pattern;
		}

		@Override
		public String toString() {
			return pattern.toString();
		}

		public Matcher match(CharSequence text) {
			return pattern.matcher(text);
		}

		public Matcher match(String text, int begin_index) {
			stringBuilder.setLength(0);
			stringBuilder.append(text, begin_index, text.length());
			return match(stringBuilder);
		}

		public Matcher match(String text, int begin_index, int end_index) {
			stringBuilder.setLength(0);
			stringBuilder.append(text, begin_index, end_index);
			return match(stringBuilder);
		}
	}

	public class IntBoolPair extends Entry<Integer, Boolean> {
		public IntBoolPair(int key, boolean value) {
			this.key = key;
			this.value = value;
		}
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

}
