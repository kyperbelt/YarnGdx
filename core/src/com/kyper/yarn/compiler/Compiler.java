package com.kyper.yarn.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import com.kyper.yarn.Program;
import com.kyper.yarn.Program.ByteCode;
import com.kyper.yarn.Program.Node;
import com.kyper.yarn.StringUtils;

public class Compiler extends YarnSpinnerParserBaseListener {

	private static Regex invalidNodeTitleNameRegex = new Regex("[\\[<>\\]{}\\|:\\s#\\$]");

	private int labelCount = 0;

	private Node currentNode;
	private boolean rawTextNode;
	private Program program;
	private final String fileName;
	private boolean containsImplicitStringTags;

	private HashMap<String, StringInfo> stringTable = new HashMap<String, Compiler.StringInfo>();
	private int stringCount = 0;

	public Compiler(String fileName) {
		program = new Program();
		this.fileName = fileName;
	}

	public static Status compileFile(Path path, Program program, HashMap<String, StringInfo> stringTable) {
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
			HashMap<String, StringInfo> stringTable) {
		CharStream input = CharStreams.fromString(text);

		YarnSpinnerLexer lexer = new YarnSpinnerLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		YarnSpinnerParser parser = new YarnSpinnerParser(tokens);

		// turning off the normal error listener and using ours
		parser.removeErrorListeners();
		parser.addErrorListener(ParserErrorListener.getInstance());

		lexer.removeErrorListeners();
		lexer.addErrorListener(LexerErrorListener.getInstance());

		ParseTree tree;
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
			throw new ParseException(e.getMessage());
			// endif // DEBUG
		}

		Compiler compiler = new Compiler(fileName);

		compiler.Compile(tree);

		program = compiler.getProgram();
		stringTable = compiler.stringTable;

		if (compiler.containsImplicitStringTags) {
			return Status.SucceededUntaggedStrings;
		} else {
			return Status.Succeeded;
		}
	}

	private static ArrayList<String> getTokensFromFile(Path path) {
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
			lineIDUsed = "%s-%s-%d";

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
	
	void Emit(Node node, ByteCode code,Operand operands...)
    {
        var instruction = new Instruction
        {
            Opcode = code
        };

        instruction.Operands.Add(operands);

        node.Instructions.Add(instruction);            
    }

	public boolean isRawTextNode() {
		return rawTextNode;
	}

	public void setRawTextNode(boolean rawTextNode) {
		this.rawTextNode = rawTextNode;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public Program getProgram() {
		return program;
	}

	public String getFileName() {
		return fileName;
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
