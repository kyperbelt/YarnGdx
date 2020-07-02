// Generated from grammarFork/YarnSpinnerParser.g4 by ANTLR 4.7.1
package com.kyper.yarn.compiler;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link YarnSpinnerParser}.
 */
public interface YarnSpinnerParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#dialogue}.
	 * @param ctx the parse tree
	 */
	void enterDialogue(YarnSpinnerParser.DialogueContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#dialogue}.
	 * @param ctx the parse tree
	 */
	void exitDialogue(YarnSpinnerParser.DialogueContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#file_hashtag}.
	 * @param ctx the parse tree
	 */
	void enterFile_hashtag(YarnSpinnerParser.File_hashtagContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#file_hashtag}.
	 * @param ctx the parse tree
	 */
	void exitFile_hashtag(YarnSpinnerParser.File_hashtagContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#node}.
	 * @param ctx the parse tree
	 */
	void enterNode(YarnSpinnerParser.NodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#node}.
	 * @param ctx the parse tree
	 */
	void exitNode(YarnSpinnerParser.NodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#header}.
	 * @param ctx the parse tree
	 */
	void enterHeader(YarnSpinnerParser.HeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#header}.
	 * @param ctx the parse tree
	 */
	void exitHeader(YarnSpinnerParser.HeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(YarnSpinnerParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(YarnSpinnerParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(YarnSpinnerParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(YarnSpinnerParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#line_statement}.
	 * @param ctx the parse tree
	 */
	void enterLine_statement(YarnSpinnerParser.Line_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#line_statement}.
	 * @param ctx the parse tree
	 */
	void exitLine_statement(YarnSpinnerParser.Line_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#line_formatted_text}.
	 * @param ctx the parse tree
	 */
	void enterLine_formatted_text(YarnSpinnerParser.Line_formatted_textContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#line_formatted_text}.
	 * @param ctx the parse tree
	 */
	void exitLine_formatted_text(YarnSpinnerParser.Line_formatted_textContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#format_function}.
	 * @param ctx the parse tree
	 */
	void enterFormat_function(YarnSpinnerParser.Format_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#format_function}.
	 * @param ctx the parse tree
	 */
	void exitFormat_function(YarnSpinnerParser.Format_functionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code KeyValuePairNamed}
	 * labeled alternative in {@link YarnSpinnerParser#key_value_pair}.
	 * @param ctx the parse tree
	 */
	void enterKeyValuePairNamed(YarnSpinnerParser.KeyValuePairNamedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code KeyValuePairNamed}
	 * labeled alternative in {@link YarnSpinnerParser#key_value_pair}.
	 * @param ctx the parse tree
	 */
	void exitKeyValuePairNamed(YarnSpinnerParser.KeyValuePairNamedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code KeyValuePairNumber}
	 * labeled alternative in {@link YarnSpinnerParser#key_value_pair}.
	 * @param ctx the parse tree
	 */
	void enterKeyValuePairNumber(YarnSpinnerParser.KeyValuePairNumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code KeyValuePairNumber}
	 * labeled alternative in {@link YarnSpinnerParser#key_value_pair}.
	 * @param ctx the parse tree
	 */
	void exitKeyValuePairNumber(YarnSpinnerParser.KeyValuePairNumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#hashtag}.
	 * @param ctx the parse tree
	 */
	void enterHashtag(YarnSpinnerParser.HashtagContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#hashtag}.
	 * @param ctx the parse tree
	 */
	void exitHashtag(YarnSpinnerParser.HashtagContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#line_condition}.
	 * @param ctx the parse tree
	 */
	void enterLine_condition(YarnSpinnerParser.Line_conditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#line_condition}.
	 * @param ctx the parse tree
	 */
	void exitLine_condition(YarnSpinnerParser.Line_conditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expParens}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpParens(YarnSpinnerParser.ExpParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expParens}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpParens(YarnSpinnerParser.ExpParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expMultDivMod}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpMultDivMod(YarnSpinnerParser.ExpMultDivModContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expMultDivMod}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpMultDivMod(YarnSpinnerParser.ExpMultDivModContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expMultDivModEquals}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpMultDivModEquals(YarnSpinnerParser.ExpMultDivModEqualsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expMultDivModEquals}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpMultDivModEquals(YarnSpinnerParser.ExpMultDivModEqualsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expComparison}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpComparison(YarnSpinnerParser.ExpComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expComparison}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpComparison(YarnSpinnerParser.ExpComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expNegative}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpNegative(YarnSpinnerParser.ExpNegativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expNegative}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpNegative(YarnSpinnerParser.ExpNegativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expAndOrXor}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpAndOrXor(YarnSpinnerParser.ExpAndOrXorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expAndOrXor}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpAndOrXor(YarnSpinnerParser.ExpAndOrXorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expPlusMinusEquals}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpPlusMinusEquals(YarnSpinnerParser.ExpPlusMinusEqualsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expPlusMinusEquals}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpPlusMinusEquals(YarnSpinnerParser.ExpPlusMinusEqualsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expAddSub}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpAddSub(YarnSpinnerParser.ExpAddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expAddSub}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpAddSub(YarnSpinnerParser.ExpAddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expNot}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpNot(YarnSpinnerParser.ExpNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expNot}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpNot(YarnSpinnerParser.ExpNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expValue}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpValue(YarnSpinnerParser.ExpValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expValue}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpValue(YarnSpinnerParser.ExpValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expEquality}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpEquality(YarnSpinnerParser.ExpEqualityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expEquality}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpEquality(YarnSpinnerParser.ExpEqualityContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueNumber}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValueNumber(YarnSpinnerParser.ValueNumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueNumber}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValueNumber(YarnSpinnerParser.ValueNumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueTrue}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValueTrue(YarnSpinnerParser.ValueTrueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueTrue}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValueTrue(YarnSpinnerParser.ValueTrueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueFalse}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValueFalse(YarnSpinnerParser.ValueFalseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueFalse}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValueFalse(YarnSpinnerParser.ValueFalseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueVar}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValueVar(YarnSpinnerParser.ValueVarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueVar}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValueVar(YarnSpinnerParser.ValueVarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueString}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValueString(YarnSpinnerParser.ValueStringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueString}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValueString(YarnSpinnerParser.ValueStringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueNull}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValueNull(YarnSpinnerParser.ValueNullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueNull}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValueNull(YarnSpinnerParser.ValueNullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueFunc}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValueFunc(YarnSpinnerParser.ValueFuncContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueFunc}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValueFunc(YarnSpinnerParser.ValueFuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(YarnSpinnerParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(YarnSpinnerParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(YarnSpinnerParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(YarnSpinnerParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(YarnSpinnerParser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(YarnSpinnerParser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#if_clause}.
	 * @param ctx the parse tree
	 */
	void enterIf_clause(YarnSpinnerParser.If_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#if_clause}.
	 * @param ctx the parse tree
	 */
	void exitIf_clause(YarnSpinnerParser.If_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#else_if_clause}.
	 * @param ctx the parse tree
	 */
	void enterElse_if_clause(YarnSpinnerParser.Else_if_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#else_if_clause}.
	 * @param ctx the parse tree
	 */
	void exitElse_if_clause(YarnSpinnerParser.Else_if_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#else_clause}.
	 * @param ctx the parse tree
	 */
	void enterElse_clause(YarnSpinnerParser.Else_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#else_clause}.
	 * @param ctx the parse tree
	 */
	void exitElse_clause(YarnSpinnerParser.Else_clauseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setVariableToValue}
	 * labeled alternative in {@link YarnSpinnerParser#set_statement}.
	 * @param ctx the parse tree
	 */
	void enterSetVariableToValue(YarnSpinnerParser.SetVariableToValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setVariableToValue}
	 * labeled alternative in {@link YarnSpinnerParser#set_statement}.
	 * @param ctx the parse tree
	 */
	void exitSetVariableToValue(YarnSpinnerParser.SetVariableToValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code setExpression}
	 * labeled alternative in {@link YarnSpinnerParser#set_statement}.
	 * @param ctx the parse tree
	 */
	void enterSetExpression(YarnSpinnerParser.SetExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code setExpression}
	 * labeled alternative in {@link YarnSpinnerParser#set_statement}.
	 * @param ctx the parse tree
	 */
	void exitSetExpression(YarnSpinnerParser.SetExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#call_statement}.
	 * @param ctx the parse tree
	 */
	void enterCall_statement(YarnSpinnerParser.Call_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#call_statement}.
	 * @param ctx the parse tree
	 */
	void exitCall_statement(YarnSpinnerParser.Call_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#command_statement}.
	 * @param ctx the parse tree
	 */
	void enterCommand_statement(YarnSpinnerParser.Command_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#command_statement}.
	 * @param ctx the parse tree
	 */
	void exitCommand_statement(YarnSpinnerParser.Command_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#command_formatted_text}.
	 * @param ctx the parse tree
	 */
	void enterCommand_formatted_text(YarnSpinnerParser.Command_formatted_textContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#command_formatted_text}.
	 * @param ctx the parse tree
	 */
	void exitCommand_formatted_text(YarnSpinnerParser.Command_formatted_textContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#shortcut_option_statement}.
	 * @param ctx the parse tree
	 */
	void enterShortcut_option_statement(YarnSpinnerParser.Shortcut_option_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#shortcut_option_statement}.
	 * @param ctx the parse tree
	 */
	void exitShortcut_option_statement(YarnSpinnerParser.Shortcut_option_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#shortcut_option}.
	 * @param ctx the parse tree
	 */
	void enterShortcut_option(YarnSpinnerParser.Shortcut_optionContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#shortcut_option}.
	 * @param ctx the parse tree
	 */
	void exitShortcut_option(YarnSpinnerParser.Shortcut_optionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OptionLink}
	 * labeled alternative in {@link YarnSpinnerParser#option_statement}.
	 * @param ctx the parse tree
	 */
	void enterOptionLink(YarnSpinnerParser.OptionLinkContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OptionLink}
	 * labeled alternative in {@link YarnSpinnerParser#option_statement}.
	 * @param ctx the parse tree
	 */
	void exitOptionLink(YarnSpinnerParser.OptionLinkContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OptionJump}
	 * labeled alternative in {@link YarnSpinnerParser#option_statement}.
	 * @param ctx the parse tree
	 */
	void enterOptionJump(YarnSpinnerParser.OptionJumpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OptionJump}
	 * labeled alternative in {@link YarnSpinnerParser#option_statement}.
	 * @param ctx the parse tree
	 */
	void exitOptionJump(YarnSpinnerParser.OptionJumpContext ctx);
	/**
	 * Enter a parse tree produced by {@link YarnSpinnerParser#option_formatted_text}.
	 * @param ctx the parse tree
	 */
	void enterOption_formatted_text(YarnSpinnerParser.Option_formatted_textContext ctx);
	/**
	 * Exit a parse tree produced by {@link YarnSpinnerParser#option_formatted_text}.
	 * @param ctx the parse tree
	 */
	void exitOption_formatted_text(YarnSpinnerParser.Option_formatted_textContext ctx);
}