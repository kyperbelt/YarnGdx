// Generated from grammarFork/YarnSpinnerLexer.g4 by ANTLR 4.7.1
package com.kyper.yarn.compiler;

import java.util.LinkedList;
import java.util.Stack;

import static org.antlr.v4.runtime.Token.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

import com.kyper.yarn.StringUtils;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class YarnSpinnerLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INDENT=1, DEDENT=2, WS=3, NEWLINE=4, ID=5, BODY_START=6, HEADER_DELIMITER=7, 
		HASHTAG=8, REST_OF_LINE=9, HEADER_NEWLINE=10, COMMENT=11, BODY_WS=12, 
		BODY_NEWLINE=13, BODY_COMMENT=14, BODY_END=15, SHORTCUT_ARROW=16, COMMAND_START=17, 
		OPTION_START=18, FORMAT_FUNCTION_START=19, BODY_HASHTAG=20, TEXT_NEWLINE=21, 
		TEXT_HASHTAG=22, TEXT_EXPRESSION_START=23, TEXT_COMMAND_START=24, TEXT_FORMAT_FUNCTION_START=25, 
		TEXT_COMMENT=26, TEXT=27, TEXT_FRAG=28, TEXT_COMMANDHASHTAG_WS=29, TEXT_COMMANDHASHTAG_COMMENT=30, 
		TEXT_COMMANDHASHTAG_COMMAND_START=31, TEXT_COMMANDHASHTAG_HASHTAG=32, 
		TEXT_COMMANDHASHTAG_NEWLINE=33, TEXT_COMMANDHASHTAG_ERROR=34, HASHTAG_WS=35, 
		HASHTAG_TAG=36, HASHTAG_TEXT=37, FORMAT_FUNCTION_WS=38, FORMAT_FUNCTION_ID=39, 
		FORMAT_FUNCTION_NUMBER=40, FORMAT_FUNCTION_EXPRESSION_START=41, FORMAT_FUNCTION_EQUALS=42, 
		FORMAT_FUNCTION_STRING=43, FORMAT_FUNCTION_END=44, EXPR_WS=45, KEYWORD_TRUE=46, 
		KEYWORD_FALSE=47, KEYWORD_NULL=48, OPERATOR_ASSIGNMENT=49, OPERATOR_LOGICAL_LESS_THAN_EQUALS=50, 
		OPERATOR_LOGICAL_GREATER_THAN_EQUALS=51, OPERATOR_LOGICAL_EQUALS=52, OPERATOR_LOGICAL_LESS=53, 
		OPERATOR_LOGICAL_GREATER=54, OPERATOR_LOGICAL_NOT_EQUALS=55, OPERATOR_LOGICAL_AND=56, 
		OPERATOR_LOGICAL_OR=57, OPERATOR_LOGICAL_XOR=58, OPERATOR_LOGICAL_NOT=59, 
		OPERATOR_MATHS_ADDITION_EQUALS=60, OPERATOR_MATHS_SUBTRACTION_EQUALS=61, 
		OPERATOR_MATHS_MULTIPLICATION_EQUALS=62, OPERATOR_MATHS_MODULUS_EQUALS=63, 
		OPERATOR_MATHS_DIVISION_EQUALS=64, OPERATOR_MATHS_ADDITION=65, OPERATOR_MATHS_SUBTRACTION=66, 
		OPERATOR_MATHS_MULTIPLICATION=67, OPERATOR_MATHS_DIVISION=68, OPERATOR_MATHS_MODULUS=69, 
		LPAREN=70, RPAREN=71, COMMA=72, STRING=73, FUNC_ID=74, EXPRESSION_END=75, 
		EXPRESSION_COMMAND_END=76, VAR_ID=77, NUMBER=78, COMMAND_WS=79, COMMAND_IF=80, 
		COMMAND_ELSEIF=81, COMMAND_ELSE=82, COMMAND_SET=83, COMMAND_ENDIF=84, 
		COMMAND_CALL=85, COMMAND_END=86, COMMAND_TEXT_END=87, COMMAND_EXPRESSION_START=88, 
		COMMAND_TEXT=89, OPTION_NEWLINE=90, OPTION_WS=91, OPTION_END=92, OPTION_DELIMIT=93, 
		OPTION_EXPRESSION_START=94, OPTION_FORMAT_FUNCTION_START=95, OPTION_TEXT=96, 
		OPTION_ID_WS=97, OPTION_ID=98;
	public static final int
		HeaderMode=1, BodyMode=2, TextMode=3, TextCommandOrHashtagMode=4, HashtagMode=5, 
		FormatFunctionMode=6, ExpressionMode=7, CommandMode=8, CommandTextMode=9, 
		OptionMode=10, OptionIDMode=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "HeaderMode", "BodyMode", "TextMode", "TextCommandOrHashtagMode", 
		"HashtagMode", "FormatFunctionMode", "ExpressionMode", "CommandMode", 
		"CommandTextMode", "OptionMode", "OptionIDMode"
	};

	public static final String[] ruleNames = {
		"WS", "SPACES", "NEWLINE", "ID", "NODE_ID", "BODY_START", "HEADER_DELIMITER", 
		"HASHTAG", "REST_OF_LINE", "HEADER_NEWLINE", "COMMENT", "BODY_WS", "BODY_NEWLINE", 
		"BODY_COMMENT", "BODY_END", "SHORTCUT_ARROW", "COMMAND_START", "OPTION_START", 
		"FORMAT_FUNCTION_START", "BODY_HASHTAG", "BODY_EXPRESSION_FUNCTION_START", 
		"BODY_FORMAT_FUNCTION_START", "ANY", "TEXT_NEWLINE", "TEXT_HASHTAG", "TEXT_EXPRESSION_START", 
		"TEXT_COMMAND_START", "TEXT_FORMAT_FUNCTION_START", "TEXT_COMMENT", "TEXT", 
		"TEXT_FRAG", "TEXT_COMMANDHASHTAG_WS", "TEXT_COMMANDHASHTAG_COMMENT", 
		"TEXT_COMMANDHASHTAG_COMMAND_START", "TEXT_COMMANDHASHTAG_HASHTAG", "TEXT_COMMANDHASHTAG_NEWLINE", 
		"TEXT_COMMANDHASHTAG_ERROR", "HASHTAG_WS", "HASHTAG_TAG", "HASHTAG_TEXT", 
		"FORMAT_FUNCTION_WS", "FORMAT_FUNCTION_ID", "FORMAT_FUNCTION_NUMBER", 
		"FORMAT_FUNCTION_EXPRESSION_START", "FORMAT_FUNCTION_EQUALS", "FORMAT_FUNCTION_MARKER", 
		"FORMAT_FUNCTION_STRING", "FORMAT_FUNCTION_END", "EXPR_WS", "KEYWORD_TRUE", 
		"KEYWORD_FALSE", "KEYWORD_NULL", "OPERATOR_ASSIGNMENT", "OPERATOR_LOGICAL_LESS_THAN_EQUALS", 
		"OPERATOR_LOGICAL_GREATER_THAN_EQUALS", "OPERATOR_LOGICAL_EQUALS", "OPERATOR_LOGICAL_LESS", 
		"OPERATOR_LOGICAL_GREATER", "OPERATOR_LOGICAL_NOT_EQUALS", "OPERATOR_LOGICAL_AND", 
		"OPERATOR_LOGICAL_OR", "OPERATOR_LOGICAL_XOR", "OPERATOR_LOGICAL_NOT", 
		"OPERATOR_MATHS_ADDITION_EQUALS", "OPERATOR_MATHS_SUBTRACTION_EQUALS", 
		"OPERATOR_MATHS_MULTIPLICATION_EQUALS", "OPERATOR_MATHS_MODULUS_EQUALS", 
		"OPERATOR_MATHS_DIVISION_EQUALS", "OPERATOR_MATHS_ADDITION", "OPERATOR_MATHS_SUBTRACTION", 
		"OPERATOR_MATHS_MULTIPLICATION", "OPERATOR_MATHS_DIVISION", "OPERATOR_MATHS_MODULUS", 
		"LPAREN", "RPAREN", "COMMA", "STRING", "FUNC_ID", "EXPRESSION_END", "EXPRESSION_COMMAND_END", 
		"VAR_ID", "NUMBER", "INT", "DIGIT", "COMMAND_WS", "COMMAND_IF", "COMMAND_ELSEIF", 
		"COMMAND_ELSE", "COMMAND_SET", "COMMAND_ENDIF", "COMMAND_CALL", "COMMAND_END", 
		"COMMAND_ARBITRARY", "COMMAND_TEXT_END", "COMMAND_EXPRESSION_START", "COMMAND_TEXT", 
		"OPTION_NEWLINE", "OPTION_WS", "OPTION_END", "OPTION_DELIMIT", "OPTION_EXPRESSION_START", 
		"OPTION_FORMAT_FUNCTION_START", "OPTION_TEXT", "OPTION_ID_WS", "OPTION_ID"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, null, null, "'---'", null, null, null, null, null, 
		null, null, null, "'==='", "'->'", null, "'[['", null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, "'='", null, "']'", null, "'true'", 
		"'false'", "'null'", null, null, null, null, null, null, null, null, null, 
		null, null, "'+='", "'-='", "'*='", "'%='", "'/='", "'+'", "'-'", "'*'", 
		"'/'", "'%'", "'('", "')'", "','", null, null, "'}'", null, null, null, 
		null, null, null, null, null, "'endif'", null, null, null, null, null, 
		null, null, "']]'", "'|'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "INDENT", "DEDENT", "WS", "NEWLINE", "ID", "BODY_START", "HEADER_DELIMITER", 
		"HASHTAG", "REST_OF_LINE", "HEADER_NEWLINE", "COMMENT", "BODY_WS", "BODY_NEWLINE", 
		"BODY_COMMENT", "BODY_END", "SHORTCUT_ARROW", "COMMAND_START", "OPTION_START", 
		"FORMAT_FUNCTION_START", "BODY_HASHTAG", "TEXT_NEWLINE", "TEXT_HASHTAG", 
		"TEXT_EXPRESSION_START", "TEXT_COMMAND_START", "TEXT_FORMAT_FUNCTION_START", 
		"TEXT_COMMENT", "TEXT", "TEXT_FRAG", "TEXT_COMMANDHASHTAG_WS", "TEXT_COMMANDHASHTAG_COMMENT", 
		"TEXT_COMMANDHASHTAG_COMMAND_START", "TEXT_COMMANDHASHTAG_HASHTAG", "TEXT_COMMANDHASHTAG_NEWLINE", 
		"TEXT_COMMANDHASHTAG_ERROR", "HASHTAG_WS", "HASHTAG_TAG", "HASHTAG_TEXT", 
		"FORMAT_FUNCTION_WS", "FORMAT_FUNCTION_ID", "FORMAT_FUNCTION_NUMBER", 
		"FORMAT_FUNCTION_EXPRESSION_START", "FORMAT_FUNCTION_EQUALS", "FORMAT_FUNCTION_STRING", 
		"FORMAT_FUNCTION_END", "EXPR_WS", "KEYWORD_TRUE", "KEYWORD_FALSE", "KEYWORD_NULL", 
		"OPERATOR_ASSIGNMENT", "OPERATOR_LOGICAL_LESS_THAN_EQUALS", "OPERATOR_LOGICAL_GREATER_THAN_EQUALS", 
		"OPERATOR_LOGICAL_EQUALS", "OPERATOR_LOGICAL_LESS", "OPERATOR_LOGICAL_GREATER", 
		"OPERATOR_LOGICAL_NOT_EQUALS", "OPERATOR_LOGICAL_AND", "OPERATOR_LOGICAL_OR", 
		"OPERATOR_LOGICAL_XOR", "OPERATOR_LOGICAL_NOT", "OPERATOR_MATHS_ADDITION_EQUALS", 
		"OPERATOR_MATHS_SUBTRACTION_EQUALS", "OPERATOR_MATHS_MULTIPLICATION_EQUALS", 
		"OPERATOR_MATHS_MODULUS_EQUALS", "OPERATOR_MATHS_DIVISION_EQUALS", "OPERATOR_MATHS_ADDITION", 
		"OPERATOR_MATHS_SUBTRACTION", "OPERATOR_MATHS_MULTIPLICATION", "OPERATOR_MATHS_DIVISION", 
		"OPERATOR_MATHS_MODULUS", "LPAREN", "RPAREN", "COMMA", "STRING", "FUNC_ID", 
		"EXPRESSION_END", "EXPRESSION_COMMAND_END", "VAR_ID", "NUMBER", "COMMAND_WS", 
		"COMMAND_IF", "COMMAND_ELSEIF", "COMMAND_ELSE", "COMMAND_SET", "COMMAND_ENDIF", 
		"COMMAND_CALL", "COMMAND_END", "COMMAND_TEXT_END", "COMMAND_EXPRESSION_START", 
		"COMMAND_TEXT", "OPTION_NEWLINE", "OPTION_WS", "OPTION_END", "OPTION_DELIMIT", 
		"OPTION_EXPRESSION_START", "OPTION_FORMAT_FUNCTION_START", "OPTION_TEXT", 
		"OPTION_ID_WS", "OPTION_ID"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	    // A queue where extra tokens are pushed on (see the NEWLINE lexer rule).
	    private LinkedList<Token> Tokens = new LinkedList<Token>();
	    // The stack that keeps track of the indentation level.
	    private Stack<Integer> Indents = new Stack<Integer>();
	    // The amount of opened braces, brackets and parenthesis.
	    private int Opened = 0;
	    // The most recently produced token.
	    private Token LastToken = null;

	    @Override
	    public void emit(Token token) {
	        super.setToken(token);
	        Tokens.addLast(token);
	    }

	    private CommonToken commonToken(int type, String text) {
	        int stop = getCharIndex() - 1;
	        int start = text.length() == 0 ? stop : stop - text.length() + 1;
	        Pair tokenFactorySourcePair = new Pair(this, getInputStream());
	        return new CommonToken(tokenFactorySourcePair, type, DEFAULT_CHANNEL, start, stop);
	    }

	    private Token createDedent() {
	        CommonToken dedent = commonToken(YarnSpinnerParser.DEDENT, "");
	        dedent.setLine(LastToken.getLine());
	        return dedent;
	    }

	    @Override
	    public Token nextToken() {
	        // Check if the end-of-file is ahead and there are still some DEDENTS expected.
	        if (getInputStream().LA(1) == IntStream.EOF && Indents.size() != 0) {
	            // Remove any trailing EOF tokens from our buffer.
	            for (Token node = Tokens.getFirst(); node != null; ) {
	//	                var temp = node.Next;
	                Token temp = node.getTokenSource().nextToken();
	                if (node.getType() == Token.EOF) {
	                    Tokens.remove(node);
	                }
	                node = temp;
	            }

	            // First emit an extra line break that serves as the end of the statement.
	            this.emit(commonToken(YarnSpinnerParser.NEWLINE, "\n"));

	            // Now emit as much DEDENT tokens as needed.
	            while (Indents.size() != 0) {
	                emit(createDedent());
	                Indents.pop();
	            }

	            // Put the EOF back on the token stream.
	            emit(commonToken(YarnSpinnerParser.EOF, "<EOF>"));
	        }

	        Token next = super.nextToken();
	        if (next.getChannel() == DEFAULT_CHANNEL) {
	            // Keep track of the last token on the default channel.
	            LastToken = next;
	        }

	        if (Tokens.size() == 0) {
	            return next;
	        } else {
	            Token x = Tokens.getFirst();
	            Tokens.removeFirst();
	            return x;
	        }
	    }

	    // Calculates the indentation of the provided spaces, taking the
	    // following rules into account:
	    //
	    // "Tabs are replaced (from left to right) by one to eight spaces
	    //  such that the total number of characters up to and including
	    //  the replacement is a multiple of eight [...]"
	    //
	    //  -- https://docs.python.org/3.1/reference/lexical_analysis.html#indentation
	    static int getIndentationCount(String spaces) {
	        int count = 0;
	        for (char ch : spaces.toCharArray()) {
	            count += ch == '\t' ? 8 - (count % 8) : 1;
	        }
	        return count;
	    }

	    boolean atStartOfInput() {
	//	        return Column == 0 && Line == 1;
	        return getCharPositionInLine() == 0 && getLine() == 1;
	    }

	    void createIndentIfNeeded() {
	        createIndentIfNeeded(YarnSpinnerParser.NEWLINE);
	    }

	    void createIndentIfNeeded(int type) {

	        String newLine = getText().replaceAll("[\r\n\f]+", "");//StringUtils.replaceLast(getText(),"[^\r\n\f]+", "");
	        String spaces = getText().replaceAll("[(\r\n|\n|\r)\f]+", "");
	        // Strip newlines inside open clauses except if we are near EOF. We keep NEWLINEs near EOF to
	        // satisfy the final newline needed by the single_put rule used by the REPL.
	        int next = getInputStream().LA(1);
	        int nextnext = getInputStream().LA(2);
	        
	        // '-1' indicates 'do not emit the newline here but do emit indents/dedents'
	        if (type != -1) {
	        	
	            emit(commonToken(type, newLine));
	        }
	        int indent = getIndentationCount(spaces);
	        int previous = Indents.size() == 0 ? 0 : Indents.peek();
	        if (indent == previous) {
	            // skip indents of the same size as the present indent-size
	        } else if (indent > previous) {
	            Indents.push(indent);

	           
	            emit(commonToken(YarnSpinnerParser.INDENT, spaces));
	            
	        } else {
	            // Possibly emit more than 1 DEDENT token.
	            while (Indents.size() != 0 && Indents.peek() > indent) {
	            	
	                this.emit(createDedent());
	                Indents.pop();
	            }
	        }
	    }


	public YarnSpinnerLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "YarnSpinnerLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 2:
			NEWLINE_action((RuleContext)_localctx, actionIndex);
			break;
		case 9:
			HEADER_NEWLINE_action((RuleContext)_localctx, actionIndex);
			break;
		case 12:
			BODY_NEWLINE_action((RuleContext)_localctx, actionIndex);
			break;
		case 23:
			TEXT_NEWLINE_action((RuleContext)_localctx, actionIndex);
			break;
		case 35:
			TEXT_COMMANDHASHTAG_NEWLINE_action((RuleContext)_localctx, actionIndex);
			break;
		case 96:
			OPTION_NEWLINE_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void NEWLINE_action(RuleContext _localctx, int actionIndex) {
		
		switch (actionIndex) {
		case 0:
			 createIndentIfNeeded(-1); 
			break;
		}
	}
	private void HEADER_NEWLINE_action(RuleContext _localctx, int actionIndex) {

		
		switch (actionIndex) {
		case 1:
			createIndentIfNeeded(HEADER_NEWLINE);
			break;
		}
	}
	private void BODY_NEWLINE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			createIndentIfNeeded(-1);
			break;
		}
	}
	private void TEXT_NEWLINE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:
			createIndentIfNeeded(TEXT_NEWLINE);
			break;
		}
	}
	private void TEXT_COMMANDHASHTAG_NEWLINE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:
			createIndentIfNeeded(TEXT_COMMANDHASHTAG_NEWLINE);
			break;
		}
	}
	private void OPTION_NEWLINE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:
			createIndentIfNeeded(OPTION_NEWLINE);
			break;
		}
	}
	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 30:
			return TEXT_FRAG_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean TEXT_FRAG_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return 
		      !(getInputStream().LA(1) == '<' && getInputStream().LA(2) == '<') // start-of-command marker
		    &&!(getInputStream().LA(1) == '/' && getInputStream().LA(2) == '/') // start of a comment
		    ;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2d\u02f3\b\1\b\1\b"+
		"\1\b\1\b\1\b\1\b\1\b\1\b\1\b\1\b\1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5"+
		"\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16"+
		"\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25"+
		"\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34"+
		"\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4"+
		"%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4,\t,\4-\t-\4.\t.\4/\t/\4\60"+
		"\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t\64\4\65\t\65\4\66\t\66\4\67"+
		"\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=\4>\t>\4?\t?\4@\t@\4A\tA\4B\t"+
		"B\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4"+
		"N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT\4U\tU\4V\tV\4W\tW\4X\tX\4Y\t"+
		"Y\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4`\t`\4a\ta\4b\tb\4c\tc\4d\td"+
		"\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\3\2\6\2\u00e2\n\2\r\2\16\2\u00e3"+
		"\3\2\3\2\3\3\6\3\u00e9\n\3\r\3\16\3\u00ea\3\4\6\4\u00ee\n\4\r\4\16\4\u00ef"+
		"\3\4\5\4\u00f3\n\4\3\4\3\4\3\4\3\4\3\5\3\5\7\5\u00fb\n\5\f\5\16\5\u00fe"+
		"\13\5\3\6\3\6\7\6\u0102\n\6\f\6\16\6\u0105\13\6\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\7\b\u010f\n\b\f\b\16\b\u0112\13\b\3\b\3\b\3\t\3\t\3\t\3\t\3"+
		"\n\6\n\u011b\n\n\r\n\16\n\u011c\3\13\3\13\5\13\u0121\n\13\3\13\3\13\3"+
		"\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\5\16\u0134"+
		"\n\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23"+
		"\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31"+
		"\3\31\5\31\u016e\n\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33"+
		"\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36"+
		"\3\36\3\36\3\36\3\37\6\37\u018c\n\37\r\37\16\37\u018d\3 \3 \3 \3!\3!\3"+
		"!\3!\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3$\3$\3$\3$\3%\3%\5%\u01a6\n%\3%\3"+
		"%\3%\3%\3&\3&\3\'\3\'\3\'\3\'\3(\3(\3)\6)\u01b5\n)\r)\16)\u01b6\3)\3)"+
		"\3*\3*\3*\3*\3+\3+\3,\3,\3-\3-\3-\3-\3.\3.\3/\3/\3\60\3\60\3\60\3\60\3"+
		"\60\5\60\u01d0\n\60\7\60\u01d2\n\60\f\60\16\60\u01d5\13\60\3\60\3\60\3"+
		"\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\63\3\64\3"+
		"\64\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\5\66\u01f4"+
		"\n\66\3\67\3\67\3\67\3\67\3\67\5\67\u01fb\n\67\38\38\38\38\38\58\u0202"+
		"\n8\39\39\39\39\39\39\59\u020a\n9\3:\3:\3:\5:\u020f\n:\3;\3;\3;\5;\u0214"+
		"\n;\3<\3<\3<\3<\3<\5<\u021b\n<\3=\3=\3=\3=\3=\5=\u0222\n=\3>\3>\3>\3>"+
		"\5>\u0228\n>\3?\3?\3?\3?\5?\u022e\n?\3@\3@\3@\3@\5@\u0234\n@\3A\3A\3A"+
		"\3B\3B\3B\3C\3C\3C\3D\3D\3D\3E\3E\3E\3F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K"+
		"\3K\3L\3L\3M\3M\3N\3N\3N\3N\7N\u0259\nN\fN\16N\u025c\13N\3N\3N\3O\3O\3"+
		"P\3P\3P\3P\3Q\3Q\3Q\3Q\3Q\3Q\3R\3R\3R\3S\3S\3S\3S\3S\5S\u0274\nS\3T\6"+
		"T\u0277\nT\rT\16T\u0278\3U\3U\3V\3V\3V\3V\3W\3W\3W\3W\3W\3W\3W\3X\3X\3"+
		"X\3X\3X\3X\3X\3X\3X\3X\3X\3Y\3Y\3Y\3Y\3Y\3Y\5Y\u0299\nY\3Z\3Z\3Z\3Z\3"+
		"Z\3Z\3Z\3Z\3[\3[\3[\3[\3[\3[\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3]\3"+
		"]\3]\3]\3]\3^\3^\3^\3^\3^\3_\3_\3_\3_\3_\3`\3`\3`\3`\3a\6a\u02c6\na\r"+
		"a\16a\u02c7\3b\3b\5b\u02cc\nb\3b\3b\3b\3b\3c\3c\3c\3c\3d\3d\3d\3d\3d\3"+
		"e\3e\3e\3e\3f\3f\3f\3f\3g\3g\3g\3g\3h\6h\u02e8\nh\rh\16h\u02e9\3i\3i\3"+
		"i\3i\3j\3j\3j\3j\2\2k\16\5\20\2\22\6\24\7\26\2\30\b\32\t\34\n\36\13 \f"+
		"\"\r$\16&\17(\20*\21,\22.\23\60\24\62\25\64\26\66\28\2:\2<\27>\30@\31"+
		"B\32D\33F\34H\35J\36L\37N P!R\"T#V$X%Z&\\\'^(`)b*d+f,h\2j-l.n/p\60r\61"+
		"t\62v\63x\64z\65|\66~\67\u00808\u00829\u0084:\u0086;\u0088<\u008a=\u008c"+
		">\u008e?\u0090@\u0092A\u0094B\u0096C\u0098D\u009aE\u009cF\u009eG\u00a0"+
		"H\u00a2I\u00a4J\u00a6K\u00a8L\u00aaM\u00acN\u00aeO\u00b0P\u00b2\2\u00b4"+
		"\2\u00b6Q\u00b8R\u00baS\u00bcT\u00beU\u00c0V\u00c2W\u00c4X\u00c6\2\u00c8"+
		"Y\u00caZ\u00cc[\u00ce\\\u00d0]\u00d2^\u00d4_\u00d6`\u00d8a\u00dab\u00dc"+
		"c\u00ded\16\2\3\4\5\6\7\b\t\n\13\f\r\20\4\2\13\13\"\"\4\2\f\f\17\17\5"+
		"\2C\\aac|\6\2\62;C\\aac|\7\2\60\60\62;C\\aac|\3\2\"\"\7\2\f\f\17\17%%"+
		"]]}}\7\2\13\f\17\17\"\"%&>>\6\2\f\f\17\17$$^^\4\2$$^^\3\2\62;\f\2\13\17"+
		"\"\"\u0087\u0087\u00a2\u00a2\u1682\u1682\u2002\u200c\u202a\u202b\u2031"+
		"\u2031\u2061\u2061\u3002\u3002\4\2@@}}\5\2]]__}~\2\u0307\2\16\3\2\2\2"+
		"\2\22\3\2\2\2\2\24\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\3\36"+
		"\3\2\2\2\3 \3\2\2\2\3\"\3\2\2\2\4$\3\2\2\2\4&\3\2\2\2\4(\3\2\2\2\4*\3"+
		"\2\2\2\4,\3\2\2\2\4.\3\2\2\2\4\60\3\2\2\2\4\62\3\2\2\2\4\64\3\2\2\2\4"+
		"\66\3\2\2\2\48\3\2\2\2\4:\3\2\2\2\5<\3\2\2\2\5>\3\2\2\2\5@\3\2\2\2\5B"+
		"\3\2\2\2\5D\3\2\2\2\5F\3\2\2\2\5H\3\2\2\2\5J\3\2\2\2\6L\3\2\2\2\6N\3\2"+
		"\2\2\6P\3\2\2\2\6R\3\2\2\2\6T\3\2\2\2\6V\3\2\2\2\7X\3\2\2\2\7Z\3\2\2\2"+
		"\7\\\3\2\2\2\b^\3\2\2\2\b`\3\2\2\2\bb\3\2\2\2\bd\3\2\2\2\bf\3\2\2\2\b"+
		"j\3\2\2\2\bl\3\2\2\2\tn\3\2\2\2\tp\3\2\2\2\tr\3\2\2\2\tt\3\2\2\2\tv\3"+
		"\2\2\2\tx\3\2\2\2\tz\3\2\2\2\t|\3\2\2\2\t~\3\2\2\2\t\u0080\3\2\2\2\t\u0082"+
		"\3\2\2\2\t\u0084\3\2\2\2\t\u0086\3\2\2\2\t\u0088\3\2\2\2\t\u008a\3\2\2"+
		"\2\t\u008c\3\2\2\2\t\u008e\3\2\2\2\t\u0090\3\2\2\2\t\u0092\3\2\2\2\t\u0094"+
		"\3\2\2\2\t\u0096\3\2\2\2\t\u0098\3\2\2\2\t\u009a\3\2\2\2\t\u009c\3\2\2"+
		"\2\t\u009e\3\2\2\2\t\u00a0\3\2\2\2\t\u00a2\3\2\2\2\t\u00a4\3\2\2\2\t\u00a6"+
		"\3\2\2\2\t\u00a8\3\2\2\2\t\u00aa\3\2\2\2\t\u00ac\3\2\2\2\t\u00ae\3\2\2"+
		"\2\t\u00b0\3\2\2\2\n\u00b6\3\2\2\2\n\u00b8\3\2\2\2\n\u00ba\3\2\2\2\n\u00bc"+
		"\3\2\2\2\n\u00be\3\2\2\2\n\u00c0\3\2\2\2\n\u00c2\3\2\2\2\n\u00c4\3\2\2"+
		"\2\n\u00c6\3\2\2\2\13\u00c8\3\2\2\2\13\u00ca\3\2\2\2\13\u00cc\3\2\2\2"+
		"\f\u00ce\3\2\2\2\f\u00d0\3\2\2\2\f\u00d2\3\2\2\2\f\u00d4\3\2\2\2\f\u00d6"+
		"\3\2\2\2\f\u00d8\3\2\2\2\f\u00da\3\2\2\2\r\u00dc\3\2\2\2\r\u00de\3\2\2"+
		"\2\16\u00e1\3\2\2\2\20\u00e8\3\2\2\2\22\u00ed\3\2\2\2\24\u00f8\3\2\2\2"+
		"\26\u00ff\3\2\2\2\30\u0106\3\2\2\2\32\u010c\3\2\2\2\34\u0115\3\2\2\2\36"+
		"\u011a\3\2\2\2 \u011e\3\2\2\2\"\u0126\3\2\2\2$\u012d\3\2\2\2&\u0131\3"+
		"\2\2\2(\u0139\3\2\2\2*\u013d\3\2\2\2,\u0143\3\2\2\2.\u0146\3\2\2\2\60"+
		"\u014b\3\2\2\2\62\u0150\3\2\2\2\64\u0155\3\2\2\2\66\u015a\3\2\2\28\u0160"+
		"\3\2\2\2:\u0166\3\2\2\2<\u016b\3\2\2\2>\u0173\3\2\2\2@\u0178\3\2\2\2B"+
		"\u017c\3\2\2\2D\u0182\3\2\2\2F\u0186\3\2\2\2H\u018b\3\2\2\2J\u018f\3\2"+
		"\2\2L\u0192\3\2\2\2N\u0196\3\2\2\2P\u019a\3\2\2\2R\u019f\3\2\2\2T\u01a3"+
		"\3\2\2\2V\u01ab\3\2\2\2X\u01ad\3\2\2\2Z\u01b1\3\2\2\2\\\u01b4\3\2\2\2"+
		"^\u01ba\3\2\2\2`\u01be\3\2\2\2b\u01c0\3\2\2\2d\u01c2\3\2\2\2f\u01c6\3"+
		"\2\2\2h\u01c8\3\2\2\2j\u01ca\3\2\2\2l\u01d8\3\2\2\2n\u01dc\3\2\2\2p\u01e0"+
		"\3\2\2\2r\u01e5\3\2\2\2t\u01eb\3\2\2\2v\u01f3\3\2\2\2x\u01fa\3\2\2\2z"+
		"\u0201\3\2\2\2|\u0209\3\2\2\2~\u020e\3\2\2\2\u0080\u0213\3\2\2\2\u0082"+
		"\u021a\3\2\2\2\u0084\u0221\3\2\2\2\u0086\u0227\3\2\2\2\u0088\u022d\3\2"+
		"\2\2\u008a\u0233\3\2\2\2\u008c\u0235\3\2\2\2\u008e\u0238\3\2\2\2\u0090"+
		"\u023b\3\2\2\2\u0092\u023e\3\2\2\2\u0094\u0241\3\2\2\2\u0096\u0244\3\2"+
		"\2\2\u0098\u0246\3\2\2\2\u009a\u0248\3\2\2\2\u009c\u024a\3\2\2\2\u009e"+
		"\u024c\3\2\2\2\u00a0\u024e\3\2\2\2\u00a2\u0250\3\2\2\2\u00a4\u0252\3\2"+
		"\2\2\u00a6\u0254\3\2\2\2\u00a8\u025f\3\2\2\2\u00aa\u0261\3\2\2\2\u00ac"+
		"\u0265\3\2\2\2\u00ae\u026b\3\2\2\2\u00b0\u0273\3\2\2\2\u00b2\u0276\3\2"+
		"\2\2\u00b4\u027a\3\2\2\2\u00b6\u027c\3\2\2\2\u00b8\u0280\3\2\2\2\u00ba"+
		"\u0287\3\2\2\2\u00bc\u0292\3\2\2\2\u00be\u029a\3\2\2\2\u00c0\u02a2\3\2"+
		"\2\2\u00c2\u02a8\3\2\2\2\u00c4\u02b1\3\2\2\2\u00c6\u02b6\3\2\2\2\u00c8"+
		"\u02bb\3\2\2\2\u00ca\u02c0\3\2\2\2\u00cc\u02c5\3\2\2\2\u00ce\u02c9\3\2"+
		"\2\2\u00d0\u02d1\3\2\2\2\u00d2\u02d5\3\2\2\2\u00d4\u02da\3\2\2\2\u00d6"+
		"\u02de\3\2\2\2\u00d8\u02e2\3\2\2\2\u00da\u02e7\3\2\2\2\u00dc\u02eb\3\2"+
		"\2\2\u00de\u02ef\3\2\2\2\u00e0\u00e2\t\2\2\2\u00e1\u00e0\3\2\2\2\u00e2"+
		"\u00e3\3\2\2\2\u00e3\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e5\3\2"+
		"\2\2\u00e5\u00e6\b\2\2\2\u00e6\17\3\2\2\2\u00e7\u00e9\t\2\2\2\u00e8\u00e7"+
		"\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb"+
		"\21\3\2\2\2\u00ec\u00ee\t\3\2\2\u00ed\u00ec\3\2\2\2\u00ee\u00ef\3\2\2"+
		"\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f2\3\2\2\2\u00f1\u00f3"+
		"\5\20\3\2\u00f2\u00f1\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\3\2\2\2"+
		"\u00f4\u00f5\b\4\3\2\u00f5\u00f6\3\2\2\2\u00f6\u00f7\b\4\2\2\u00f7\23"+
		"\3\2\2\2\u00f8\u00fc\t\4\2\2\u00f9\u00fb\t\5\2\2\u00fa\u00f9\3\2\2\2\u00fb"+
		"\u00fe\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\25\3\2\2"+
		"\2\u00fe\u00fc\3\2\2\2\u00ff\u0103\t\4\2\2\u0100\u0102\t\6\2\2\u0101\u0100"+
		"\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104"+
		"\27\3\2\2\2\u0105\u0103\3\2\2\2\u0106\u0107\7/\2\2\u0107\u0108\7/\2\2"+
		"\u0108\u0109\7/\2\2\u0109\u010a\3\2\2\2\u010a\u010b\b\7\4\2\u010b\31\3"+
		"\2\2\2\u010c\u0110\7<\2\2\u010d\u010f\t\7\2\2\u010e\u010d\3\2\2\2\u010f"+
		"\u0112\3\2\2\2\u0110\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0113\3\2"+
		"\2\2\u0112\u0110\3\2\2\2\u0113\u0114\b\b\5\2\u0114\33\3\2\2\2\u0115\u0116"+
		"\7%\2\2\u0116\u0117\3\2\2\2\u0117\u0118\b\t\6\2\u0118\35\3\2\2\2\u0119"+
		"\u011b\n\3\2\2\u011a\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c\u011a\3\2"+
		"\2\2\u011c\u011d\3\2\2\2\u011d\37\3\2\2\2\u011e\u0120\5\22\4\2\u011f\u0121"+
		"\5\20\3\2\u0120\u011f\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0122\3\2\2\2"+
		"\u0122\u0123\b\13\7\2\u0123\u0124\3\2\2\2\u0124\u0125\b\13\b\2\u0125!"+
		"\3\2\2\2\u0126\u0127\7\61\2\2\u0127\u0128\7\61\2\2\u0128\u0129\3\2\2\2"+
		"\u0129\u012a\5\36\n\2\u012a\u012b\3\2\2\2\u012b\u012c\b\f\2\2\u012c#\3"+
		"\2\2\2\u012d\u012e\5\16\2\2\u012e\u012f\3\2\2\2\u012f\u0130\b\r\2\2\u0130"+
		"%\3\2\2\2\u0131\u0133\5\22\4\2\u0132\u0134\5\20\3\2\u0133\u0132\3\2\2"+
		"\2\u0133\u0134\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0136\b\16\t\2\u0136"+
		"\u0137\3\2\2\2\u0137\u0138\b\16\2\2\u0138\'\3\2\2\2\u0139\u013a\5\"\f"+
		"\2\u013a\u013b\3\2\2\2\u013b\u013c\b\17\2\2\u013c)\3\2\2\2\u013d\u013e"+
		"\7?\2\2\u013e\u013f\7?\2\2\u013f\u0140\7?\2\2\u0140\u0141\3\2\2\2\u0141"+
		"\u0142\b\20\b\2\u0142+\3\2\2\2\u0143\u0144\7/\2\2\u0144\u0145\7@\2\2\u0145"+
		"-\3\2\2\2\u0146\u0147\7>\2\2\u0147\u0148\7>\2\2\u0148\u0149\3\2\2\2\u0149"+
		"\u014a\b\22\n\2\u014a/\3\2\2\2\u014b\u014c\7]\2\2\u014c\u014d\7]\2\2\u014d"+
		"\u014e\3\2\2\2\u014e\u014f\b\23\13\2\u014f\61\3\2\2\2\u0150\u0151\7]\2"+
		"\2\u0151\u0152\3\2\2\2\u0152\u0153\b\24\f\2\u0153\u0154\b\24\r\2\u0154"+
		"\63\3\2\2\2\u0155\u0156\7%\2\2\u0156\u0157\3\2\2\2\u0157\u0158\b\25\16"+
		"\2\u0158\u0159\b\25\6\2\u0159\65\3\2\2\2\u015a\u015b\7}\2\2\u015b\u015c"+
		"\3\2\2\2\u015c\u015d\b\26\17\2\u015d\u015e\b\26\f\2\u015e\u015f\b\26\20"+
		"\2\u015f\67\3\2\2\2\u0160\u0161\7]\2\2\u0161\u0162\3\2\2\2\u0162\u0163"+
		"\b\27\21\2\u0163\u0164\b\27\f\2\u0164\u0165\b\27\r\2\u01659\3\2\2\2\u0166"+
		"\u0167\13\2\2\2\u0167\u0168\3\2\2\2\u0168\u0169\b\30\22\2\u0169\u016a"+
		"\b\30\f\2\u016a;\3\2\2\2\u016b\u016d\5\22\4\2\u016c\u016e\5\20\3\2\u016d"+
		"\u016c\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0170\b\31"+
		"\23\2\u0170\u0171\3\2\2\2\u0171\u0172\b\31\b\2\u0172=\3\2\2\2\u0173\u0174"+
		"\5\34\t\2\u0174\u0175\3\2\2\2\u0175\u0176\b\32\24\2\u0176\u0177\b\32\6"+
		"\2\u0177?\3\2\2\2\u0178\u0179\7}\2\2\u0179\u017a\3\2\2\2\u017a\u017b\b"+
		"\33\20\2\u017bA\3\2\2\2\u017c\u017d\7>\2\2\u017d\u017e\7>\2\2\u017e\u017f"+
		"\3\2\2\2\u017f\u0180\b\34\24\2\u0180\u0181\b\34\n\2\u0181C\3\2\2\2\u0182"+
		"\u0183\7]\2\2\u0183\u0184\3\2\2\2\u0184\u0185\b\35\r\2\u0185E\3\2\2\2"+
		"\u0186\u0187\5\"\f\2\u0187\u0188\3\2\2\2\u0188\u0189\b\36\2\2\u0189G\3"+
		"\2\2\2\u018a\u018c\5J \2\u018b\u018a\3\2\2\2\u018c\u018d\3\2\2\2\u018d"+
		"\u018b\3\2\2\2\u018d\u018e\3\2\2\2\u018eI\3\2\2\2\u018f\u0190\6 \2\2\u0190"+
		"\u0191\n\b\2\2\u0191K\3\2\2\2\u0192\u0193\5\16\2\2\u0193\u0194\3\2\2\2"+
		"\u0194\u0195\b!\2\2\u0195M\3\2\2\2\u0196\u0197\5\"\f\2\u0197\u0198\3\2"+
		"\2\2\u0198\u0199\b\"\2\2\u0199O\3\2\2\2\u019a\u019b\7>\2\2\u019b\u019c"+
		"\7>\2\2\u019c\u019d\3\2\2\2\u019d\u019e\b#\n\2\u019eQ\3\2\2\2\u019f\u01a0"+
		"\7%\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a2\b$\6\2\u01a2S\3\2\2\2\u01a3\u01a5"+
		"\5\22\4\2\u01a4\u01a6\5\20\3\2\u01a5\u01a4\3\2\2\2\u01a5\u01a6\3\2\2\2"+
		"\u01a6\u01a7\3\2\2\2\u01a7\u01a8\b%\25\2\u01a8\u01a9\3\2\2\2\u01a9\u01aa"+
		"\b%\b\2\u01aaU\3\2\2\2\u01ab\u01ac\13\2\2\2\u01acW\3\2\2\2\u01ad\u01ae"+
		"\5\16\2\2\u01ae\u01af\3\2\2\2\u01af\u01b0\b\'\2\2\u01b0Y\3\2\2\2\u01b1"+
		"\u01b2\5\34\t\2\u01b2[\3\2\2\2\u01b3\u01b5\n\t\2\2\u01b4\u01b3\3\2\2\2"+
		"\u01b5\u01b6\3\2\2\2\u01b6\u01b4\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7\u01b8"+
		"\3\2\2\2\u01b8\u01b9\b)\b\2\u01b9]\3\2\2\2\u01ba\u01bb\5\16\2\2\u01bb"+
		"\u01bc\3\2\2\2\u01bc\u01bd\b*\2\2\u01bd_\3\2\2\2\u01be\u01bf\5\24\5\2"+
		"\u01bfa\3\2\2\2\u01c0\u01c1\5\u00b0S\2\u01c1c\3\2\2\2\u01c2\u01c3\7}\2"+
		"\2\u01c3\u01c4\3\2\2\2\u01c4\u01c5\b-\20\2\u01c5e\3\2\2\2\u01c6\u01c7"+
		"\7?\2\2\u01c7g\3\2\2\2\u01c8\u01c9\7\'\2\2\u01c9i\3\2\2\2\u01ca\u01d3"+
		"\7$\2\2\u01cb\u01d2\n\n\2\2\u01cc\u01cf\7^\2\2\u01cd\u01d0\t\13\2\2\u01ce"+
		"\u01d0\5h/\2\u01cf\u01cd\3\2\2\2\u01cf\u01ce\3\2\2\2\u01d0\u01d2\3\2\2"+
		"\2\u01d1\u01cb\3\2\2\2\u01d1\u01cc\3\2\2\2\u01d2\u01d5\3\2\2\2\u01d3\u01d1"+
		"\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4\u01d6\3\2\2\2\u01d5\u01d3\3\2\2\2\u01d6"+
		"\u01d7\7$\2\2\u01d7k\3\2\2\2\u01d8\u01d9\7_\2\2\u01d9\u01da\3\2\2\2\u01da"+
		"\u01db\b\61\b\2\u01dbm\3\2\2\2\u01dc\u01dd\5\16\2\2\u01dd\u01de\3\2\2"+
		"\2\u01de\u01df\b\62\2\2\u01dfo\3\2\2\2\u01e0\u01e1\7v\2\2\u01e1\u01e2"+
		"\7t\2\2\u01e2\u01e3\7w\2\2\u01e3\u01e4\7g\2\2\u01e4q\3\2\2\2\u01e5\u01e6"+
		"\7h\2\2\u01e6\u01e7\7c\2\2\u01e7\u01e8\7n\2\2\u01e8\u01e9\7u\2\2\u01e9"+
		"\u01ea\7g\2\2\u01eas\3\2\2\2\u01eb\u01ec\7p\2\2\u01ec\u01ed\7w\2\2\u01ed"+
		"\u01ee\7n\2\2\u01ee\u01ef\7n\2\2\u01efu\3\2\2\2\u01f0\u01f4\7?\2\2\u01f1"+
		"\u01f2\7v\2\2\u01f2\u01f4\7q\2\2\u01f3\u01f0\3\2\2\2\u01f3\u01f1\3\2\2"+
		"\2\u01f4w\3\2\2\2\u01f5\u01f6\7>\2\2\u01f6\u01fb\7?\2\2\u01f7\u01f8\7"+
		"n\2\2\u01f8\u01f9\7v\2\2\u01f9\u01fb\7g\2\2\u01fa\u01f5\3\2\2\2\u01fa"+
		"\u01f7\3\2\2\2\u01fby\3\2\2\2\u01fc\u01fd\7@\2\2\u01fd\u0202\7?\2\2\u01fe"+
		"\u01ff\7i\2\2\u01ff\u0200\7v\2\2\u0200\u0202\7g\2\2\u0201\u01fc\3\2\2"+
		"\2\u0201\u01fe\3\2\2\2\u0202{\3\2\2\2\u0203\u0204\7?\2\2\u0204\u020a\7"+
		"?\2\2\u0205\u0206\7k\2\2\u0206\u020a\7u\2\2\u0207\u0208\7g\2\2\u0208\u020a"+
		"\7s\2\2\u0209\u0203\3\2\2\2\u0209\u0205\3\2\2\2\u0209\u0207\3\2\2\2\u020a"+
		"}\3\2\2\2\u020b\u020f\7>\2\2\u020c\u020d\7n\2\2\u020d\u020f\7v\2\2\u020e"+
		"\u020b\3\2\2\2\u020e\u020c\3\2\2\2\u020f\177\3\2\2\2\u0210\u0214\7@\2"+
		"\2\u0211\u0212\7i\2\2\u0212\u0214\7v\2\2\u0213\u0210\3\2\2\2\u0213\u0211"+
		"\3\2\2\2\u0214\u0081\3\2\2\2\u0215\u0216\7#\2\2\u0216\u021b\7?\2\2\u0217"+
		"\u0218\7p\2\2\u0218\u0219\7g\2\2\u0219\u021b\7s\2\2\u021a\u0215\3\2\2"+
		"\2\u021a\u0217\3\2\2\2\u021b\u0083\3\2\2\2\u021c\u021d\7c\2\2\u021d\u021e"+
		"\7p\2\2\u021e\u0222\7f\2\2\u021f\u0220\7(\2\2\u0220\u0222\7(\2\2\u0221"+
		"\u021c\3\2\2\2\u0221\u021f\3\2\2\2\u0222\u0085\3\2\2\2\u0223\u0224\7q"+
		"\2\2\u0224\u0228\7t\2\2\u0225\u0226\7~\2\2\u0226\u0228\7~\2\2\u0227\u0223"+
		"\3\2\2\2\u0227\u0225\3\2\2\2\u0228\u0087\3\2\2\2\u0229\u022a\7z\2\2\u022a"+
		"\u022b\7q\2\2\u022b\u022e\7t\2\2\u022c\u022e\7`\2\2\u022d\u0229\3\2\2"+
		"\2\u022d\u022c\3\2\2\2\u022e\u0089\3\2\2\2\u022f\u0230\7p\2\2\u0230\u0231"+
		"\7q\2\2\u0231\u0234\7v\2\2\u0232\u0234\7#\2\2\u0233\u022f\3\2\2\2\u0233"+
		"\u0232\3\2\2\2\u0234\u008b\3\2\2\2\u0235\u0236\7-\2\2\u0236\u0237\7?\2"+
		"\2\u0237\u008d\3\2\2\2\u0238\u0239\7/\2\2\u0239\u023a\7?\2\2\u023a\u008f"+
		"\3\2\2\2\u023b\u023c\7,\2\2\u023c\u023d\7?\2\2\u023d\u0091\3\2\2\2\u023e"+
		"\u023f\7\'\2\2\u023f\u0240\7?\2\2\u0240\u0093\3\2\2\2\u0241\u0242\7\61"+
		"\2\2\u0242\u0243\7?\2\2\u0243\u0095\3\2\2\2\u0244\u0245\7-\2\2\u0245\u0097"+
		"\3\2\2\2\u0246\u0247\7/\2\2\u0247\u0099\3\2\2\2\u0248\u0249\7,\2\2\u0249"+
		"\u009b\3\2\2\2\u024a\u024b\7\61\2\2\u024b\u009d\3\2\2\2\u024c\u024d\7"+
		"\'\2\2\u024d\u009f\3\2\2\2\u024e\u024f\7*\2\2\u024f\u00a1\3\2\2\2\u0250"+
		"\u0251\7+\2\2\u0251\u00a3\3\2\2\2\u0252\u0253\7.\2\2\u0253\u00a5\3\2\2"+
		"\2\u0254\u025a\7$\2\2\u0255\u0259\n\n\2\2\u0256\u0257\7^\2\2\u0257\u0259"+
		"\t\13\2\2\u0258\u0255\3\2\2\2\u0258\u0256\3\2\2\2\u0259\u025c\3\2\2\2"+
		"\u025a\u0258\3\2\2\2\u025a\u025b\3\2\2\2\u025b\u025d\3\2\2\2\u025c\u025a"+
		"\3\2\2\2\u025d\u025e\7$\2\2\u025e\u00a7\3\2\2\2\u025f\u0260\5\24\5\2\u0260"+
		"\u00a9\3\2\2\2\u0261\u0262\7\177\2\2\u0262\u0263\3\2\2\2\u0263\u0264\b"+
		"P\b\2\u0264\u00ab\3\2\2\2\u0265\u0266\7@\2\2\u0266\u0267\7@\2\2\u0267"+
		"\u0268\3\2\2\2\u0268\u0269\bQ\b\2\u0269\u026a\bQ\b\2\u026a\u00ad\3\2\2"+
		"\2\u026b\u026c\7&\2\2\u026c\u026d\5\24\5\2\u026d\u00af\3\2\2\2\u026e\u0274"+
		"\5\u00b2T\2\u026f\u0270\5\u00b2T\2\u0270\u0271\7\60\2\2\u0271\u0272\5"+
		"\u00b2T\2\u0272\u0274\3\2\2\2\u0273\u026e\3\2\2\2\u0273\u026f\3\2\2\2"+
		"\u0274\u00b1\3\2\2\2\u0275\u0277\5\u00b4U\2\u0276\u0275\3\2\2\2\u0277"+
		"\u0278\3\2\2\2\u0278\u0276\3\2\2\2\u0278\u0279\3\2\2\2\u0279\u00b3\3\2"+
		"\2\2\u027a\u027b\t\f\2\2\u027b\u00b5\3\2\2\2\u027c\u027d\5\16\2\2\u027d"+
		"\u027e\3\2\2\2\u027e\u027f\bV\2\2\u027f\u00b7\3\2\2\2\u0280\u0281\7k\2"+
		"\2\u0281\u0282\7h\2\2\u0282\u0283\3\2\2\2\u0283\u0284\t\r\2\2\u0284\u0285"+
		"\3\2\2\2\u0285\u0286\bW\20\2\u0286\u00b9\3\2\2\2\u0287\u0288\7g\2\2\u0288"+
		"\u0289\7n\2\2\u0289\u028a\7u\2\2\u028a\u028b\7g\2\2\u028b\u028c\7k\2\2"+
		"\u028c\u028d\7h\2\2\u028d\u028e\3\2\2\2\u028e\u028f\t\r\2\2\u028f\u0290"+
		"\3\2\2\2\u0290\u0291\bX\20\2\u0291\u00bb\3\2\2\2\u0292\u0293\7g\2\2\u0293"+
		"\u0294\7n\2\2\u0294\u0295\7u\2\2\u0295\u0296\7g\2\2\u0296\u0298\3\2\2"+
		"\2\u0297\u0299\t\r\2\2\u0298\u0297\3\2\2\2\u0298\u0299\3\2\2\2\u0299\u00bd"+
		"\3\2\2\2\u029a\u029b\7u\2\2\u029b\u029c\7g\2\2\u029c\u029d\7v\2\2\u029d"+
		"\u029e\3\2\2\2\u029e\u029f\t\r\2\2\u029f\u02a0\3\2\2\2\u02a0\u02a1\bZ"+
		"\20\2\u02a1\u00bf\3\2\2\2\u02a2\u02a3\7g\2\2\u02a3\u02a4\7p\2\2\u02a4"+
		"\u02a5\7f\2\2\u02a5\u02a6\7k\2\2\u02a6\u02a7\7h\2\2\u02a7\u00c1\3\2\2"+
		"\2\u02a8\u02a9\7e\2\2\u02a9\u02aa\7c\2\2\u02aa\u02ab\7n\2\2\u02ab\u02ac"+
		"\7n\2\2\u02ac\u02ad\3\2\2\2\u02ad\u02ae\t\r\2\2\u02ae\u02af\3\2\2\2\u02af"+
		"\u02b0\b\\\20\2\u02b0\u00c3\3\2\2\2\u02b1\u02b2\7@\2\2\u02b2\u02b3\7@"+
		"\2\2\u02b3\u02b4\3\2\2\2\u02b4\u02b5\b]\b\2\u02b5\u00c5\3\2\2\2\u02b6"+
		"\u02b7\13\2\2\2\u02b7\u02b8\3\2\2\2\u02b8\u02b9\b^\26\2\u02b9\u02ba\b"+
		"^\27\2\u02ba\u00c7\3\2\2\2\u02bb\u02bc\7@\2\2\u02bc\u02bd\7@\2\2\u02bd"+
		"\u02be\3\2\2\2\u02be\u02bf\b_\b\2\u02bf\u00c9\3\2\2\2\u02c0\u02c1\7}\2"+
		"\2\u02c1\u02c2\3\2\2\2\u02c2\u02c3\b`\20\2\u02c3\u00cb\3\2\2\2\u02c4\u02c6"+
		"\n\16\2\2\u02c5\u02c4\3\2\2\2\u02c6\u02c7\3\2\2\2\u02c7\u02c5\3\2\2\2"+
		"\u02c7\u02c8\3\2\2\2\u02c8\u00cd\3\2\2\2\u02c9\u02cb\5\22\4\2\u02ca\u02cc"+
		"\5\20\3\2\u02cb\u02ca\3\2\2\2\u02cb\u02cc\3\2\2\2\u02cc\u02cd\3\2\2\2"+
		"\u02cd\u02ce\bb\30\2\u02ce\u02cf\3\2\2\2\u02cf\u02d0\bb\b\2\u02d0\u00cf"+
		"\3\2\2\2\u02d1\u02d2\5\16\2\2\u02d2\u02d3\3\2\2\2\u02d3\u02d4\bc\2\2\u02d4"+
		"\u00d1\3\2\2\2\u02d5\u02d6\7_\2\2\u02d6\u02d7\7_\2\2\u02d7\u02d8\3\2\2"+
		"\2\u02d8\u02d9\bd\b\2\u02d9\u00d3\3\2\2\2\u02da\u02db\7~\2\2\u02db\u02dc"+
		"\3\2\2\2\u02dc\u02dd\be\31\2\u02dd\u00d5\3\2\2\2\u02de\u02df\7}\2\2\u02df"+
		"\u02e0\3\2\2\2\u02e0\u02e1\bf\20\2\u02e1\u00d7\3\2\2\2\u02e2\u02e3\7]"+
		"\2\2\u02e3\u02e4\3\2\2\2\u02e4\u02e5\bg\r\2\u02e5\u00d9\3\2\2\2\u02e6"+
		"\u02e8\n\17\2\2\u02e7\u02e6\3\2\2\2\u02e8\u02e9\3\2\2\2\u02e9\u02e7\3"+
		"\2\2\2\u02e9\u02ea\3\2\2\2\u02ea\u00db\3\2\2\2\u02eb\u02ec\t\2\2\2\u02ec"+
		"\u02ed\3\2\2\2\u02ed\u02ee\bi\2\2\u02ee\u00dd\3\2\2\2\u02ef\u02f0\5\26"+
		"\6\2\u02f0\u02f1\3\2\2\2\u02f1\u02f2\bj\b\2\u02f2\u00df\3\2\2\2\64\2\3"+
		"\4\5\6\7\b\t\n\13\f\r\u00e3\u00ea\u00ef\u00f2\u00fa\u00fc\u0101\u0103"+
		"\u0110\u011c\u0120\u0133\u016d\u018d\u01a5\u01b6\u01cf\u01d1\u01d3\u01f3"+
		"\u01fa\u0201\u0209\u020e\u0213\u021a\u0221\u0227\u022d\u0233\u0258\u025a"+
		"\u0273\u0278\u0298\u02c7\u02cb\u02e9\32\b\2\2\3\4\2\7\4\2\7\3\2\7\7\2"+
		"\3\13\3\6\2\2\3\16\4\7\n\2\7\f\2\7\5\2\7\b\2\7\6\2\t\31\2\7\t\2\t\33\2"+
		"\5\2\2\3\31\5\4\6\2\3%\6\t[\2\4\13\2\3b\7\7\r\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}