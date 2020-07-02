// Generated from grammarFork/YarnSpinnerParser.g4 by ANTLR 4.7.1
package com.kyper.yarn.compiler;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link YarnSpinnerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface YarnSpinnerParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#dialogue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDialogue(YarnSpinnerParser.DialogueContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#file_hashtag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_hashtag(YarnSpinnerParser.File_hashtagContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#node}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNode(YarnSpinnerParser.NodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader(YarnSpinnerParser.HeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(YarnSpinnerParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(YarnSpinnerParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#line_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine_statement(YarnSpinnerParser.Line_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#line_formatted_text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine_formatted_text(YarnSpinnerParser.Line_formatted_textContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#format_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormat_function(YarnSpinnerParser.Format_functionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code KeyValuePairNamed}
	 * labeled alternative in {@link YarnSpinnerParser#key_value_pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyValuePairNamed(YarnSpinnerParser.KeyValuePairNamedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code KeyValuePairNumber}
	 * labeled alternative in {@link YarnSpinnerParser#key_value_pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyValuePairNumber(YarnSpinnerParser.KeyValuePairNumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#hashtag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHashtag(YarnSpinnerParser.HashtagContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#line_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine_condition(YarnSpinnerParser.Line_conditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expParens}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpParens(YarnSpinnerParser.ExpParensContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expMultDivMod}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpMultDivMod(YarnSpinnerParser.ExpMultDivModContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expMultDivModEquals}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpMultDivModEquals(YarnSpinnerParser.ExpMultDivModEqualsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expComparison}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpComparison(YarnSpinnerParser.ExpComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expNegative}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpNegative(YarnSpinnerParser.ExpNegativeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expAndOrXor}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpAndOrXor(YarnSpinnerParser.ExpAndOrXorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expPlusMinusEquals}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpPlusMinusEquals(YarnSpinnerParser.ExpPlusMinusEqualsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expAddSub}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpAddSub(YarnSpinnerParser.ExpAddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expNot}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpNot(YarnSpinnerParser.ExpNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expValue}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpValue(YarnSpinnerParser.ExpValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expEquality}
	 * labeled alternative in {@link YarnSpinnerParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpEquality(YarnSpinnerParser.ExpEqualityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueNumber}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueNumber(YarnSpinnerParser.ValueNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueTrue}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueTrue(YarnSpinnerParser.ValueTrueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueFalse}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueFalse(YarnSpinnerParser.ValueFalseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueVar}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueVar(YarnSpinnerParser.ValueVarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueString}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueString(YarnSpinnerParser.ValueStringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueNull}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueNull(YarnSpinnerParser.ValueNullContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valueFunc}
	 * labeled alternative in {@link YarnSpinnerParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueFunc(YarnSpinnerParser.ValueFuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(YarnSpinnerParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(YarnSpinnerParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(YarnSpinnerParser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#if_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_clause(YarnSpinnerParser.If_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#else_if_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElse_if_clause(YarnSpinnerParser.Else_if_clauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#else_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElse_clause(YarnSpinnerParser.Else_clauseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setVariableToValue}
	 * labeled alternative in {@link YarnSpinnerParser#set_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetVariableToValue(YarnSpinnerParser.SetVariableToValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code setExpression}
	 * labeled alternative in {@link YarnSpinnerParser#set_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetExpression(YarnSpinnerParser.SetExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#call_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCall_statement(YarnSpinnerParser.Call_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#command_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommand_statement(YarnSpinnerParser.Command_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#command_formatted_text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommand_formatted_text(YarnSpinnerParser.Command_formatted_textContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#shortcut_option_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShortcut_option_statement(YarnSpinnerParser.Shortcut_option_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#shortcut_option}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShortcut_option(YarnSpinnerParser.Shortcut_optionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OptionLink}
	 * labeled alternative in {@link YarnSpinnerParser#option_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionLink(YarnSpinnerParser.OptionLinkContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OptionJump}
	 * labeled alternative in {@link YarnSpinnerParser#option_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionJump(YarnSpinnerParser.OptionJumpContext ctx);
	/**
	 * Visit a parse tree produced by {@link YarnSpinnerParser#option_formatted_text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption_formatted_text(YarnSpinnerParser.Option_formatted_textContext ctx);
}