package com.kyper.yarn.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.kyper.yarn.Program;
import com.kyper.yarn.Program.ByteCode;
import com.kyper.yarn.Program.Instruction;
import com.kyper.yarn.Program.Node;
import com.kyper.yarn.Program.Operand;
import com.kyper.yarn.StringUtils;
import com.kyper.yarn.VirtualMachine.TokenType;
import com.kyper.yarn.compiler.YarnSpinnerParser.BodyContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Call_statementContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Command_statementContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Else_clauseContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Else_if_clauseContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpAddSubContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpComparisonContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpEqualityContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpMultDivModContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpMultDivModEqualsContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpNegativeContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpNotContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpParensContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpPlusMinusEqualsContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpValueContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ExpressionContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.FunctionContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.HashtagContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.HeaderContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.If_clauseContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.If_statementContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Key_value_pairContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Line_statementContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.NodeContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.OptionJumpContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.OptionLinkContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.SetExpressionContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.SetVariableToValueContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Shortcut_optionContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.Shortcut_option_statementContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.StatementContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ValueFalseContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ValueFuncContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ValueNullContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ValueNumberContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ValueStringContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ValueTrueContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.ValueVarContext;
import com.kyper.yarn.compiler.YarnSpinnerParser.VariableContext;

public class YarnCompiler extends YarnSpinnerParserBaseListener {

	private static Regex invalidNodeTitleNameRegex = new Regex("[\\[<>\\]{}\\|:\\s#\\$]");

	private int labelCount = 0;

	private Node currentNode;
	private boolean rawTextNode;
	private Program program;
	private final String fileName;
	private boolean containsImplicitStringTags;

	private HashMap<String, StringInfo> stringTable = new HashMap<String, YarnCompiler.StringInfo>();
	private int stringCount = 0;
	HashMap<Integer, TokenType> tokens = new HashMap<Integer, TokenType>();

	public YarnCompiler(String fileName) {
		program = new Program();
		program.name = fileName;
		this.fileName = fileName;
	}

