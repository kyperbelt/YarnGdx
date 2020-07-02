// Generated from grammarFork/YarnSpinnerParser.g4 by ANTLR 4.7.1
package com.kyper.yarn.compiler;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class YarnSpinnerParser extends Parser {
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
		RULE_dialogue = 0, RULE_file_hashtag = 1, RULE_node = 2, RULE_header = 3, 
		RULE_body = 4, RULE_statement = 5, RULE_line_statement = 6, RULE_line_formatted_text = 7, 
		RULE_format_function = 8, RULE_key_value_pair = 9, RULE_hashtag = 10, 
		RULE_line_condition = 11, RULE_expression = 12, RULE_value = 13, RULE_variable = 14, 
		RULE_function = 15, RULE_if_statement = 16, RULE_if_clause = 17, RULE_else_if_clause = 18, 
		RULE_else_clause = 19, RULE_set_statement = 20, RULE_call_statement = 21, 
		RULE_command_statement = 22, RULE_command_formatted_text = 23, RULE_shortcut_option_statement = 24, 
		RULE_shortcut_option = 25, RULE_option_statement = 26, RULE_option_formatted_text = 27;
	public static final String[] ruleNames = {
		"dialogue", "file_hashtag", "node", "header", "body", "statement", "line_statement", 
		"line_formatted_text", "format_function", "key_value_pair", "hashtag", 
		"line_condition", "expression", "value", "variable", "function", "if_statement", 
		"if_clause", "else_if_clause", "else_clause", "set_statement", "call_statement", 
		"command_statement", "command_formatted_text", "shortcut_option_statement", 
		"shortcut_option", "option_statement", "option_formatted_text"
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

	@Override
	public String getGrammarFileName() { return "YarnSpinnerParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public YarnSpinnerParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DialogueContext extends ParserRuleContext {
		public List<NodeContext> node() {
			return getRuleContexts(NodeContext.class);
		}
		public NodeContext node(int i) {
			return getRuleContext(NodeContext.class,i);
		}
		public List<File_hashtagContext> file_hashtag() {
			return getRuleContexts(File_hashtagContext.class);
		}
		public File_hashtagContext file_hashtag(int i) {
			return getRuleContext(File_hashtagContext.class,i);
		}
		public DialogueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dialogue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterDialogue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitDialogue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitDialogue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DialogueContext dialogue() throws RecognitionException {
		DialogueContext _localctx = new DialogueContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_dialogue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==HASHTAG) {
				{
				{
				setState(56);
				file_hashtag();
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				node();
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class File_hashtagContext extends ParserRuleContext {
		public TerminalNode HASHTAG() { return getToken(YarnSpinnerParser.HASHTAG, 0); }
		public TerminalNode HASHTAG_TEXT() { return getToken(YarnSpinnerParser.HASHTAG_TEXT, 0); }
		public TerminalNode TEXT_COMMANDHASHTAG_NEWLINE() { return getToken(YarnSpinnerParser.TEXT_COMMANDHASHTAG_NEWLINE, 0); }
		public File_hashtagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_hashtag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterFile_hashtag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitFile_hashtag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitFile_hashtag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final File_hashtagContext file_hashtag() throws RecognitionException {
		File_hashtagContext _localctx = new File_hashtagContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_file_hashtag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(HASHTAG);
			setState(68);
			match(HASHTAG_TEXT);
			setState(69);
			match(TEXT_COMMANDHASHTAG_NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NodeContext extends ParserRuleContext {
		public TerminalNode BODY_START() { return getToken(YarnSpinnerParser.BODY_START, 0); }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public TerminalNode BODY_END() { return getToken(YarnSpinnerParser.BODY_END, 0); }
		public List<HeaderContext> header() {
			return getRuleContexts(HeaderContext.class);
		}
		public HeaderContext header(int i) {
			return getRuleContext(HeaderContext.class,i);
		}
		public NodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_node; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterNode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitNode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitNode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NodeContext node() throws RecognitionException {
		NodeContext _localctx = new NodeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_node);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(71);
				header();
				}
				}
				setState(74); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(76);
			match(BODY_START);
			setState(77);
			body();
			setState(78);
			match(BODY_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HeaderContext extends ParserRuleContext {
		public Token header_key;
		public Token header_value;
		public TerminalNode HEADER_DELIMITER() { return getToken(YarnSpinnerParser.HEADER_DELIMITER, 0); }
		public TerminalNode HEADER_NEWLINE() { return getToken(YarnSpinnerParser.HEADER_NEWLINE, 0); }
		public TerminalNode ID() { return getToken(YarnSpinnerParser.ID, 0); }
		public TerminalNode REST_OF_LINE() { return getToken(YarnSpinnerParser.REST_OF_LINE, 0); }
		public HeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeaderContext header() throws RecognitionException {
		HeaderContext _localctx = new HeaderContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_header);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			((HeaderContext)_localctx).header_key = match(ID);
			setState(81);
			match(HEADER_DELIMITER);
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==REST_OF_LINE) {
				{
				setState(82);
				((HeaderContext)_localctx).header_value = match(REST_OF_LINE);
				}
			}

			setState(85);
			match(HEADER_NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BodyContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BodyContext body() throws RecognitionException {
		BodyContext _localctx = new BodyContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INDENT) | (1L << HASHTAG) | (1L << SHORTCUT_ARROW) | (1L << COMMAND_START) | (1L << OPTION_START) | (1L << FORMAT_FUNCTION_START) | (1L << BODY_HASHTAG) | (1L << TEXT_NEWLINE) | (1L << TEXT_HASHTAG) | (1L << TEXT_EXPRESSION_START) | (1L << TEXT_COMMAND_START) | (1L << TEXT_FORMAT_FUNCTION_START) | (1L << TEXT) | (1L << TEXT_COMMANDHASHTAG_COMMAND_START) | (1L << TEXT_COMMANDHASHTAG_HASHTAG) | (1L << TEXT_COMMANDHASHTAG_NEWLINE) | (1L << HASHTAG_TAG))) != 0)) {
				{
				{
				setState(87);
				statement();
				}
				}
				setState(92);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public Line_statementContext line_statement() {
			return getRuleContext(Line_statementContext.class,0);
		}
		public If_statementContext if_statement() {
			return getRuleContext(If_statementContext.class,0);
		}
		public Set_statementContext set_statement() {
			return getRuleContext(Set_statementContext.class,0);
		}
		public Option_statementContext option_statement() {
			return getRuleContext(Option_statementContext.class,0);
		}
		public Shortcut_option_statementContext shortcut_option_statement() {
			return getRuleContext(Shortcut_option_statementContext.class,0);
		}
		public Call_statementContext call_statement() {
			return getRuleContext(Call_statementContext.class,0);
		}
		public Command_statementContext command_statement() {
			return getRuleContext(Command_statementContext.class,0);
		}
		public TerminalNode INDENT() { return getToken(YarnSpinnerParser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(YarnSpinnerParser.DEDENT, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_statement);
		int _la;
		try {
			setState(108);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(93);
				line_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(94);
				if_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(95);
				set_statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(96);
				option_statement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(97);
				shortcut_option_statement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(98);
				call_statement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(99);
				command_statement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(100);
				match(INDENT);
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INDENT) | (1L << HASHTAG) | (1L << SHORTCUT_ARROW) | (1L << COMMAND_START) | (1L << OPTION_START) | (1L << FORMAT_FUNCTION_START) | (1L << BODY_HASHTAG) | (1L << TEXT_NEWLINE) | (1L << TEXT_HASHTAG) | (1L << TEXT_EXPRESSION_START) | (1L << TEXT_COMMAND_START) | (1L << TEXT_FORMAT_FUNCTION_START) | (1L << TEXT) | (1L << TEXT_COMMANDHASHTAG_COMMAND_START) | (1L << TEXT_COMMANDHASHTAG_HASHTAG) | (1L << TEXT_COMMANDHASHTAG_NEWLINE) | (1L << HASHTAG_TAG))) != 0)) {
					{
					{
					setState(101);
					statement();
					}
					}
					setState(106);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(107);
				match(DEDENT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Line_statementContext extends ParserRuleContext {
		public Line_formatted_textContext line_formatted_text() {
			return getRuleContext(Line_formatted_textContext.class,0);
		}
		public TerminalNode TEXT_NEWLINE() { return getToken(YarnSpinnerParser.TEXT_NEWLINE, 0); }
		public TerminalNode TEXT_COMMANDHASHTAG_NEWLINE() { return getToken(YarnSpinnerParser.TEXT_COMMANDHASHTAG_NEWLINE, 0); }
		public Line_conditionContext line_condition() {
			return getRuleContext(Line_conditionContext.class,0);
		}
		public List<HashtagContext> hashtag() {
			return getRuleContexts(HashtagContext.class);
		}
		public HashtagContext hashtag(int i) {
			return getRuleContext(HashtagContext.class,i);
		}
		public Line_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterLine_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitLine_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitLine_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Line_statementContext line_statement() throws RecognitionException {
		Line_statementContext _localctx = new Line_statementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_line_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			line_formatted_text();
			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TEXT_COMMAND_START || _la==TEXT_COMMANDHASHTAG_COMMAND_START) {
				{
				setState(111);
				line_condition();
				}
			}

			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HASHTAG) | (1L << BODY_HASHTAG) | (1L << TEXT_HASHTAG) | (1L << TEXT_COMMANDHASHTAG_HASHTAG) | (1L << HASHTAG_TAG))) != 0)) {
				{
				{
				setState(114);
				hashtag();
				}
				}
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(120);
			_la = _input.LA(1);
			if ( !(_la==TEXT_NEWLINE || _la==TEXT_COMMANDHASHTAG_NEWLINE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Line_formatted_textContext extends ParserRuleContext {
		public List<TerminalNode> TEXT() { return getTokens(YarnSpinnerParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(YarnSpinnerParser.TEXT, i);
		}
		public List<TerminalNode> TEXT_EXPRESSION_START() { return getTokens(YarnSpinnerParser.TEXT_EXPRESSION_START); }
		public TerminalNode TEXT_EXPRESSION_START(int i) {
			return getToken(YarnSpinnerParser.TEXT_EXPRESSION_START, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> EXPRESSION_END() { return getTokens(YarnSpinnerParser.EXPRESSION_END); }
		public TerminalNode EXPRESSION_END(int i) {
			return getToken(YarnSpinnerParser.EXPRESSION_END, i);
		}
		public List<Format_functionContext> format_function() {
			return getRuleContexts(Format_functionContext.class);
		}
		public Format_functionContext format_function(int i) {
			return getRuleContext(Format_functionContext.class,i);
		}
		public List<TerminalNode> FORMAT_FUNCTION_END() { return getTokens(YarnSpinnerParser.FORMAT_FUNCTION_END); }
		public TerminalNode FORMAT_FUNCTION_END(int i) {
			return getToken(YarnSpinnerParser.FORMAT_FUNCTION_END, i);
		}
		public List<TerminalNode> FORMAT_FUNCTION_START() { return getTokens(YarnSpinnerParser.FORMAT_FUNCTION_START); }
		public TerminalNode FORMAT_FUNCTION_START(int i) {
			return getToken(YarnSpinnerParser.FORMAT_FUNCTION_START, i);
		}
		public List<TerminalNode> TEXT_FORMAT_FUNCTION_START() { return getTokens(YarnSpinnerParser.TEXT_FORMAT_FUNCTION_START); }
		public TerminalNode TEXT_FORMAT_FUNCTION_START(int i) {
			return getToken(YarnSpinnerParser.TEXT_FORMAT_FUNCTION_START, i);
		}
		public Line_formatted_textContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line_formatted_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterLine_formatted_text(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitLine_formatted_text(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitLine_formatted_text(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Line_formatted_textContext line_formatted_text() throws RecognitionException {
		Line_formatted_textContext _localctx = new Line_formatted_textContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_line_formatted_text);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FORMAT_FUNCTION_START) | (1L << TEXT_EXPRESSION_START) | (1L << TEXT_FORMAT_FUNCTION_START) | (1L << TEXT))) != 0)) {
				{
				setState(131);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case TEXT:
					{
					setState(122);
					match(TEXT);
					}
					break;
				case TEXT_EXPRESSION_START:
					{
					setState(123);
					match(TEXT_EXPRESSION_START);
					setState(124);
					expression(0);
					setState(125);
					match(EXPRESSION_END);
					}
					break;
				case FORMAT_FUNCTION_START:
				case TEXT_FORMAT_FUNCTION_START:
					{
					setState(127);
					_la = _input.LA(1);
					if ( !(_la==FORMAT_FUNCTION_START || _la==TEXT_FORMAT_FUNCTION_START) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(128);
					format_function();
					setState(129);
					match(FORMAT_FUNCTION_END);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Format_functionContext extends ParserRuleContext {
		public Token function_name;
		public TerminalNode FORMAT_FUNCTION_EXPRESSION_START() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_EXPRESSION_START, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode EXPRESSION_END() { return getToken(YarnSpinnerParser.EXPRESSION_END, 0); }
		public TerminalNode FORMAT_FUNCTION_ID() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_ID, 0); }
		public List<Key_value_pairContext> key_value_pair() {
			return getRuleContexts(Key_value_pairContext.class);
		}
		public Key_value_pairContext key_value_pair(int i) {
			return getRuleContext(Key_value_pairContext.class,i);
		}
		public Format_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_format_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterFormat_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitFormat_function(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitFormat_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Format_functionContext format_function() throws RecognitionException {
		Format_functionContext _localctx = new Format_functionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_format_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			((Format_functionContext)_localctx).function_name = match(FORMAT_FUNCTION_ID);
			setState(137);
			match(FORMAT_FUNCTION_EXPRESSION_START);
			setState(138);
			variable();
			setState(139);
			match(EXPRESSION_END);
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FORMAT_FUNCTION_ID || _la==FORMAT_FUNCTION_NUMBER) {
				{
				{
				setState(140);
				key_value_pair();
				}
				}
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Key_value_pairContext extends ParserRuleContext {
		public Key_value_pairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key_value_pair; }
	 
		public Key_value_pairContext() { }
		public void copyFrom(Key_value_pairContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class KeyValuePairNumberContext extends Key_value_pairContext {
		public Token pair_key;
		public Token pair_value;
		public TerminalNode FORMAT_FUNCTION_EQUALS() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_EQUALS, 0); }
		public TerminalNode FORMAT_FUNCTION_NUMBER() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_NUMBER, 0); }
		public TerminalNode FORMAT_FUNCTION_STRING() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_STRING, 0); }
		public KeyValuePairNumberContext(Key_value_pairContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterKeyValuePairNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitKeyValuePairNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitKeyValuePairNumber(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class KeyValuePairNamedContext extends Key_value_pairContext {
		public Token pair_key;
		public Token pair_value;
		public TerminalNode FORMAT_FUNCTION_EQUALS() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_EQUALS, 0); }
		public TerminalNode FORMAT_FUNCTION_ID() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_ID, 0); }
		public TerminalNode FORMAT_FUNCTION_STRING() { return getToken(YarnSpinnerParser.FORMAT_FUNCTION_STRING, 0); }
		public KeyValuePairNamedContext(Key_value_pairContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterKeyValuePairNamed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitKeyValuePairNamed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitKeyValuePairNamed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Key_value_pairContext key_value_pair() throws RecognitionException {
		Key_value_pairContext _localctx = new Key_value_pairContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_key_value_pair);
		try {
			setState(152);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FORMAT_FUNCTION_ID:
				_localctx = new KeyValuePairNamedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				((KeyValuePairNamedContext)_localctx).pair_key = match(FORMAT_FUNCTION_ID);
				setState(147);
				match(FORMAT_FUNCTION_EQUALS);
				setState(148);
				((KeyValuePairNamedContext)_localctx).pair_value = match(FORMAT_FUNCTION_STRING);
				}
				break;
			case FORMAT_FUNCTION_NUMBER:
				_localctx = new KeyValuePairNumberContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(149);
				((KeyValuePairNumberContext)_localctx).pair_key = match(FORMAT_FUNCTION_NUMBER);
				setState(150);
				match(FORMAT_FUNCTION_EQUALS);
				setState(151);
				((KeyValuePairNumberContext)_localctx).pair_value = match(FORMAT_FUNCTION_STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HashtagContext extends ParserRuleContext {
		public Token text;
		public TerminalNode TEXT_HASHTAG() { return getToken(YarnSpinnerParser.TEXT_HASHTAG, 0); }
		public TerminalNode TEXT_COMMANDHASHTAG_HASHTAG() { return getToken(YarnSpinnerParser.TEXT_COMMANDHASHTAG_HASHTAG, 0); }
		public TerminalNode HASHTAG_TAG() { return getToken(YarnSpinnerParser.HASHTAG_TAG, 0); }
		public TerminalNode BODY_HASHTAG() { return getToken(YarnSpinnerParser.BODY_HASHTAG, 0); }
		public TerminalNode HASHTAG() { return getToken(YarnSpinnerParser.HASHTAG, 0); }
		public TerminalNode HASHTAG_TEXT() { return getToken(YarnSpinnerParser.HASHTAG_TEXT, 0); }
		public HashtagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hashtag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterHashtag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitHashtag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitHashtag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HashtagContext hashtag() throws RecognitionException {
		HashtagContext _localctx = new HashtagContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_hashtag);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HASHTAG) | (1L << BODY_HASHTAG) | (1L << TEXT_HASHTAG) | (1L << TEXT_COMMANDHASHTAG_HASHTAG) | (1L << HASHTAG_TAG))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(155);
			((HashtagContext)_localctx).text = match(HASHTAG_TEXT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Line_conditionContext extends ParserRuleContext {
		public TerminalNode COMMAND_IF() { return getToken(YarnSpinnerParser.COMMAND_IF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXPRESSION_COMMAND_END() { return getToken(YarnSpinnerParser.EXPRESSION_COMMAND_END, 0); }
		public TerminalNode TEXT_COMMANDHASHTAG_COMMAND_START() { return getToken(YarnSpinnerParser.TEXT_COMMANDHASHTAG_COMMAND_START, 0); }
		public TerminalNode TEXT_COMMAND_START() { return getToken(YarnSpinnerParser.TEXT_COMMAND_START, 0); }
		public Line_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterLine_condition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitLine_condition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitLine_condition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Line_conditionContext line_condition() throws RecognitionException {
		Line_conditionContext _localctx = new Line_conditionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_line_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			_la = _input.LA(1);
			if ( !(_la==TEXT_COMMAND_START || _la==TEXT_COMMANDHASHTAG_COMMAND_START) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(158);
			match(COMMAND_IF);
			setState(159);
			expression(0);
			setState(160);
			match(EXPRESSION_COMMAND_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExpParensContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpParensContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpParens(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpParens(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpParens(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpMultDivModContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpMultDivModContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpMultDivMod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpMultDivMod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpMultDivMod(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpMultDivModEqualsContext extends ExpressionContext {
		public Token op;
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpMultDivModEqualsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpMultDivModEquals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpMultDivModEquals(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpMultDivModEquals(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpComparisonContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OPERATOR_LOGICAL_LESS_THAN_EQUALS() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_LESS_THAN_EQUALS, 0); }
		public TerminalNode OPERATOR_LOGICAL_GREATER_THAN_EQUALS() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_GREATER_THAN_EQUALS, 0); }
		public TerminalNode OPERATOR_LOGICAL_LESS() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_LESS, 0); }
		public TerminalNode OPERATOR_LOGICAL_GREATER() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_GREATER, 0); }
		public ExpComparisonContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpComparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpComparison(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpNegativeContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpNegativeContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpNegative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpNegative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpNegative(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpAndOrXorContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OPERATOR_LOGICAL_AND() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_AND, 0); }
		public TerminalNode OPERATOR_LOGICAL_OR() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_OR, 0); }
		public TerminalNode OPERATOR_LOGICAL_XOR() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_XOR, 0); }
		public ExpAndOrXorContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpAndOrXor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpAndOrXor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpAndOrXor(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpPlusMinusEqualsContext extends ExpressionContext {
		public Token op;
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpPlusMinusEqualsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpPlusMinusEquals(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpPlusMinusEquals(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpPlusMinusEquals(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpAddSubContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpAddSubContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpAddSub(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpAddSub(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpAddSub(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpNotContext extends ExpressionContext {
		public TerminalNode OPERATOR_LOGICAL_NOT() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_NOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpNotContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpNot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpNot(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpValueContext extends ExpressionContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ExpValueContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpEqualityContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OPERATOR_LOGICAL_EQUALS() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_EQUALS, 0); }
		public TerminalNode OPERATOR_LOGICAL_NOT_EQUALS() { return getToken(YarnSpinnerParser.OPERATOR_LOGICAL_NOT_EQUALS, 0); }
		public ExpEqualityContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterExpEquality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitExpEquality(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitExpEquality(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				_localctx = new ExpParensContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(163);
				match(LPAREN);
				setState(164);
				expression(0);
				setState(165);
				match(RPAREN);
				}
				break;
			case 2:
				{
				_localctx = new ExpNegativeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(167);
				match(OPERATOR_MATHS_SUBTRACTION);
				setState(168);
				expression(10);
				}
				break;
			case 3:
				{
				_localctx = new ExpNotContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(169);
				match(OPERATOR_LOGICAL_NOT);
				setState(170);
				expression(9);
				}
				break;
			case 4:
				{
				_localctx = new ExpMultDivModEqualsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(171);
				variable();
				setState(172);
				((ExpMultDivModEqualsContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & ((1L << (OPERATOR_MATHS_MULTIPLICATION_EQUALS - 62)) | (1L << (OPERATOR_MATHS_MODULUS_EQUALS - 62)) | (1L << (OPERATOR_MATHS_DIVISION_EQUALS - 62)))) != 0)) ) {
					((ExpMultDivModEqualsContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(173);
				expression(4);
				}
				break;
			case 5:
				{
				_localctx = new ExpPlusMinusEqualsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(175);
				variable();
				setState(176);
				((ExpPlusMinusEqualsContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==OPERATOR_MATHS_ADDITION_EQUALS || _la==OPERATOR_MATHS_SUBTRACTION_EQUALS) ) {
					((ExpPlusMinusEqualsContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(177);
				expression(3);
				}
				break;
			case 6:
				{
				_localctx = new ExpValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(179);
				value();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(199);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(197);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new ExpMultDivModContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(182);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(183);
						((ExpMultDivModContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (OPERATOR_MATHS_MULTIPLICATION - 67)) | (1L << (OPERATOR_MATHS_DIVISION - 67)) | (1L << (OPERATOR_MATHS_MODULUS - 67)))) != 0)) ) {
							((ExpMultDivModContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(184);
						expression(9);
						}
						break;
					case 2:
						{
						_localctx = new ExpAddSubContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(185);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(186);
						((ExpAddSubContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OPERATOR_MATHS_ADDITION || _la==OPERATOR_MATHS_SUBTRACTION) ) {
							((ExpAddSubContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(187);
						expression(8);
						}
						break;
					case 3:
						{
						_localctx = new ExpComparisonContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(188);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(189);
						((ExpComparisonContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPERATOR_LOGICAL_LESS_THAN_EQUALS) | (1L << OPERATOR_LOGICAL_GREATER_THAN_EQUALS) | (1L << OPERATOR_LOGICAL_LESS) | (1L << OPERATOR_LOGICAL_GREATER))) != 0)) ) {
							((ExpComparisonContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(190);
						expression(7);
						}
						break;
					case 4:
						{
						_localctx = new ExpEqualityContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(191);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(192);
						((ExpEqualityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OPERATOR_LOGICAL_EQUALS || _la==OPERATOR_LOGICAL_NOT_EQUALS) ) {
							((ExpEqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(193);
						expression(6);
						}
						break;
					case 5:
						{
						_localctx = new ExpAndOrXorContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(194);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(195);
						((ExpAndOrXorContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPERATOR_LOGICAL_AND) | (1L << OPERATOR_LOGICAL_OR) | (1L << OPERATOR_LOGICAL_XOR))) != 0)) ) {
							((ExpAndOrXorContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(196);
						expression(3);
						}
						break;
					}
					} 
				}
				setState(201);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
	 
		public ValueContext() { }
		public void copyFrom(ValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ValueNullContext extends ValueContext {
		public TerminalNode KEYWORD_NULL() { return getToken(YarnSpinnerParser.KEYWORD_NULL, 0); }
		public ValueNullContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterValueNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitValueNull(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitValueNull(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueNumberContext extends ValueContext {
		public TerminalNode NUMBER() { return getToken(YarnSpinnerParser.NUMBER, 0); }
		public ValueNumberContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterValueNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitValueNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitValueNumber(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueTrueContext extends ValueContext {
		public TerminalNode KEYWORD_TRUE() { return getToken(YarnSpinnerParser.KEYWORD_TRUE, 0); }
		public ValueTrueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterValueTrue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitValueTrue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitValueTrue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueFalseContext extends ValueContext {
		public TerminalNode KEYWORD_FALSE() { return getToken(YarnSpinnerParser.KEYWORD_FALSE, 0); }
		public ValueFalseContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterValueFalse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitValueFalse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitValueFalse(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueFuncContext extends ValueContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ValueFuncContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterValueFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitValueFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitValueFunc(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueVarContext extends ValueContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public ValueVarContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterValueVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitValueVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitValueVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueStringContext extends ValueContext {
		public TerminalNode STRING() { return getToken(YarnSpinnerParser.STRING, 0); }
		public ValueStringContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterValueString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitValueString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitValueString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_value);
		try {
			setState(209);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUMBER:
				_localctx = new ValueNumberContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(202);
				match(NUMBER);
				}
				break;
			case KEYWORD_TRUE:
				_localctx = new ValueTrueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(203);
				match(KEYWORD_TRUE);
				}
				break;
			case KEYWORD_FALSE:
				_localctx = new ValueFalseContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(204);
				match(KEYWORD_FALSE);
				}
				break;
			case VAR_ID:
				_localctx = new ValueVarContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(205);
				variable();
				}
				break;
			case STRING:
				_localctx = new ValueStringContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(206);
				match(STRING);
				}
				break;
			case KEYWORD_NULL:
				_localctx = new ValueNullContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(207);
				match(KEYWORD_NULL);
				}
				break;
			case FUNC_ID:
				_localctx = new ValueFuncContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(208);
				function();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode VAR_ID() { return getToken(YarnSpinnerParser.VAR_ID, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(VAR_ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode FUNC_ID() { return getToken(YarnSpinnerParser.FUNC_ID, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(YarnSpinnerParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(YarnSpinnerParser.COMMA, i);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(FUNC_ID);
			setState(214);
			match(LPAREN);
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 46)) & ~0x3f) == 0 && ((1L << (_la - 46)) & ((1L << (KEYWORD_TRUE - 46)) | (1L << (KEYWORD_FALSE - 46)) | (1L << (KEYWORD_NULL - 46)) | (1L << (OPERATOR_LOGICAL_NOT - 46)) | (1L << (OPERATOR_MATHS_SUBTRACTION - 46)) | (1L << (LPAREN - 46)) | (1L << (STRING - 46)) | (1L << (FUNC_ID - 46)) | (1L << (VAR_ID - 46)) | (1L << (NUMBER - 46)))) != 0)) {
				{
				setState(215);
				expression(0);
				}
			}

			setState(222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(218);
				match(COMMA);
				setState(219);
				expression(0);
				}
				}
				setState(224);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(225);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_statementContext extends ParserRuleContext {
		public If_clauseContext if_clause() {
			return getRuleContext(If_clauseContext.class,0);
		}
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public TerminalNode COMMAND_ENDIF() { return getToken(YarnSpinnerParser.COMMAND_ENDIF, 0); }
		public TerminalNode COMMAND_END() { return getToken(YarnSpinnerParser.COMMAND_END, 0); }
		public List<Else_if_clauseContext> else_if_clause() {
			return getRuleContexts(Else_if_clauseContext.class);
		}
		public Else_if_clauseContext else_if_clause(int i) {
			return getRuleContext(Else_if_clauseContext.class,i);
		}
		public Else_clauseContext else_clause() {
			return getRuleContext(Else_clauseContext.class,0);
		}
		public If_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterIf_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitIf_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitIf_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_statementContext if_statement() throws RecognitionException {
		If_statementContext _localctx = new If_statementContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_if_statement);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			if_clause();
			setState(231);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(228);
					else_if_clause();
					}
					} 
				}
				setState(233);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			setState(235);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(234);
				else_clause();
				}
				break;
			}
			setState(237);
			match(COMMAND_START);
			setState(238);
			match(COMMAND_ENDIF);
			setState(239);
			match(COMMAND_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_clauseContext extends ParserRuleContext {
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public TerminalNode COMMAND_IF() { return getToken(YarnSpinnerParser.COMMAND_IF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXPRESSION_COMMAND_END() { return getToken(YarnSpinnerParser.EXPRESSION_COMMAND_END, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public If_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterIf_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitIf_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitIf_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_clauseContext if_clause() throws RecognitionException {
		If_clauseContext _localctx = new If_clauseContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_if_clause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			match(COMMAND_START);
			setState(242);
			match(COMMAND_IF);
			setState(243);
			expression(0);
			setState(244);
			match(EXPRESSION_COMMAND_END);
			setState(248);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(245);
					statement();
					}
					} 
				}
				setState(250);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Else_if_clauseContext extends ParserRuleContext {
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public TerminalNode COMMAND_ELSEIF() { return getToken(YarnSpinnerParser.COMMAND_ELSEIF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXPRESSION_COMMAND_END() { return getToken(YarnSpinnerParser.EXPRESSION_COMMAND_END, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public Else_if_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_if_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterElse_if_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitElse_if_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitElse_if_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Else_if_clauseContext else_if_clause() throws RecognitionException {
		Else_if_clauseContext _localctx = new Else_if_clauseContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_else_if_clause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(COMMAND_START);
			setState(252);
			match(COMMAND_ELSEIF);
			setState(253);
			expression(0);
			setState(254);
			match(EXPRESSION_COMMAND_END);
			setState(258);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(255);
					statement();
					}
					} 
				}
				setState(260);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Else_clauseContext extends ParserRuleContext {
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public TerminalNode COMMAND_ELSE() { return getToken(YarnSpinnerParser.COMMAND_ELSE, 0); }
		public TerminalNode COMMAND_END() { return getToken(YarnSpinnerParser.COMMAND_END, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public Else_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterElse_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitElse_clause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitElse_clause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Else_clauseContext else_clause() throws RecognitionException {
		Else_clauseContext _localctx = new Else_clauseContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_else_clause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(COMMAND_START);
			setState(262);
			match(COMMAND_ELSE);
			setState(263);
			match(COMMAND_END);
			setState(267);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(264);
					statement();
					}
					} 
				}
				setState(269);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Set_statementContext extends ParserRuleContext {
		public Set_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_statement; }
	 
		public Set_statementContext() { }
		public void copyFrom(Set_statementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SetExpressionContext extends Set_statementContext {
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public TerminalNode COMMAND_SET() { return getToken(YarnSpinnerParser.COMMAND_SET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXPRESSION_COMMAND_END() { return getToken(YarnSpinnerParser.EXPRESSION_COMMAND_END, 0); }
		public SetExpressionContext(Set_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterSetExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitSetExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitSetExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SetVariableToValueContext extends Set_statementContext {
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public TerminalNode COMMAND_SET() { return getToken(YarnSpinnerParser.COMMAND_SET, 0); }
		public TerminalNode VAR_ID() { return getToken(YarnSpinnerParser.VAR_ID, 0); }
		public TerminalNode OPERATOR_ASSIGNMENT() { return getToken(YarnSpinnerParser.OPERATOR_ASSIGNMENT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXPRESSION_COMMAND_END() { return getToken(YarnSpinnerParser.EXPRESSION_COMMAND_END, 0); }
		public SetVariableToValueContext(Set_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterSetVariableToValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitSetVariableToValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitSetVariableToValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Set_statementContext set_statement() throws RecognitionException {
		Set_statementContext _localctx = new Set_statementContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_set_statement);
		try {
			setState(282);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				_localctx = new SetVariableToValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(270);
				match(COMMAND_START);
				setState(271);
				match(COMMAND_SET);
				setState(272);
				match(VAR_ID);
				setState(273);
				match(OPERATOR_ASSIGNMENT);
				setState(274);
				expression(0);
				setState(275);
				match(EXPRESSION_COMMAND_END);
				}
				break;
			case 2:
				_localctx = new SetExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(277);
				match(COMMAND_START);
				setState(278);
				match(COMMAND_SET);
				setState(279);
				expression(0);
				setState(280);
				match(EXPRESSION_COMMAND_END);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Call_statementContext extends ParserRuleContext {
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public TerminalNode COMMAND_CALL() { return getToken(YarnSpinnerParser.COMMAND_CALL, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode EXPRESSION_COMMAND_END() { return getToken(YarnSpinnerParser.EXPRESSION_COMMAND_END, 0); }
		public Call_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_call_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterCall_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitCall_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitCall_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Call_statementContext call_statement() throws RecognitionException {
		Call_statementContext _localctx = new Call_statementContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_call_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			match(COMMAND_START);
			setState(285);
			match(COMMAND_CALL);
			setState(286);
			function();
			setState(287);
			match(EXPRESSION_COMMAND_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Command_statementContext extends ParserRuleContext {
		public TerminalNode COMMAND_START() { return getToken(YarnSpinnerParser.COMMAND_START, 0); }
		public Command_formatted_textContext command_formatted_text() {
			return getRuleContext(Command_formatted_textContext.class,0);
		}
		public TerminalNode COMMAND_TEXT_END() { return getToken(YarnSpinnerParser.COMMAND_TEXT_END, 0); }
		public TerminalNode TEXT_COMMANDHASHTAG_NEWLINE() { return getToken(YarnSpinnerParser.TEXT_COMMANDHASHTAG_NEWLINE, 0); }
		public List<HashtagContext> hashtag() {
			return getRuleContexts(HashtagContext.class);
		}
		public HashtagContext hashtag(int i) {
			return getRuleContext(HashtagContext.class,i);
		}
		public Command_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterCommand_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitCommand_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitCommand_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Command_statementContext command_statement() throws RecognitionException {
		Command_statementContext _localctx = new Command_statementContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_command_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			match(COMMAND_START);
			setState(290);
			command_formatted_text();
			setState(291);
			match(COMMAND_TEXT_END);
			setState(299);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				setState(295);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HASHTAG) | (1L << BODY_HASHTAG) | (1L << TEXT_HASHTAG) | (1L << TEXT_COMMANDHASHTAG_HASHTAG) | (1L << HASHTAG_TAG))) != 0)) {
					{
					{
					setState(292);
					hashtag();
					}
					}
					setState(297);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(298);
				match(TEXT_COMMANDHASHTAG_NEWLINE);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Command_formatted_textContext extends ParserRuleContext {
		public List<TerminalNode> COMMAND_TEXT() { return getTokens(YarnSpinnerParser.COMMAND_TEXT); }
		public TerminalNode COMMAND_TEXT(int i) {
			return getToken(YarnSpinnerParser.COMMAND_TEXT, i);
		}
		public List<TerminalNode> COMMAND_EXPRESSION_START() { return getTokens(YarnSpinnerParser.COMMAND_EXPRESSION_START); }
		public TerminalNode COMMAND_EXPRESSION_START(int i) {
			return getToken(YarnSpinnerParser.COMMAND_EXPRESSION_START, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> EXPRESSION_END() { return getTokens(YarnSpinnerParser.EXPRESSION_END); }
		public TerminalNode EXPRESSION_END(int i) {
			return getToken(YarnSpinnerParser.EXPRESSION_END, i);
		}
		public Command_formatted_textContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command_formatted_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterCommand_formatted_text(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitCommand_formatted_text(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitCommand_formatted_text(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Command_formatted_textContext command_formatted_text() throws RecognitionException {
		Command_formatted_textContext _localctx = new Command_formatted_textContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_command_formatted_text);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMAND_EXPRESSION_START || _la==COMMAND_TEXT) {
				{
				setState(306);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COMMAND_TEXT:
					{
					setState(301);
					match(COMMAND_TEXT);
					}
					break;
				case COMMAND_EXPRESSION_START:
					{
					setState(302);
					match(COMMAND_EXPRESSION_START);
					setState(303);
					expression(0);
					setState(304);
					match(EXPRESSION_END);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(310);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Shortcut_option_statementContext extends ParserRuleContext {
		public List<Shortcut_optionContext> shortcut_option() {
			return getRuleContexts(Shortcut_optionContext.class);
		}
		public Shortcut_optionContext shortcut_option(int i) {
			return getRuleContext(Shortcut_optionContext.class,i);
		}
		public Shortcut_option_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shortcut_option_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterShortcut_option_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitShortcut_option_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitShortcut_option_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Shortcut_option_statementContext shortcut_option_statement() throws RecognitionException {
		Shortcut_option_statementContext _localctx = new Shortcut_option_statementContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_shortcut_option_statement);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(312); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(311);
					shortcut_option();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(314); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Shortcut_optionContext extends ParserRuleContext {
		public Line_statementContext line_statement() {
			return getRuleContext(Line_statementContext.class,0);
		}
		public TerminalNode INDENT() { return getToken(YarnSpinnerParser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(YarnSpinnerParser.DEDENT, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public Shortcut_optionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shortcut_option; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterShortcut_option(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitShortcut_option(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitShortcut_option(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Shortcut_optionContext shortcut_option() throws RecognitionException {
		Shortcut_optionContext _localctx = new Shortcut_optionContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_shortcut_option);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			match(SHORTCUT_ARROW);
			setState(317);
			line_statement();
			setState(326);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				setState(318);
				match(INDENT);
				setState(322);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INDENT) | (1L << HASHTAG) | (1L << SHORTCUT_ARROW) | (1L << COMMAND_START) | (1L << OPTION_START) | (1L << FORMAT_FUNCTION_START) | (1L << BODY_HASHTAG) | (1L << TEXT_NEWLINE) | (1L << TEXT_HASHTAG) | (1L << TEXT_EXPRESSION_START) | (1L << TEXT_COMMAND_START) | (1L << TEXT_FORMAT_FUNCTION_START) | (1L << TEXT) | (1L << TEXT_COMMANDHASHTAG_COMMAND_START) | (1L << TEXT_COMMANDHASHTAG_HASHTAG) | (1L << TEXT_COMMANDHASHTAG_NEWLINE) | (1L << HASHTAG_TAG))) != 0)) {
					{
					{
					setState(319);
					statement();
					}
					}
					setState(324);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(325);
				match(DEDENT);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Option_statementContext extends ParserRuleContext {
		public Option_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option_statement; }
	 
		public Option_statementContext() { }
		public void copyFrom(Option_statementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class OptionLinkContext extends Option_statementContext {
		public Token NodeName;
		public Option_formatted_textContext option_formatted_text() {
			return getRuleContext(Option_formatted_textContext.class,0);
		}
		public TerminalNode OPTION_ID() { return getToken(YarnSpinnerParser.OPTION_ID, 0); }
		public TerminalNode TEXT_COMMANDHASHTAG_NEWLINE() { return getToken(YarnSpinnerParser.TEXT_COMMANDHASHTAG_NEWLINE, 0); }
		public List<HashtagContext> hashtag() {
			return getRuleContexts(HashtagContext.class);
		}
		public HashtagContext hashtag(int i) {
			return getRuleContext(HashtagContext.class,i);
		}
		public OptionLinkContext(Option_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterOptionLink(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitOptionLink(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitOptionLink(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OptionJumpContext extends Option_statementContext {
		public Token NodeName;
		public TerminalNode OPTION_TEXT() { return getToken(YarnSpinnerParser.OPTION_TEXT, 0); }
		public OptionJumpContext(Option_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterOptionJump(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitOptionJump(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitOptionJump(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Option_statementContext option_statement() throws RecognitionException {
		Option_statementContext _localctx = new Option_statementContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_option_statement);
		int _la;
		try {
			setState(345);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				_localctx = new OptionLinkContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(328);
				match(OPTION_START);
				setState(329);
				option_formatted_text();
				setState(330);
				match(OPTION_DELIMIT);
				setState(331);
				((OptionLinkContext)_localctx).NodeName = match(OPTION_ID);
				setState(332);
				match(OPTION_END);
				setState(340);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(336);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HASHTAG) | (1L << BODY_HASHTAG) | (1L << TEXT_HASHTAG) | (1L << TEXT_COMMANDHASHTAG_HASHTAG) | (1L << HASHTAG_TAG))) != 0)) {
						{
						{
						setState(333);
						hashtag();
						}
						}
						setState(338);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(339);
					match(TEXT_COMMANDHASHTAG_NEWLINE);
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new OptionJumpContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(342);
				match(OPTION_START);
				setState(343);
				((OptionJumpContext)_localctx).NodeName = match(OPTION_TEXT);
				setState(344);
				match(OPTION_END);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Option_formatted_textContext extends ParserRuleContext {
		public List<TerminalNode> OPTION_TEXT() { return getTokens(YarnSpinnerParser.OPTION_TEXT); }
		public TerminalNode OPTION_TEXT(int i) {
			return getToken(YarnSpinnerParser.OPTION_TEXT, i);
		}
		public List<TerminalNode> OPTION_EXPRESSION_START() { return getTokens(YarnSpinnerParser.OPTION_EXPRESSION_START); }
		public TerminalNode OPTION_EXPRESSION_START(int i) {
			return getToken(YarnSpinnerParser.OPTION_EXPRESSION_START, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> EXPRESSION_END() { return getTokens(YarnSpinnerParser.EXPRESSION_END); }
		public TerminalNode EXPRESSION_END(int i) {
			return getToken(YarnSpinnerParser.EXPRESSION_END, i);
		}
		public List<TerminalNode> OPTION_FORMAT_FUNCTION_START() { return getTokens(YarnSpinnerParser.OPTION_FORMAT_FUNCTION_START); }
		public TerminalNode OPTION_FORMAT_FUNCTION_START(int i) {
			return getToken(YarnSpinnerParser.OPTION_FORMAT_FUNCTION_START, i);
		}
		public List<Format_functionContext> format_function() {
			return getRuleContexts(Format_functionContext.class);
		}
		public Format_functionContext format_function(int i) {
			return getRuleContext(Format_functionContext.class,i);
		}
		public List<TerminalNode> FORMAT_FUNCTION_END() { return getTokens(YarnSpinnerParser.FORMAT_FUNCTION_END); }
		public TerminalNode FORMAT_FUNCTION_END(int i) {
			return getToken(YarnSpinnerParser.FORMAT_FUNCTION_END, i);
		}
		public Option_formatted_textContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option_formatted_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).enterOption_formatted_text(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof YarnSpinnerParserListener ) ((YarnSpinnerParserListener)listener).exitOption_formatted_text(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof YarnSpinnerParserVisitor ) return ((YarnSpinnerParserVisitor<? extends T>)visitor).visitOption_formatted_text(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Option_formatted_textContext option_formatted_text() throws RecognitionException {
		Option_formatted_textContext _localctx = new Option_formatted_textContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_option_formatted_text);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(356);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case OPTION_TEXT:
					{
					setState(347);
					match(OPTION_TEXT);
					}
					break;
				case OPTION_EXPRESSION_START:
					{
					setState(348);
					match(OPTION_EXPRESSION_START);
					setState(349);
					expression(0);
					setState(350);
					match(EXPRESSION_END);
					}
					break;
				case OPTION_FORMAT_FUNCTION_START:
					{
					setState(352);
					match(OPTION_FORMAT_FUNCTION_START);
					setState(353);
					format_function();
					setState(354);
					match(FORMAT_FUNCTION_END);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(358); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( ((((_la - 94)) & ~0x3f) == 0 && ((1L << (_la - 94)) & ((1L << (OPTION_EXPRESSION_START - 94)) | (1L << (OPTION_FORMAT_FUNCTION_START - 94)) | (1L << (OPTION_TEXT - 94)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 12:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3d\u016b\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\7\2<\n\2\f\2\16\2?\13\2\3"+
		"\2\6\2B\n\2\r\2\16\2C\3\3\3\3\3\3\3\3\3\4\6\4K\n\4\r\4\16\4L\3\4\3\4\3"+
		"\4\3\4\3\5\3\5\3\5\5\5V\n\5\3\5\3\5\3\6\7\6[\n\6\f\6\16\6^\13\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7i\n\7\f\7\16\7l\13\7\3\7\5\7o\n\7\3"+
		"\b\3\b\5\bs\n\b\3\b\7\bv\n\b\f\b\16\by\13\b\3\b\3\b\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\7\t\u0086\n\t\f\t\16\t\u0089\13\t\3\n\3\n\3\n\3\n\3"+
		"\n\7\n\u0090\n\n\f\n\16\n\u0093\13\n\3\13\3\13\3\13\3\13\3\13\3\13\5\13"+
		"\u009b\n\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00b7"+
		"\n\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\7\16\u00c8\n\16\f\16\16\16\u00cb\13\16\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\5\17\u00d4\n\17\3\20\3\20\3\21\3\21\3\21\5\21\u00db\n"+
		"\21\3\21\3\21\7\21\u00df\n\21\f\21\16\21\u00e2\13\21\3\21\3\21\3\22\3"+
		"\22\7\22\u00e8\n\22\f\22\16\22\u00eb\13\22\3\22\5\22\u00ee\n\22\3\22\3"+
		"\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\7\23\u00f9\n\23\f\23\16\23\u00fc"+
		"\13\23\3\24\3\24\3\24\3\24\3\24\7\24\u0103\n\24\f\24\16\24\u0106\13\24"+
		"\3\25\3\25\3\25\3\25\7\25\u010c\n\25\f\25\16\25\u010f\13\25\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u011d\n\26\3\27"+
		"\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\7\30\u0128\n\30\f\30\16\30\u012b"+
		"\13\30\3\30\5\30\u012e\n\30\3\31\3\31\3\31\3\31\3\31\7\31\u0135\n\31\f"+
		"\31\16\31\u0138\13\31\3\32\6\32\u013b\n\32\r\32\16\32\u013c\3\33\3\33"+
		"\3\33\3\33\7\33\u0143\n\33\f\33\16\33\u0146\13\33\3\33\5\33\u0149\n\33"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\7\34\u0151\n\34\f\34\16\34\u0154\13\34"+
		"\3\34\5\34\u0157\n\34\3\34\3\34\3\34\5\34\u015c\n\34\3\35\3\35\3\35\3"+
		"\35\3\35\3\35\3\35\3\35\3\35\6\35\u0167\n\35\r\35\16\35\u0168\3\35\2\3"+
		"\32\36\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668\2"+
		"\r\4\2\27\27##\4\2\25\25\33\33\7\2\n\n\26\26\30\30\"\"&&\4\2\32\32!!\3"+
		"\2@B\3\2>?\3\2EG\3\2CD\4\2\64\65\678\4\2\66\6699\3\2:<\2\u0187\2=\3\2"+
		"\2\2\4E\3\2\2\2\6J\3\2\2\2\bR\3\2\2\2\n\\\3\2\2\2\fn\3\2\2\2\16p\3\2\2"+
		"\2\20\u0087\3\2\2\2\22\u008a\3\2\2\2\24\u009a\3\2\2\2\26\u009c\3\2\2\2"+
		"\30\u009f\3\2\2\2\32\u00b6\3\2\2\2\34\u00d3\3\2\2\2\36\u00d5\3\2\2\2 "+
		"\u00d7\3\2\2\2\"\u00e5\3\2\2\2$\u00f3\3\2\2\2&\u00fd\3\2\2\2(\u0107\3"+
		"\2\2\2*\u011c\3\2\2\2,\u011e\3\2\2\2.\u0123\3\2\2\2\60\u0136\3\2\2\2\62"+
		"\u013a\3\2\2\2\64\u013e\3\2\2\2\66\u015b\3\2\2\28\u0166\3\2\2\2:<\5\4"+
		"\3\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>A\3\2\2\2?=\3\2\2\2@B\5\6"+
		"\4\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2D\3\3\2\2\2EF\7\n\2\2FG\7"+
		"\'\2\2GH\7#\2\2H\5\3\2\2\2IK\5\b\5\2JI\3\2\2\2KL\3\2\2\2LJ\3\2\2\2LM\3"+
		"\2\2\2MN\3\2\2\2NO\7\b\2\2OP\5\n\6\2PQ\7\21\2\2Q\7\3\2\2\2RS\7\7\2\2S"+
		"U\7\t\2\2TV\7\13\2\2UT\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\7\f\2\2X\t\3\2\2"+
		"\2Y[\5\f\7\2ZY\3\2\2\2[^\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]\13\3\2\2\2^\\"+
		"\3\2\2\2_o\5\16\b\2`o\5\"\22\2ao\5*\26\2bo\5\66\34\2co\5\62\32\2do\5,"+
		"\27\2eo\5.\30\2fj\7\3\2\2gi\5\f\7\2hg\3\2\2\2il\3\2\2\2jh\3\2\2\2jk\3"+
		"\2\2\2km\3\2\2\2lj\3\2\2\2mo\7\4\2\2n_\3\2\2\2n`\3\2\2\2na\3\2\2\2nb\3"+
		"\2\2\2nc\3\2\2\2nd\3\2\2\2ne\3\2\2\2nf\3\2\2\2o\r\3\2\2\2pr\5\20\t\2q"+
		"s\5\30\r\2rq\3\2\2\2rs\3\2\2\2sw\3\2\2\2tv\5\26\f\2ut\3\2\2\2vy\3\2\2"+
		"\2wu\3\2\2\2wx\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\t\2\2\2{\17\3\2\2\2|\u0086"+
		"\7\35\2\2}~\7\31\2\2~\177\5\32\16\2\177\u0080\7M\2\2\u0080\u0086\3\2\2"+
		"\2\u0081\u0082\t\3\2\2\u0082\u0083\5\22\n\2\u0083\u0084\7.\2\2\u0084\u0086"+
		"\3\2\2\2\u0085|\3\2\2\2\u0085}\3\2\2\2\u0085\u0081\3\2\2\2\u0086\u0089"+
		"\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\21\3\2\2\2\u0089"+
		"\u0087\3\2\2\2\u008a\u008b\7)\2\2\u008b\u008c\7+\2\2\u008c\u008d\5\36"+
		"\20\2\u008d\u0091\7M\2\2\u008e\u0090\5\24\13\2\u008f\u008e\3\2\2\2\u0090"+
		"\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\23\3\2\2"+
		"\2\u0093\u0091\3\2\2\2\u0094\u0095\7)\2\2\u0095\u0096\7,\2\2\u0096\u009b"+
		"\7-\2\2\u0097\u0098\7*\2\2\u0098\u0099\7,\2\2\u0099\u009b\7-\2\2\u009a"+
		"\u0094\3\2\2\2\u009a\u0097\3\2\2\2\u009b\25\3\2\2\2\u009c\u009d\t\4\2"+
		"\2\u009d\u009e\7\'\2\2\u009e\27\3\2\2\2\u009f\u00a0\t\5\2\2\u00a0\u00a1"+
		"\7R\2\2\u00a1\u00a2\5\32\16\2\u00a2\u00a3\7N\2\2\u00a3\31\3\2\2\2\u00a4"+
		"\u00a5\b\16\1\2\u00a5\u00a6\7H\2\2\u00a6\u00a7\5\32\16\2\u00a7\u00a8\7"+
		"I\2\2\u00a8\u00b7\3\2\2\2\u00a9\u00aa\7D\2\2\u00aa\u00b7\5\32\16\f\u00ab"+
		"\u00ac\7=\2\2\u00ac\u00b7\5\32\16\13\u00ad\u00ae\5\36\20\2\u00ae\u00af"+
		"\t\6\2\2\u00af\u00b0\5\32\16\6\u00b0\u00b7\3\2\2\2\u00b1\u00b2\5\36\20"+
		"\2\u00b2\u00b3\t\7\2\2\u00b3\u00b4\5\32\16\5\u00b4\u00b7\3\2\2\2\u00b5"+
		"\u00b7\5\34\17\2\u00b6\u00a4\3\2\2\2\u00b6\u00a9\3\2\2\2\u00b6\u00ab\3"+
		"\2\2\2\u00b6\u00ad\3\2\2\2\u00b6\u00b1\3\2\2\2\u00b6\u00b5\3\2\2\2\u00b7"+
		"\u00c9\3\2\2\2\u00b8\u00b9\f\n\2\2\u00b9\u00ba\t\b\2\2\u00ba\u00c8\5\32"+
		"\16\13\u00bb\u00bc\f\t\2\2\u00bc\u00bd\t\t\2\2\u00bd\u00c8\5\32\16\n\u00be"+
		"\u00bf\f\b\2\2\u00bf\u00c0\t\n\2\2\u00c0\u00c8\5\32\16\t\u00c1\u00c2\f"+
		"\7\2\2\u00c2\u00c3\t\13\2\2\u00c3\u00c8\5\32\16\b\u00c4\u00c5\f\4\2\2"+
		"\u00c5\u00c6\t\f\2\2\u00c6\u00c8\5\32\16\5\u00c7\u00b8\3\2\2\2\u00c7\u00bb"+
		"\3\2\2\2\u00c7\u00be\3\2\2\2\u00c7\u00c1\3\2\2\2\u00c7\u00c4\3\2\2\2\u00c8"+
		"\u00cb\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\33\3\2\2"+
		"\2\u00cb\u00c9\3\2\2\2\u00cc\u00d4\7P\2\2\u00cd\u00d4\7\60\2\2\u00ce\u00d4"+
		"\7\61\2\2\u00cf\u00d4\5\36\20\2\u00d0\u00d4\7K\2\2\u00d1\u00d4\7\62\2"+
		"\2\u00d2\u00d4\5 \21\2\u00d3\u00cc\3\2\2\2\u00d3\u00cd\3\2\2\2\u00d3\u00ce"+
		"\3\2\2\2\u00d3\u00cf\3\2\2\2\u00d3\u00d0\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3"+
		"\u00d2\3\2\2\2\u00d4\35\3\2\2\2\u00d5\u00d6\7O\2\2\u00d6\37\3\2\2\2\u00d7"+
		"\u00d8\7L\2\2\u00d8\u00da\7H\2\2\u00d9\u00db\5\32\16\2\u00da\u00d9\3\2"+
		"\2\2\u00da\u00db\3\2\2\2\u00db\u00e0\3\2\2\2\u00dc\u00dd\7J\2\2\u00dd"+
		"\u00df\5\32\16\2\u00de\u00dc\3\2\2\2\u00df\u00e2\3\2\2\2\u00e0\u00de\3"+
		"\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e3\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e3"+
		"\u00e4\7I\2\2\u00e4!\3\2\2\2\u00e5\u00e9\5$\23\2\u00e6\u00e8\5&\24\2\u00e7"+
		"\u00e6\3\2\2\2\u00e8\u00eb\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea\3\2"+
		"\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00ec\u00ee\5(\25\2\u00ed"+
		"\u00ec\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0\7\23"+
		"\2\2\u00f0\u00f1\7V\2\2\u00f1\u00f2\7X\2\2\u00f2#\3\2\2\2\u00f3\u00f4"+
		"\7\23\2\2\u00f4\u00f5\7R\2\2\u00f5\u00f6\5\32\16\2\u00f6\u00fa\7N\2\2"+
		"\u00f7\u00f9\5\f\7\2\u00f8\u00f7\3\2\2\2\u00f9\u00fc\3\2\2\2\u00fa\u00f8"+
		"\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb%\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fd"+
		"\u00fe\7\23\2\2\u00fe\u00ff\7S\2\2\u00ff\u0100\5\32\16\2\u0100\u0104\7"+
		"N\2\2\u0101\u0103\5\f\7\2\u0102\u0101\3\2\2\2\u0103\u0106\3\2\2\2\u0104"+
		"\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105\'\3\2\2\2\u0106\u0104\3\2\2\2"+
		"\u0107\u0108\7\23\2\2\u0108\u0109\7T\2\2\u0109\u010d\7X\2\2\u010a\u010c"+
		"\5\f\7\2\u010b\u010a\3\2\2\2\u010c\u010f\3\2\2\2\u010d\u010b\3\2\2\2\u010d"+
		"\u010e\3\2\2\2\u010e)\3\2\2\2\u010f\u010d\3\2\2\2\u0110\u0111\7\23\2\2"+
		"\u0111\u0112\7U\2\2\u0112\u0113\7O\2\2\u0113\u0114\7\63\2\2\u0114\u0115"+
		"\5\32\16\2\u0115\u0116\7N\2\2\u0116\u011d\3\2\2\2\u0117\u0118\7\23\2\2"+
		"\u0118\u0119\7U\2\2\u0119\u011a\5\32\16\2\u011a\u011b\7N\2\2\u011b\u011d"+
		"\3\2\2\2\u011c\u0110\3\2\2\2\u011c\u0117\3\2\2\2\u011d+\3\2\2\2\u011e"+
		"\u011f\7\23\2\2\u011f\u0120\7W\2\2\u0120\u0121\5 \21\2\u0121\u0122\7N"+
		"\2\2\u0122-\3\2\2\2\u0123\u0124\7\23\2\2\u0124\u0125\5\60\31\2\u0125\u012d"+
		"\7Y\2\2\u0126\u0128\5\26\f\2\u0127\u0126\3\2\2\2\u0128\u012b\3\2\2\2\u0129"+
		"\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012c\3\2\2\2\u012b\u0129\3\2"+
		"\2\2\u012c\u012e\7#\2\2\u012d\u0129\3\2\2\2\u012d\u012e\3\2\2\2\u012e"+
		"/\3\2\2\2\u012f\u0135\7[\2\2\u0130\u0131\7Z\2\2\u0131\u0132\5\32\16\2"+
		"\u0132\u0133\7M\2\2\u0133\u0135\3\2\2\2\u0134\u012f\3\2\2\2\u0134\u0130"+
		"\3\2\2\2\u0135\u0138\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137"+
		"\61\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u013b\5\64\33\2\u013a\u0139\3\2"+
		"\2\2\u013b\u013c\3\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d"+
		"\63\3\2\2\2\u013e\u013f\7\22\2\2\u013f\u0148\5\16\b\2\u0140\u0144\7\3"+
		"\2\2\u0141\u0143\5\f\7\2\u0142\u0141\3\2\2\2\u0143\u0146\3\2\2\2\u0144"+
		"\u0142\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u0147\3\2\2\2\u0146\u0144\3\2"+
		"\2\2\u0147\u0149\7\4\2\2\u0148\u0140\3\2\2\2\u0148\u0149\3\2\2\2\u0149"+
		"\65\3\2\2\2\u014a\u014b\7\24\2\2\u014b\u014c\58\35\2\u014c\u014d\7_\2"+
		"\2\u014d\u014e\7d\2\2\u014e\u0156\7^\2\2\u014f\u0151\5\26\f\2\u0150\u014f"+
		"\3\2\2\2\u0151\u0154\3\2\2\2\u0152\u0150\3\2\2\2\u0152\u0153\3\2\2\2\u0153"+
		"\u0155\3\2\2\2\u0154\u0152\3\2\2\2\u0155\u0157\7#\2\2\u0156\u0152\3\2"+
		"\2\2\u0156\u0157\3\2\2\2\u0157\u015c\3\2\2\2\u0158\u0159\7\24\2\2\u0159"+
		"\u015a\7b\2\2\u015a\u015c\7^\2\2\u015b\u014a\3\2\2\2\u015b\u0158\3\2\2"+
		"\2\u015c\67\3\2\2\2\u015d\u0167\7b\2\2\u015e\u015f\7`\2\2\u015f\u0160"+
		"\5\32\16\2\u0160\u0161\7M\2\2\u0161\u0167\3\2\2\2\u0162\u0163\7a\2\2\u0163"+
		"\u0164\5\22\n\2\u0164\u0165\7.\2\2\u0165\u0167\3\2\2\2\u0166\u015d\3\2"+
		"\2\2\u0166\u015e\3\2\2\2\u0166\u0162\3\2\2\2\u0167\u0168\3\2\2\2\u0168"+
		"\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u01699\3\2\2\2\'=CLU\\jnrw\u0085\u0087"+
		"\u0091\u009a\u00b6\u00c7\u00c9\u00d3\u00da\u00e0\u00e9\u00ed\u00fa\u0104"+
		"\u010d\u011c\u0129\u012d\u0134\u0136\u013c\u0144\u0148\u0152\u0156\u015b"+
		"\u0166\u0168";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}