	public static Status compileFile(Path path, Program program, Map<String, StringInfo> stringTable) {
		String source = null;
		try {
			source = new String(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		String fileName = StringUtils.removeExtension(path.getFileName().toString());

		return compileString(source, fileName, program, stringTable);
	}

	// if DEBUG
//    private String parseTree;
//    private ArrayList<String> tokens;

	public static Status compileString(String text, String fileName, Program program,
			Map<String, StringInfo> stringTable2) {
		CharStream input = CharStreams.fromString(text);

		YarnSpinnerLexer lexer = new YarnSpinnerLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		YarnSpinnerParser parser = new YarnSpinnerParser(tokens);

		// turning off the normal error listener and using ours
		parser.removeErrorListeners();
		parser.addErrorListener(ParserErrorListener.getInstance());

		lexer.removeErrorListeners();
		lexer.addErrorListener(LexerErrorListener.getInstance());
		// lexer.getAllTokens().stream().forEach(System.out::println);
		ParseTree tree;
		// getTokensFromString(text).stream().forEach(System.out::println);
		try {
			tree = parser.dialogue();
		} catch (ParseException e) {

			// if DEBUG
//              ArrayList<String> tokenStringList = new ArrayList<String>();
//              tokens.seek(0);
//              for(Token token : tokens.getTokens()) {
//                  tokenStringList.add(String.format("%d:%d %s'%s'",token.getLine(),token.getTokenSource().getCharPositionInLine(),YarnSpinnerLexer.VOCABULARY.getDisplayName(token.getType()),token.getText());
//              }
//
//              throw new ParseException(String.format("%s\n\nTokens:\n%s",e.getMessage(),String.join("\n", tokenStringList)));
			// else
			System.out.println(e.getMessage());
			throw new ParseException(e.getMessage());
			// endif // DEBUG
		}
		YarnCompiler compiler = new YarnCompiler(fileName);

		compiler.compile(tree);

		program.mergeFrom(compiler.getProgram());
		stringTable2.putAll(compiler.stringTable);

		if (compiler.containsImplicitStringTags) {
			return Status.SucceededUntaggedStrings;
		} else {
			return Status.Succeeded;
		}
	}

	public static ArrayList<String> getTokensFromFile(Path path) {
		String text = null;
		try {
			text = new String(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return getTokensFromString(text);
	}

	private static ArrayList<String> getTokensFromString(String text) {
		CharStream input = CharStreams.fromString(text);

		YarnSpinnerLexer lexer = new YarnSpinnerLexer(input);

		ArrayList<String> tokenStringList = new ArrayList<String>();

		ArrayList<? extends Token> tokens = (ArrayList<? extends Token>) lexer.getAllTokens();
		for (Token token : tokens) {
			tokenStringList
					.add(String.format("%d:%d %s'%s'", token.getLine(), token.getTokenSource().getCharPositionInLine(),
							YarnSpinnerLexer.VOCABULARY.getDisplayName(token.getType()), token.getText()));
		}

		return tokenStringList;
	}

	public String registerString(String text, String nodeName, String lineID, int lineNumber, String[] tags) {
		String lineIDUsed;

		boolean isImplicit;

		if (lineID == null) {
			lineIDUsed = String.format("%s-%s-%d", this.fileName, nodeName, this.stringCount);

			this.stringCount += 1;

			// Note that we had to make up a tag for this string, which
			// may not be the same on future compilations
			containsImplicitStringTags = true;

			isImplicit = true;
		} else {
			lineIDUsed = lineID;

			isImplicit = false;
		}

		StringInfo theString = new StringInfo(text, nodeName, lineNumber, fileName, isImplicit, tags);

		// Finally, add this to the string table, and return the line ID.
		this.stringTable.put(lineIDUsed, theString);

		return lineIDUsed;
	}

	public String registerLabel(String commentary) {
		return "L" + labelCount++ + (commentary == null ? "" : commentary);
	}

	protected void emit(Node node, ByteCode code, Operand... operands) {
		Instruction instruction = new Instruction();
		instruction.operation = code;
		instruction.operands.addAll(Arrays.asList(operands));

		node.instructions.add(instruction);
	}

	protected void emit(ByteCode code, Operand... operands) {
		emit(this.currentNode, code, operands);
	}

	protected String getLineId(List<HashtagContext> list) {
		// if there are any hashtags
		if (list != null) {
			for (HashtagContext hashtag : list) {
				String tagText = hashtag.text.getText();
				if (tagText.startsWith("line:")) {
					return tagText;
				}
			}
		}
		return null;
	}

	protected void compile(ParseTree tree) {
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(this, tree);
	}

	@Override
	public void enterNode(NodeContext ctx) {
		currentNode = new Node();
		rawTextNode = false;
	}

	@Override
	public void exitNode(NodeContext ctx) {
		// System.out.println("hello from:"+currentNode.name);
		program.nodes.put(currentNode.name, currentNode);
		currentNode = null;
		rawTextNode = false;
	}

	@Override
	public void exitHeader(HeaderContext context) {
		String headerKey = context.header_key.getText();

		// Use the header value if provided, else fall back to the
		// empty string. This means that a header like "foo: \n" will
		// be stored as 'foo', '', consistent with how it was typed.
		// That is, it's not null, because a header was provided, but
		// it was written as an empty line.
		String headerValue = context.header_value != null
				? (context.header_value.getText() == null ? "" : context.header_value.getText())
				: "";

		if (headerKey.equals("title")) {
			// Set the name of the node
			currentNode.name = headerValue;

			// Throw an exception if this node name contains illegal
			// characters
			if (invalidNodeTitleNameRegex.match(currentNode.name).find()) {
				throw new ParseException(
						String.format("The node '%s' contains illegal characters in its title.", currentNode.name));
			}
		}

		if (headerKey.equals("tags")) {
			// Split the list of tags by spaces, and use that
			// TODO: dont know if im using streams correctly - hope i am XD
			// System.out.println("before:"+currentNode.tags);
			Arrays.stream(headerValue.split(" ")).filter(item -> !item.isEmpty()).forEach(currentNode.tags::add);
			// System.out.println("after:"+currentNode.tags);
			if (currentNode.tags.contains("rawText")) {
				// This is a raw text node. Flag it as such for future compilation.
				rawTextNode = true;
			}

		}
	}

	@Override
	public void enterBody(BodyContext context) {

		// if it is a regular node
		if (!rawTextNode) {
			// This is the start of a node that we can jump to. Add a label at this point.
			currentNode.labels.put(registerLabel(null), currentNode.instructions.size());

			BodyVisitor visitor = new BodyVisitor(this);

			for (StatementContext statement : context.statement()) {
				visitor.visit(statement);
			}
		}
		// we are a rawText node
		// turn the body into text
		// save that into the node
		// perform no compilation
		// TODO: oh glob! there has to be a better way
		else {
			currentNode.sourceStringId = registerString(context.getText(), currentNode.name, "line:" + currentNode.name,
					context.getStart().getLine(), null);
		}
	}

	@Override
	public void exitBody(BodyContext context) {
		// if it is a regular node
		if (!rawTextNode) {
			// Note: this only works when we know that we don't have
			// AddOptions and then Jump up back into the code to run them.
			// TODO: A better solution would be for the parser to flag
			// whether a node has Options at the end.
			boolean hasRemainingOptions = false;
			for (Instruction instruction : currentNode.instructions) {
				if (instruction.operation == ByteCode.AddOption) {
					hasRemainingOptions = true;
				}
				if (instruction.operation == ByteCode.ShowOptions) {
					hasRemainingOptions = false;
				}
			}

			// If this compiled node has no lingering options to show at the end of the
			// node, then stop at the end
			if (hasRemainingOptions == false) {
				emit(currentNode, ByteCode.Stop);
			} else {
				// Otherwise, show the accumulated nodes and then jump to the selected node
				emit(currentNode, ByteCode.ShowOptions);

				// Showing options will make the execution stop; the
				// user will have invoked code that pushes the name of
				// a node onto the stack, which RunNode handles
				emit(currentNode, ByteCode.RunNode);
			}
		}
	}

	public boolean isRawTextNode() {
		return rawTextNode;
	}

	public void setRawTextNode(boolean rawTextNode) {
		this.rawTextNode = rawTextNode;
	}

	public Node getcurrentNode() {
		return currentNode;
	}

	public Program getProgram() {
		return program;
	}

	public String getFileName() {
		return fileName;
	}

	protected class BodyVisitor extends YarnSpinnerParserBaseVisitor<Integer> {

		private YarnCompiler compiler;

		public BodyVisitor(YarnCompiler compiler) {
			this.compiler = compiler;
			this.loadOperators();
		}

		private void generateFormattedText(List<ParseTree> children, StringBuilder outputString,
				Integer expressionCount) {
			expressionCount = 0;
			StringBuilder composedString = new StringBuilder();

			// First, visit all of the nodes, which are either terminal
			// text nodes or expressions. if they're expressions, we
			// evaluate them, and inject a positional reference into the
			// final string.
			for (ParseTree child : children) {
				if (child instanceof TerminalNode) {
					composedString.append(child.getText());
				} else if (child instanceof YarnSpinnerParser.Format_functionContext) {
					// Format functions are composed of:
					// 1. The name of the function
					// 2. A value
					// 3. Zero or more key-value pairs.
					//
					// We want to evaluate the value, and ensure that it's
					// on the stack; we then want to emit the entire format
					// function into the composed line, but with the
					// evaluated value replaced with a placeholder, as
					// though it had been an inline expression. We do this
					// because the format function is localisable -
					// different languages will want to have different
					// values.
					//
					// We therefore evaluate any information we need, and
					// then emit the format function into the line, ready
					// to be loaded, or dumped into a string table to be
					// localised.
					//
					// As with inline expressions, we don't emit the '['
					// and ']', because these have already been captured as
					// part of the line text.

					YarnSpinnerParser.Format_functionContext formatFunction = (YarnSpinnerParser.Format_functionContext) child;

					visit(formatFunction.variable());

					composedString.append(formatFunction.function_name.getText());
					composedString.append(" ");
					composedString.append("\"{" + expressionCount + "}\"");

					for (Key_value_pairContext keyValuePair : formatFunction.key_value_pair()) {
						composedString.append(" " + keyValuePair.getText());
					}

					expressionCount += 1;

				} else if (child instanceof ParserRuleContext) {
					// assume that this is an expression (the parser only
					// permits them to be expressions, but we can't specify
					// that here) - visit it, and we will emit code that
					// pushes the final value of this expression onto the
					// stack. running the line will pop these expressions
					// off the stack.
					//
					// Expressions in the final string are denoted as the
					// index of the expression, surrounded by braces { }.
					// However, we don't need to write the braces here
					// ourselves, because the text itself that the parser
					// captured already has them. So, we just need to write
					// the expression count.
					visit(child);
					composedString.append(expressionCount);
					expressionCount += 1;
				}
			}
			outputString.setLength(0);
			outputString.append(composedString.toString().trim());

		}

		private String[] getHashtagTexts(List<HashtagContext> list) {
			// Add hashtag
			ArrayList<String> hashtagText = new ArrayList<String>();
			for (HashtagContext tag : list) {
				hashtagText.add(tag.HASHTAG_TEXT().getText());
			}
			return hashtagText.toArray(new String[0]);
		}

		@Override
		public Integer visitLine_statement(Line_statementContext context) {
			// TODO: add support for line conditions:
			//
			// Mae: here's a line <<if true>>
			//
			// is identical to
			//
			// <<if true>>
			// Mae: here's a line
			// <<endif>>

			StringBuilder composedString = new StringBuilder();
			Integer expressionCount = new Integer(0);
			// Convert the formatted string into a string with
			// placeholders, and evaluate the inline expressions and push
			// the results onto the stack.
			generateFormattedText(context.line_formatted_text().children, composedString, expressionCount);

			// Get the lineID for this string from the hashtags if it has one; otherwise, a
			// new one will be created
			String lineID = compiler.getLineId(context.hashtag());

			String[] hashtagText = getHashtagTexts(context.hashtag());

			int lineNumber = context.getStart().getLine();

			String stringID = compiler.registerString(composedString.toString(), compiler.currentNode.name, lineID,
					lineNumber, hashtagText);

			compiler.emit(ByteCode.RunLine, new Operand(stringID), new Operand(expressionCount));

			return 0;
		}

		@Override
		public Integer visitOptionJump(OptionJumpContext context) {
			String destination = context.NodeName.getText().trim();
			compiler.emit(ByteCode.RunLine, new Operand(destination));
			return 0;
		}

		@Override
		public Integer visitOptionLink(OptionLinkContext context) {

			StringBuilder composedString = new StringBuilder();
			Integer expressionCount = 0;
			// Create the formatted string and evaluate any inline
			// expressions
			generateFormattedText(context.option_formatted_text().children, composedString, expressionCount);

			String destination = context.NodeName.getText().trim();
			String label = composedString.toString();

			int lineNumber = context.getStart().getLine();

			// getting the lineID from the hashtags if it has one
			String lineID = compiler.getLineId(context.hashtag());

			String[] hashtagText = getHashtagTexts(context.hashtag());

			String stringID = compiler.registerString(label, compiler.currentNode.name, lineID, lineNumber,
					hashtagText);

			compiler.emit(ByteCode.AddOption, new Operand(stringID), new Operand(destination),
					new Operand(expressionCount));

			return 0;
		}

		@Override
		public Integer visitSetVariableToValue(SetVariableToValueContext context) {
			// add the expression (whatever it resolves to)
			visit(context.expression());

			// now store the variable and clean up the stack
			String variableName = context.VAR_ID().getText();
			compiler.emit(ByteCode.StoreVariable, new Operand(variableName));
			compiler.emit(ByteCode.Pop);
			return 0;
		}

		@Override
		public Integer visitSetExpression(SetExpressionContext context) {
			// checking the expression is of the correct form
			ExpressionContext expression = context.expression();
			// TODO: is there really no more elegant way of doing this?!
			if (expression instanceof YarnSpinnerParser.ExpMultDivModEqualsContext
					|| expression instanceof YarnSpinnerParser.ExpPlusMinusEqualsContext) {
				// run the expression, it handles it from here
				visit(expression);
			} else {
				// throw an error
				throw ParseException.make(context, "Invalid expression inside assignment statement");
			}
			return 0;
		}

		@Override
		public Integer visitCall_statement(Call_statementContext context) {
			// Visit our function call, which will invoke the function
			visit(context.function());

			// TODO: if this function returns a value, it will be pushed
			// onto the stack, but there's no way for the compiler to know
			// that, so the stack will not be tidied up. is there a way for
			// that to work?
			return 0;
		}

		@Override
		public Integer visitCommand_statement(Command_statementContext context) {
			StringBuilder composedString = new StringBuilder();
			Integer expressionCount = 0;

			generateFormattedText(context.command_formatted_text().children, composedString, expressionCount);

			// TODO: look into replacing this as it seems a bit odd
			switch (composedString.toString()) {
			case "stop":
				// "stop" is a special command that immediately stops
				// execution
				compiler.emit(ByteCode.Stop);
				break;
			default:
				compiler.emit(ByteCode.RunCommand, new Operand(composedString.toString()),
						new Operand(expressionCount));
				break;
			}

			return 0;
		}

		// emits the required bytecode for the function call
		private void handleFunction(String functionName, List<ExpressionContext> list) {
			// generate the instructions for all of the parameters
			for (ExpressionContext parameter : list) {
				visit(parameter);
			}

			// push the number of parameters onto the stack
			compiler.emit(ByteCode.PushNumber, new Operand(list.size()));

			// then call the function itself
			compiler.emit(ByteCode.CallFunc, new Operand(functionName));
		}

		@Override
		public Integer visitFunction(FunctionContext context) {
			String functionName = context.FUNC_ID().getText();
			this.handleFunction(functionName, context.expression());
			return 0;
		}

		@Override
		public Integer visitIf_statement(If_statementContext context) {
			// label to give us a jump point for when the if finishes
			String endOfIfStatementLabel = compiler.registerLabel("endif");

			// handle the if
			If_clauseContext ifClause = context.if_clause();
			generateClause(endOfIfStatementLabel, ifClause.statement(), ifClause.expression());

			// all elseifs
			for (Else_if_clauseContext elseIfClause : context.else_if_clause()) {
				generateClause(endOfIfStatementLabel, elseIfClause.statement(), elseIfClause.expression());
			}

			// the else, if there is one
			Else_clauseContext elseClause = context.else_clause();
			if (elseClause != null) {
				generateClause(endOfIfStatementLabel, elseClause.statement(), null);
			}

			compiler.currentNode.labels.put(endOfIfStatementLabel, compiler.currentNode.instructions.size());

			return 0;
		}

		protected void generateClause(String jumpLabel, List<StatementContext> list, ExpressionContext expression) {
			String endOfClauseLabel = compiler.registerLabel("skipclause");

			// handling the expression (if it has one)
			// will only be called on ifs and elseifs
			if (expression != null) {
				visit(expression);
				compiler.emit(ByteCode.JumpIfFalse, new Operand(endOfClauseLabel));
			}

			// running through all of the children statements
			for (StatementContext child : list) {
				visit(child);
			}

			compiler.emit(ByteCode.JumpTo, new Operand(jumpLabel));
			if (expression != null) {
				compiler.currentNode.labels.put(endOfClauseLabel, compiler.currentNode.instructions.size());
				compiler.emit(ByteCode.Pop);
			}
		}

		@Override
		public Integer visitShortcut_option_statement(Shortcut_option_statementContext context) {

			String endOfGroupLabel = compiler.registerLabel("group_end");

			ArrayList<String> labels = new ArrayList<String>();

			int optionCount = 0;

			// For each option, create an internal destination label that,
			// if the user selects the option, control flow jumps to. Then,
			// evaluate its associated line_statement, and use that as the
			// option text. Finally, add this option to the list of
			// upcoming options.
			for (Shortcut_optionContext shortcut : context.shortcut_option()) {
				// Generate the name of internal label that we'll jump to
				// if this option is selected. We'll emit the label itself
				// later.
				String optionDestinationLabel = compiler.registerLabel(String.format("shortcutoption_%s_%d",
						(currentNode.name == null ? "node" : currentNode.name), optionCount + 1));
				labels.add(optionDestinationLabel);

				// This line statement may have a condition on it. If it
				// does, emit code that evaluates the condition, and skips
				// over the code that prepares and adds the option.
				String endOfClauseLabel = null;
				if (shortcut.line_statement().line_condition() != null) {
					// Register the label we'll jump to if the condition
					// fails. We'll add it later.
					endOfClauseLabel = compiler.registerLabel("conditional_" + optionCount);

					// Evaluate the condition, and jump to the end of
					// clause if it evaluates to false.

					visit(shortcut.line_statement().line_condition().expression());

					compiler.emit(ByteCode.JumpIfFalse, new Operand(endOfClauseLabel));
				}

				StringBuilder composedString = new StringBuilder();
				Integer expressionCount = 0;
				// We can now prepare and add the option.

				// Start by figuring out the text that we want to add. This
				// will involve evaluating any inline expressions.
				generateFormattedText(shortcut.line_statement().line_formatted_text().children, composedString,
						expressionCount);

				// Get the line ID from the hashtags if it has one
				String lineID = compiler.getLineId(shortcut.line_statement().hashtag());

				// Get the hashtags for the line
				String[] hashtags = getHashtagTexts(shortcut.line_statement().hashtag());

				// Register this string
				String labelStringID = compiler.registerString(composedString.toString(), compiler.currentNode.name,
						lineID, shortcut.getStart().getLine(), hashtags);

				// And add this option to the list.
				compiler.emit(ByteCode.AddOption, new Operand(labelStringID), new Operand(optionDestinationLabel),
						new Operand(expressionCount));

				// If we had a line condition, now's the time to generate
				// the label that we'd jump to if its condition is false.
				if (shortcut.line_statement().line_condition() != null) {
					compiler.currentNode.labels.put(endOfClauseLabel, compiler.currentNode.instructions.size());

					// JumpIfFalse doesn't change the stack, so we need to
					// tidy up
					compiler.emit(ByteCode.Pop);
				}

				optionCount++;
			}

			// All of the options that we intend to show are now ready to
			// go.
			compiler.emit(ByteCode.ShowOptions);

			// The top of the stack now contains the name of the label we
			// want to jump to. Jump to it now.
			compiler.emit(ByteCode.Jump);

			// We'll now emit the labels and code associated with each
			// option.
			optionCount = 0;
			for (Shortcut_optionContext shortcut : context.shortcut_option()) {
				// emit the label for this option's code
				compiler.currentNode.labels.put(labels.get(optionCount), compiler.currentNode.instructions.size());

				// Run through all the children statements of the shortcut
				// option.
				for (StatementContext child : shortcut.statement()) {
					visit(child);
				}

				// Jump to the end of this shortcut option group.
				compiler.emit(ByteCode.JumpTo, new Operand(endOfGroupLabel));

				optionCount++;
			}

			// We made it to the end! Mark the end of the group, so we can jump to it.
			compiler.currentNode.labels.put(endOfGroupLabel, compiler.currentNode.instructions.size());
			compiler.emit(ByteCode.Pop);

			return 0;
		}

		@Override
		public Integer visitExpParens(ExpParensContext context) {
			return visit(context.expression());

		}

		@Override
		public Integer visitExpNegative(ExpNegativeContext ctx) {
			visit(ctx.expression());
			// TODO: temp operator call

			// Indicate that we are pushing one parameter
			compiler.emit(ByteCode.PushNumber, new Operand(1));

			compiler.emit(ByteCode.CallFunc, new Operand(TokenType.UnaryMinus.name()));

			return 0;
		}

		@Override
		public Integer visitExpNot(ExpNotContext ctx) {
			visit(ctx.expression());

			// TODO: temp operator call

			// Indicate that we are pushing one parameter
			compiler.emit(ByteCode.PushNumber, new Operand(1));

			compiler.emit(ByteCode.CallFunc, new Operand(TokenType.Not.name()));

			return 0;
		}

		@Override
		public Integer visitExpValue(ExpValueContext ctx) {
			// TODO Auto-generated method stub
			return visit(ctx.value());
		}

		protected void genericExpVisitor(YarnSpinnerParser.ExpressionContext left,
				YarnSpinnerParser.ExpressionContext right, int op) {
			visit(left);
			visit(right);

			// TODO: temp operator call

			// Indicate that we are pushing two items for comparison
			compiler.emit(ByteCode.PushNumber, new Operand(2));

			compiler.emit(ByteCode.CallFunc, new Operand(tokens.get(op).toString()));
		}

		@Override
		public Integer visitExpMultDivMod(ExpMultDivModContext ctx) {
			genericExpVisitor(ctx.expression(0), ctx.expression(1), ctx.op.getType());
			return 0;
		}

		@Override
		public Integer visitExpAddSub(ExpAddSubContext ctx) {
			genericExpVisitor(ctx.expression(0), ctx.expression(1), ctx.op.getType());
			return 0;
		}

		@Override
		public Integer visitExpComparison(ExpComparisonContext ctx) {
			genericExpVisitor(ctx.expression(0), ctx.expression(1), ctx.op.getType());
			return 0;
		}

		@Override
		public Integer visitExpEquality(ExpEqualityContext ctx) {
			genericExpVisitor(ctx.expression(0), ctx.expression(1), ctx.op.getType());
			return 0;
		}

		protected void opEquals(String varName, YarnSpinnerParser.ExpressionContext expression, int op) {
			// Get the current value of the variable
			compiler.emit(ByteCode.PushVariable, new Operand(varName));

			// run the expression
			visit(expression);

			// Stack now contains [currentValue, expressionValue]

			// Indicate that we are pushing two items for comparison
			compiler.emit(ByteCode.PushNumber, new Operand(2));

			// now we evaluate the operator
			// op will match to one of + - / * %
			compiler.emit(ByteCode.CallFunc, new Operand(tokens.get(op).toString()));

			// Stack now has the destination value
			// now store the variable and clean up the stack
			compiler.emit(ByteCode.StoreVariable, new Operand(varName));
			compiler.emit(ByteCode.Pop);
		}

		@Override
		public Integer visitExpMultDivModEquals(ExpMultDivModEqualsContext ctx) {
			opEquals(ctx.variable().getText(), ctx.expression(), ctx.op.getType());
			return 0;
		}

		@Override
		public Integer visitExpPlusMinusEquals(ExpPlusMinusEqualsContext ctx) {
			opEquals(ctx.variable().getText(), ctx.expression(), ctx.op.getType());
			return 0;
		}

		@Override
		public Integer visitValueVar(ValueVarContext ctx) {

			return visit(ctx.variable());
		}

		@Override
		public Integer visitValueNumber(ValueNumberContext context) {
			float number = Float.parseFloat(context.NUMBER().getText());
			compiler.emit(ByteCode.PushNumber, new Operand(number));

			return 0;
		}

		@Override
		public Integer visitValueTrue(ValueTrueContext ctx) {
			compiler.emit(ByteCode.PushBool, new Operand(true));
			return 0;
		}

		@Override
		public Integer visitValueFalse(ValueFalseContext ctx) {
			compiler.emit(ByteCode.PushBool, new Operand(false));
			return 0;
		}

		@Override
		public Integer visitVariable(VariableContext context) {
			String variableName = context.VAR_ID().getText();
			compiler.emit(ByteCode.PushVariable, new Operand(variableName));

			return 0;
		}

		@Override
		public Integer visitValueString(ValueStringContext ctx) {
			// stripping the " off the front and back
			// actually is this what we want?
			String stringVal = ctx.STRING().getText().replaceAll("^[\'\"]+", "").replaceAll("[\'\"]+$", "");

			compiler.emit(ByteCode.PushString, new Operand(stringVal));

			return 0;
		}

		@Override
		public Integer visitValueFunc(ValueFuncContext context) {
			visit(context.function());

			return 0;
		}

		@Override
		public Integer visitValueNull(ValueNullContext ctx) {
			compiler.emit(ByteCode.PushNull);
			return 0;
		}

		private void loadOperators() {
			// operators for the standard expressions
			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_LESS_THAN_EQUALS, TokenType.LessThanOrEqualTo);
			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_GREATER_THAN_EQUALS, TokenType.GreaterThanOrEqualTo);
			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_LESS, TokenType.LessThan);
			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_GREATER, TokenType.GreaterThan);

			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_EQUALS, TokenType.EqualTo);
			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_NOT_EQUALS, TokenType.NotEqualTo);

			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_AND, TokenType.And);
			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_OR, TokenType.Or);
			tokens.put(YarnSpinnerLexer.OPERATOR_LOGICAL_XOR, TokenType.Xor);

			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_ADDITION, TokenType.Add);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_SUBTRACTION, TokenType.Minus);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_MULTIPLICATION, TokenType.Multiply);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_DIVISION, TokenType.Divide);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_MODULUS, TokenType.Modulo);
			// operators for the set expressions
			// these map directly to the operator if they didn't have the =
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_ADDITION_EQUALS, TokenType.Add);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_SUBTRACTION_EQUALS, TokenType.Minus);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_MULTIPLICATION_EQUALS, TokenType.Multiply);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_DIVISION_EQUALS, TokenType.Divide);
			tokens.put(YarnSpinnerLexer.OPERATOR_MATHS_MODULUS_EQUALS, TokenType.Modulo);
		}

	}

	public static class Graph {
		public ArrayList<String> nodes = new ArrayList<String>();
		public MultiMap<String, String> edges = new MultiMap<String, String>();
		public String graphName = "G";

		public void edge(String source, String target) {
			edges.map(source, target);
		}

		public String toDot() {
			StringBuilder buf = new StringBuilder();
			buf.append(StringUtils.format("digraph %s ", graphName));
			buf.append("{\n");
			buf.append("  ");
			for (String node : nodes) { // print all nodes first
				buf.append(node);
				buf.append("; ");
			}
			buf.append("\n");
			for (String src : edges.keySet()) {
				List<String> output;
				if ((output = edges.getOrDefault(src, new ArrayList<String>())) != null) {
					for (String trg : output) {
						buf.append("  ");
						buf.append(src);
						buf.append(" -> ");
						buf.append(trg);
						buf.append(";\n");
					}
				}
			}
			buf.append("}\n");
			return buf.toString();
		}
	}

	public static class GraphListener extends YarnSpinnerParserBaseListener {
		String currentNode = null;
		public Graph graph = new Graph();

		public void enterHeader(YarnSpinnerParser.HeaderContext context) {
			System.out.println("header " + context.header_key.getText());
			if (context.header_key.getText().equals("title")) {
				currentNode = context.header_value.getText();
			}
		}

		public void exitNode(YarnSpinnerParser.NodeContext context) {
			// Add this node to the graph
			graph.nodes.add(currentNode);
		}

		public void exitOptionJump(YarnSpinnerParser.OptionJumpContext context) {
			graph.edge(currentNode, context.NodeName.getText());
		}

		public void exitOptionLink(YarnSpinnerParser.OptionLinkContext context) {
			graph.edge(currentNode, context.NodeName.getText());
		}

	}

	public static enum Status {
		Succeeded, SucceededUntaggedStrings,
	}

	public static class StringInfo {

		public String text;
		public String nodeName;
		public int lineNumber;
		public String fileName;
		public boolean implicit;
		public String[] metaData;

		public StringInfo(String text, String nodeName, int lineNumber, String fileName, boolean implicit,
				String[] metaData) {
			this.text = text;
			this.nodeName = nodeName;
			this.lineNumber = lineNumber;
			this.fileName = fileName;
			this.implicit = implicit;
			this.metaData = metaData != null ? metaData : new String[8];
		}
	}

//	public static class CompileFlags {
//		//should we emmit code that turns VAR_SHUFFLE_OPTIONS off
//		//after the next RunOptions bytecode
//		public boolean DisableShuffleOptionsAfterNextSet;
//	}
//
//	CompileFlags flags = new CompileFlags();
//	public Program program;
//	public String program_name;
//
//	private int label_count = 0;
//
//	public Compiler(String program_name) {
//		program = new Program();
//		this.program_name = program_name;
//	}
//
//	public Program getProgram() {
//		return program;
//	}
//
//	public void compileNode(Parser.Node node) {
//		if (program.nodes.containsKey(node.getName())) {
//			throw new IllegalArgumentException("Diplicate node name " + node.getName());
//		}
//
//		Program.Node compiled_node = new Program.Node();
//
//		compiled_node.name = node.getName();
//		compiled_node.tags = new ArrayList<String>(node.getNodeTags()); // TODO maybe just keep this as an ArrayList the whole time
//
//		//register the entire text of this node if we hav eit
//		if (node.getSource() != null) {
//			//dump the entire contents of this node into the string table
//			//instead of compiling its contents
//			//the line number is 0 because the string starts at the begining of the node
//			compiled_node.source_string_id = program.registerString(node.getSource(), node.getName(),
//					"line:" + node.getName(), 0, true);
//		} else {
//
//			//compile the node
//
//			String start_label = registerLabel();
//			emit(compiled_node, ByteCode.Label, start_label);
//
//			for (Statement statement : node.getStatements()) {
//				generateCode(compiled_node, statement);
//			}
//
//			// Does this node end after emitting AddOptions codes
//			// without calling ShowOptions?
//
//			// Note: this only works when we know that we don't have
//			// AddOptions and then Jump up back into the code to run them.
//			// TODO: A better solution would be for the parser to flag
//			// whether a node has Options at the end.
//
//			boolean has_remaining_options = false;
//
//			for (Instruction instruction : compiled_node.instructions) {
//				if(instruction.getOperation() == ByteCode.AddOption) {
//					has_remaining_options = true;
//				}
//				if(instruction.getOperation() == ByteCode.ShowOptions)
//					has_remaining_options = false;
//			}
//
//			//if this compiled node has no lingering options to show at the end of the node, then we stop at the end
//			if(!has_remaining_options) {
//				emit(compiled_node,ByteCode.Stop);
//			}else {
//
//				//otherwise show the accumulated nodes and then jump to the selected node
//
//				emit(compiled_node, ByteCode.ShowOptions);
//
//				if(flags.DisableShuffleOptionsAfterNextSet) {
//					emit(compiled_node, ByteCode.PushBool,false);
//					emit(compiled_node, ByteCode.StoreVariable,VirtualMachine.SpecialVariables.ShuffleOptions);
//					emit(compiled_node, ByteCode.Pop);
//					flags.DisableShuffleOptionsAfterNextSet = false;
//				}
//
//				emit(compiled_node, ByteCode.RunNode);
//			}
//
//		}
//
//		program.nodes.put(compiled_node.name, compiled_node);
//	}
//
//	public void emit(Program.Node node, ByteCode code, Object operandA, Object operandB) {
//		Instruction instruction = new Instruction();
//		instruction.setOperation(code);
//		instruction.setOperandA(operandA);
//		instruction.setOperandB(operandB);
//
//		node.instructions.add(instruction);
//
//		if (code == ByteCode.Label) {
//			//add this label to the label table
//			node.labels.put((String) instruction.operandA(), node.instructions.size() - 1);
//		}
//	}
//
//	public void emit(Program.Node node, ByteCode code, Object operandA) {
//		emit(node, code, operandA, null);
//	}
//
//	public void emit(Program.Node node, ByteCode code) {
//		emit(node, code, null);
//	}
//
//	public String getLineIDFromNodeTags(Parser.ParseNode node) {
//		for (String tag : node.tags) {
//			if (tag.startsWith("line:"))
//				return tag;
//		}
//		return null;
//	}
//
//	//statements
//	public void generateCode(Program.Node node, Statement statement) {
//		switch (statement.getType()) {
//		case CustomCommand:
//			generateCode(node, statement.getCustomCommand());
//			break;
//		case ShortcutOptionGroup:
//			generateCode(node, statement.getShortcutOptionGroup());
//			break;
//		case Block:
//			//blocks are just groups of statements
//			for (Statement s : statement.getBlock().getStatements()) {
//				generateCode(node, s);
//			}
//			break;
//		case IfStatement:
//			generateCode(node, statement.getIfStatement());
//			break;
//		case OptionStatement:
//			generateCode(node, statement.getOptionStatement());
//			break;
//		case AssignmentStatement:
//			generateCode(node, statement.getAssignmentStatement());
//			break;
//		case Line:
//			generateCode(node, statement, statement.getLine());
//			break;
//		default:
//			throw new IllegalArgumentException();
//		}
//	}
//
//	public void generateCode(Program.Node node, CustomCommand statement) {
//
//		//if this command is an evaluable expression, evaluate it
//		if (statement.getExpression() != null) {
//			generateCode(node, statement.getExpression());
//		} else {
//			String custom_command = statement.getClientCommand();
//			if (custom_command.equals("stop")) {
//				//CASE: stop
//
//				emit(node, ByteCode.Stop);
//
//			} else if (custom_command.equals("shuffleNextOptions")) {
//				//CASE : shuffleNextOptions
//
//				//emit code that sets VARSHUFFLE OPPTIONS to true
//				emit(node, ByteCode.PushBool, true);
//				emit(node, ByteCode.StoreVariable, VirtualMachine.SpecialVariables.ShuffleOptions);
//				emit(node, ByteCode.Pop);
//				flags.DisableShuffleOptionsAfterNextSet = true;
//
//			} else {
//				//DEFAULT
//				emit(node, ByteCode.RunCommand, custom_command);
//			}
//		}
//
//	}
//
//	public void generateCode(Program.Node node, Statement parse_node, String line) {
//		//does this line have #line:LINENUM tag? use it
//		String line_id = getLineIDFromNodeTags(parse_node);
//		String num = program.registerString(line, node.name, line_id, parse_node.line_number, true);
//
//		emit(node, ByteCode.RunLine, num);
//	}
//
//	public void generateCode(Program.Node node, ShortcutOptionGroup statement) {
//		String endof_group = registerLabel("group_end");
//
//		ArrayList<String> labels = new ArrayList<String>();
//
//		int option_count = 0;
//
//		for (ShortcutOption option : statement.getOptions()) {
//			String option_destination = registerLabel("option_" + (option_count + 1));
//			labels.add(option_destination);
//
//			String endof_clause = null;
//
//			if (option.getCondition() != null) {
//				endof_clause = registerLabel("conditional_" + option_count);
//				generateCode(node, option.getCondition());
//
//				emit(node, ByteCode.JumpIfFalse, endof_clause);
//			}
//
//			String label_line_id = getLineIDFromNodeTags(option);
//			String label_string_id = program.registerString(option.getLabel(), node.name, label_line_id,
//					option.line_number, true);
//
//			emit(node, ByteCode.AddOption, label_string_id, option_destination);
//
//			if (option.getCondition() != null) {
//				emit(node, ByteCode.Label, endof_clause);
//				emit(node, ByteCode.Pop);
//			}
//			option_count++;
//		}
//
//		emit(node, ByteCode.ShowOptions);
//
//		if (flags.DisableShuffleOptionsAfterNextSet == true) {
//			emit(node, ByteCode.PushBool, false);
//			emit(node, ByteCode.StoreVariable, VirtualMachine.SpecialVariables.ShuffleOptions);
//			emit(node, ByteCode.Pop);
//			flags.DisableShuffleOptionsAfterNextSet = false;
//		}
//
//		emit(node, ByteCode.Jump);
//
//		option_count = 0;
//		for (ShortcutOption option : statement.getOptions()) {
//			emit(node, ByteCode.Label, labels.get(option_count));
//
//			if (option.getOptionNode() != null)
//				generateCode(node, option.getOptionNode().getStatements());
//
//			emit(node, ByteCode.JumpTo, endof_group);
//
//			option_count++;
//
//		}
//
//		//reached the end of option group
//		emit(node,ByteCode.Label,endof_group);
//
//		//clean up after jump
//		emit(node, ByteCode.Pop);
//
//	}
//
//	public void generateCode(Program.Node node, List<Statement> statements) {
//		if (statements == null)
//			return;
//		for (Statement statement : statements) {
//			generateCode(node, statement);
//		}
//	}
//
//	public void generateCode(Program.Node node, IfStatement statement) {
//		//we will jump to this label at the end of every clause
//		String endof_if = registerLabel("endif");
//
//		for (Clause clause : statement.clauses) {
//			String endof_clause = registerLabel("skipclause");
//
//			if (clause.getExpression() != null) {
//				generateCode(node, clause.getExpression());
//				emit(node, ByteCode.JumpIfFalse, endof_clause);
//			}
//
//			generateCode(node, clause.getStatements());
//			emit(node, ByteCode.JumpTo, endof_if);
//
//			if (clause.getExpression() != null) {
//				emit(node, ByteCode.Label, endof_clause);
//			}
//
//			//clean the stack by popping the expression that was tested earlier
//			if (clause.getExpression() != null) {
//				emit(node, ByteCode.Pop);
//			}
//
//		}
//
//		emit(node, ByteCode.Label, endof_if);
//
//	}
//
//	public void generateCode(Program.Node node, OptionStatement statement) {
//		String destination = statement.getDestination();
//
//		if (statement.getLabel() == null || statement.getLabel().isEmpty()) {
//			//this is a jump to another node
//			emit(node, ByteCode.RunNode, destination);
//		} else {
//			String line_id = getLineIDFromNodeTags(statement.parent);
//			String string_id = program.registerString(statement.getLabel(), node.name, line_id, statement.line_number,
//					true);
//
//			emit(node, ByteCode.AddOption, string_id, destination);
//		}
//
//	}
//
//	public void generateCode(Program.Node node, AssignmentStatement statement) {
//		//is it a straight assignment
//		if (statement.getOperation() == TokenType.EqualToOrAssign) {
//			//evaluate the expression which will result in a value
//			//on the stack
//			generateCode(node, statement.getValueExpression());
//			//stack now contains destionation value
//		} else {
//
//			//its combined operation plus assignment
//
//			//get the current value of the variable
//			emit(node, ByteCode.PushVariable, statement.getDestinationVariable());
//
//			//evaluate the expression , which will result in a value
//			//on the stack
//			generateCode(node, statement.getValueExpression());
//
//			//stack now contains currentValue,expresionValue
//
//			switch (statement.getOperation()) {
//			case AddAssign:
//				emit(node, ByteCode.CallFunc, TokenType.Add.name());
//				break;
//			case MinusAssign:
//				emit(node, ByteCode.CallFunc, TokenType.Minus.name());
//				break;
//			case MultiplyAssign:
//				emit(node, ByteCode.CallFunc, TokenType.Multiply.name());
//				break;
//			case DivideAssign:
//				emit(node, ByteCode.CallFunc, TokenType.Divide.name());
//				break;
//			default:
//				throw new IllegalArgumentException();
//			}
//
//			//stack contains destinationValue
//		}
//
//		//store the top of the stack in a variable
//		emit(node, ByteCode.StoreVariable, statement.getDestinationVariable());
//
//		//clean up the stack
//		emit(node, ByteCode.Pop);
//
//	}
//
//	public void generateCode(Program.Node node, Expression expression) {
//
//		//expressions are either plain values or function calls
//		switch (expression.type) {
//		case Value:
//			//plain vallue emmit that
//			generateCode(node, expression.value);
//			break;
//		case FunctionCall:
//			//evaluate all parameter expressions
//			//which will push them to the stack
//			for (Expression param : expression.params) {
//				generateCode(node, param);
//			}
//
//			//if this function has a variable number of params,
//			//put the number of params that we passed t the stack
//			if (expression.function.getParamCount() == -1) {
//				emit(node, ByteCode.PushNumber, expression.params.size());
//			}
//
//			//and then call the function
//			emit(node, ByteCode.CallFunc, expression.function.getName());
//			break;
//		default:
//			break;
//		}
//
//	}
//
//	public void generateCode(Program.Node node, ValueNode value) {
//		//push value to on to the stack
//		switch (value.getValue().getType()) {
//		case NUMBER:
//			emit(node, ByteCode.PushNumber, value.getValue().getNumberValue());
//			break;
//		case STRING:
//			String id = program.registerString(value.getValue().getStringValue(), node.name, null, value.line_number,
//					false);
//			emit(node, ByteCode.PushString, id);
//			break;
//		case BOOL:
//			emit(node, ByteCode.PushBool, value.getValue().getBoolValue());
//			break;
//		case VARNAME:
//			emit(node, ByteCode.PushVariable, value.getValue().getVarName());
//			break;
//		case NULL:
//			emit(node, ByteCode.PushNull);
//			break;
//		default:
//			throw new IllegalArgumentException("Unrecognized ValueNode Type:" + value.getValue().getType());
//		}
//	}
//
//	public String registerLabel(String commentary) {
//		return "L" + (label_count++) + commentary;
//	}
//
//	public String registerLabel() {
//		return registerLabel("");
//	}
}
