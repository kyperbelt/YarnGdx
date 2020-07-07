package com.kyper.yarn;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.kyper.yarn.Dialogue.Command;
import com.kyper.yarn.Dialogue.Line;
import com.kyper.yarn.Dialogue.Option;
import com.kyper.yarn.Dialogue.OptionSet;
import com.kyper.yarn.Library.FunctionInfo;
import com.kyper.yarn.Program.Instruction;
import com.kyper.yarn.Program.Node;

public class VirtualMachine {

	public static enum ExecutionState {
		/** Stopped */
		Stopped,
		/** Waiting on option selection */
		WaitingOnOptionSelection,
		/** Suspended in the middle of execution */
		Suspended,
		/** Running */
		Running
	}


	public static final String EXEC_COMPLETE = "execution_complete_command";

	protected LineHandler lineHandler;
	protected OptionsHandler optionHandler;
	protected CommandHandler commandHandler;
	protected NodeStartHandler nodeStartHandler;
	protected NodeCompleteHandler nodeCompleteHandler;
	protected DialogueCompleteHandler dialogueCompleteHandler;

	private Dialogue dialogue;
	private Program program;
	private State state;

	private ExecutionState executionState;

	private Node currentNode;

	protected VirtualMachine(Dialogue d) {
		this.dialogue = d;
		state = new State();
	}
	
	public void setProgram(Program program) {
		this.program = program;
	}

//	protected VirtualMachine(Dialogue d, Program p) {
//		this.dialogue = d;
//		this.program = p;
//		state = new State();
//		execution_state = ExecutionState.Running;
//	}

	/**
	 * set the current node the program is on log an error and stop execution if the
	 * node does not exist
	 */
	public boolean setNode(String name) {
		if (program == null || program.nodes.size() == 0)
			throw new DialogueException(StringUtils.format("Cannot load node %s: No nodes have been loaded.", name));

		if (!program.nodes.containsKey(name)) {
			setExecutionState(ExecutionState.Stopped);
			throw new DialogueException(StringUtils.format("No node named [%s] has been loaded.", name));
		}

		dialogue.debug_logger.log("Running node " + name);

		// clear the special variables
//		dialogue.continuity.setValue(SpecialVariables.ShuffleOptions, new Value(false));

		currentNode = program.nodes.get(name);
		resetState();
		state.currentNodeName = name;
		return true;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public String currentNodeName() {
		return state.currentNodeName;
	}

	public void stop() {
		setExecutionState(ExecutionState.Stopped);
		currentNode = null;
	}

	public void setSelectedOption(int selectedOptionId) {
		if (executionState != ExecutionState.WaitingOnOptionSelection) {
			throw new DialogueException(StringUtils.format(
					"SelectedOption was called, but Dialogue wasn't waiting for a selection. This method should only be called after the Dialogue is waiting for the user to select an option."));
		}

		if (selectedOptionId < 0 || selectedOptionId >= state.current_options.size()) {
			throw new IllegalArgumentException(
					StringUtils.format("%d is not a valid option ID(expected a number between 0 and %d)",
							selectedOptionId, state.current_options.size()));
		}

		String destination = state.current_options.get(selectedOptionId).getValue();
		state.pushValue(destination);
		state.current_options.clear();
		// We're no longer in the WaitingForOptions state; we are now
		// instead Suspended
		setExecutionState(ExecutionState.Suspended);
	}

	protected boolean hasOptions() {
		return state.current_options.size() > 0;
	}

	public void continueRunning() {
		if (currentNode == null)
			throw new DialogueException("Cannot continue running dialogue. No node has been selected.");
		if (executionState == ExecutionState.WaitingOnOptionSelection)
			throw new DialogueException("Cannot continue running dialogue. Still waiting on option selection.");
		if (lineHandler == null)
			throw new DialogueException("Cannot continue running dialogue. lineHandler has not been set.");

		if (optionHandler == null)
			throw new DialogueException("Cannot continue running dialogue. optionHandler has not been set.");

		if (commandHandler == null)
			throw new DialogueException("Cannot continue running dialogue. commandHandler has not been set.");

		if (nodeStartHandler == null)
			throw new DialogueException("Cannot continue running dialogue. nodeStartHandler has not been set.");

		if (nodeCompleteHandler == null)
			throw new DialogueException("Cannot continue running dialogue. nodeCompleteHandler has not been set.");

		setExecutionState(ExecutionState.Running);

		// Execute instructions until something forces us to stop
		while (executionState == ExecutionState.Running) {
			Instruction currentInstruction = currentNode.instructions.get(state.program_counter);

			runInstruction(currentInstruction);

			state.program_counter++;

			if (state.program_counter >= currentNode.instructions.size()) {
				nodeCompleteHandler.handle(currentNode.name);
				executionState = ExecutionState.Stopped;
				dialogueCompleteHandler.handle();
				dialogue.debug_logger.log("Run complete.");
			}
		}
	}

//	/**
//	 * Executes the next instruction in the current node
//	 */
//	public void runNext() {
//
//		if (execution_state == ExecutionState.WaitingOnOptionSelection) {
//			dialogue.error_logger.log("Cannot continue running dialogue. Still waiting on option selection.");
//			// execution_state = ExecutionState.Stopped;
//			setExecutionState(ExecutionState.Stopped);
//
//			return;
//		}
//
//		if (execution_state == ExecutionState.Stopped)
//			setExecutionState(ExecutionState.Running);
//
//		Instruction current_instruction = currentNode.instructions.get(state.program_counter);
//
//		runInstruction(current_instruction);
//
//		// DEBUG instruction sets ---
//		// System.out.println(current_instruction.toString(program, dialogue.library));
//
//		state.program_counter++;
//
//		if (state.program_counter >= currentNode.instructions.size()) {
//			nodeCompleteHandler.handle(new NodeCompleteResult(null));
//			// execution_state = ExecutionState.Stopped;
//			setExecutionState(ExecutionState.Stopped);
//			dialogue.debug_logger.log("Run complete");
//			return;
//		}
//
//	}

	/**
	 * looks up the instruction number for a named label in the current node.
	 */
	public int findInstructionForLabel(String label) {
		if (!currentNode.labels.containsKey(label))
			throw new IndexOutOfBoundsException("Unknown label " + label + " in node " + state.currentNodeName);
		return currentNode.labels.get(label);
	}

	public void runInstruction(Instruction instruction) {
		switch (instruction.operation) {
		// TODO:DEPRECATED --- remove(first figure out why)
		case Label:
			// label no-op, used as a destination for jumpto and jump
			break;
		case JumpTo:
			// jumps to named label
			state.program_counter = findInstructionForLabel((String) instruction.operands.get(0).getStringValue()) - 1;
			break;
		case RunLine: {
			// looks up a string from the string table
			// and passes it to the client as a line
			String stringKey = (String) instruction.operands.get(0).getStringValue();

			Line line = new Line(stringKey);

			// The second operand, if provided (compilers prior
			// to v1.1 don't include it), indicates the number
			// of expressions in the line. We need to pop these
			// values off the stack and deliver them to the
			// line handler.
			if (instruction.operands.size() > 1) {
				// TODO: we only have float operands, which is
				// unpleasant. we should make 'int' operands a
				// valid type, but doing that implies that the
				// language differentiates between floats and
				// ints itself. something to think about.
				int expressionCount = (int) instruction.operands.get(1).getFloatValue();

				String[] strings = new String[expressionCount];

				for (int expressionIndex = expressionCount - 1; expressionIndex >= 0; expressionIndex--) {
					strings[expressionIndex] = state.popValue().asString();
				}

				line.substitutions = strings;
			}

			{
				HandlerExecutionType pause = lineHandler.handle(line);

				if (pause == HandlerExecutionType.PauseExecution) {
					setExecutionState(ExecutionState.Suspended);
				}
			}
		}
			break;
//			String line_text = program.getString();
//			if (line_text == null) {
//				dialogue.error_logger.log("no loaded string table includes line " + instruction.operandA());
//				break;
//			}
//			lineHandler.handle(new LineResult(line_text));

		case RunCommand:
			// passes a string to the client as a custom command

			String commandText = instruction.operands.get(0).getStringValue();

			// The second operand, if provided (compilers prior
			// to v1.1 don't include it), indicates the number
			// of expressions in the command. We need to pop
			// these values off the stack and deliver them to
			// the line handler.
			if (instruction.operands.size() > 1) {
				// TODO: we only have float operands, which is
				// unpleasant. we should make 'int' operands a
				// valid type, but doing that implies that the
				// language differentiates between floats and
				// ints itself. something to think about.
				int expressionCount = (int) instruction.operands.get(1).getFloatValue();

				//String[] strings = new String[expressionCount];

				// Get the values from the stack, and
				// substitute them into the command text
				for (int expressionIndex = expressionCount - 1; expressionIndex >= 0; expressionIndex--) {
					String substitution = state.popValue().asString();

					commandText = commandText.replace("{" + expressionIndex + "}", substitution);
				}
			}

		{
			Command command = new Command(commandText);
			HandlerExecutionType pause = commandHandler.handle(command);
			if (pause == HandlerExecutionType.PauseExecution) {
				setExecutionState(ExecutionState.Suspended);
			}
		}
			break;
		case PushString:
			// pushes a string value onto the stack. the operand is an index into
			// the string table, so thats looked up first
			state.pushValue(instruction.operands.get(0).getStringValue());
			break;
		case PushNumber:
			// pushes a number onto the stack
			state.pushValue(instruction.operands.get(0).getFloatValue());
			break;
		case PushBool:
			// pushes a boolean value onto the stack.
			state.pushValue(instruction.operands.get(0).getBoolValue());
			break;
		case PushNull:
			// pushes a null value onto the stack
			state.pushValue(Value.NULL);
			break;
		case JumpIfFalse:
			// jumps to a named label if the value of the top of the stack
			// evaluates to the boolean value 'false'
			if (!state.peekValue().asBool()) {
				state.program_counter = findInstructionForLabel(instruction.operands.get(0).getStringValue()) - 1;
			}
			break;
		case Jump:
			// jumps to a label whose name is on the stack
			String jump_dest = state.peekValue().asString();
			state.program_counter = findInstructionForLabel(jump_dest) - 1;

			break;
		case Pop:
			// pop a value from the stack
			state.popValue();
			break;
		case CallFunc:
			// call a function, whose parameters are expected to
			// be on the stack. pushes the functions return value,
			// if it returns one
			String function_name = instruction.operands.get(0).getStringValue();

			FunctionInfo function = dialogue.library.getFunction(function_name);

		{
			int expectedParamCount = function.getParamCount();

			int actualParamCount = (int) state.popValue().asNumber();

			// if this function takes -1 params, it is variadic.
			// expect the compiler to have palced the number of params
			// actually passed at the top of the stack.
			if (expectedParamCount == -1) {
				expectedParamCount = actualParamCount;
			}

			if (expectedParamCount != actualParamCount) {
				throw new IllegalStateException(StringUtils.format("Function %s expected %d, but received %d",
						function.getName(), expectedParamCount, actualParamCount));
			}

			Value result;

			if (actualParamCount == 0) {
				result = function.invoke();
			} else {
				// get the parameters, which are pushed in reverse
				Value[] params = new Value[actualParamCount];
				for (int i = actualParamCount - 1; i >= 0; i--) {
					params[i] = state.popValue();
				}

				// invoke the function
				result = function.invokeWithArray(params);
			}

			// if the function returns a value push it
			if (function.returnsValue()) {
				state.pushValue(result);
			}

		}
			break;
		case PushVariable:
			// get contents of a variable and push it to the stack
			String var_name = (String) instruction.operands.get(0).getStringValue();
			Value loaded = dialogue.continuity.getValue(var_name);
			state.pushValue(loaded);
			break;
		case StoreVariable:
			// store the top value on the stack in a variable
			Value topval = state.peekValue();
			String destinationVarName = instruction.operands.get(0).getStringValue();
			dialogue.continuity.setValue(destinationVarName, topval);
			break;
		case Stop:
			// stop execution immidiately and report it
			// command_handler.handle(new CommandResult(EXEC_COMPLETE));
			nodeCompleteHandler.handle(currentNode.name);
			dialogueCompleteHandler.handle();
			// execution_state = ExecutionState.Stopped;
			setExecutionState(ExecutionState.Stopped);
			break;
		case RunNode:
			// run a node
			String node_name;

			if (instruction.operands.size() == 0 || (instruction.operands.get(0).getStringValue().isEmpty())) {
				// get a string from the stack, and jump to a node with that name
				node_name = state.peekValue().asString();
			} else {
				// jump straight to the node
				node_name = instruction.operands.get(0).getStringValue();
			}

		{

			HandlerExecutionType pause = nodeCompleteHandler.handle(currentNode.name);
			setNode(node_name);
			// Decrement program counter here, because it will
			// be incremented when this function returns, and
			// would mean skipping the first instruction
			state.program_counter -= 1;
			if (pause == HandlerExecutionType.PauseExecution) {
				setExecutionState(ExecutionState.Suspended);
			}
		}
			break;
		case AddOption: {
			/// - AddOption
			/**
			 * Add an option to the current state.
			 */

			Line line = new Line(instruction.operands.get(0).getStringValue());

			if (instruction.operands.size() > 2) {
				// TODO: we only have float operands, which is
				// unpleasant. we should make 'int' operands a
				// valid type, but doing that implies that the
				// language differentiates between floats and
				// ints itself. something to think about.

				// get the number of expressions that we're
				// working with out of the third operand
				int expressionCount = (int) instruction.operands.get(2).getFloatValue();

				String[] strings = new String[expressionCount];

				// pop the expression values off the stack in
				// reverse order, and store the list of substitutions
				for (int expressionIndex = expressionCount - 1; expressionIndex >= 0; expressionIndex--) {
					String substitution = state.popValue().asString();
					strings[expressionIndex] = substitution;
				}

				line.substitutions = strings;
			}
			state.current_options.add(new SimpleEntry<Line, String>(line, // line to show
					instruction.operands.get(1).getStringValue() // node name
			));

			break;
		}
		case ShowOptions: {
			/// - ShowOptions
			/**
			 * If we have no options to show, immediately stop.
			 */
			if (state.current_options.size() == 0) {
				executionState = ExecutionState.Stopped;
				dialogueCompleteHandler.handle();
				break;
			}

			// Present the list of options to the user and let them pick
			ArrayList<Option> optionChoices = new ArrayList<Option>();

			for (int optionIndex = 0; optionIndex < state.current_options.size(); optionIndex++) {
				SimpleEntry<Line, String> option = state.current_options.get(optionIndex);
				optionChoices.add(new Option(option.getKey(), optionIndex, option.getValue()));
			}

			// We can't continue until our client tell us which option to pick
			executionState = ExecutionState.WaitingOnOptionSelection;

			// Pass the options set to the client, as well as a delegate for them to call
			// when the
			// user has made a selection

			optionHandler.handle(new OptionSet(optionChoices.toArray(new Option[0])));

			break;
		}

		default:
			// no acepted bytecode, stop the program
			// and throw exeption
			// execution_state = ExecutionState.Stopped;
			setExecutionState(ExecutionState.Stopped);
			throw new IllegalArgumentException(StringUtils.format("Uknown ByteCode %s",instruction.operation.name()));

		}
	}

	public ExecutionState getExecutionState() {
		return executionState;
	}

	private void setExecutionState(ExecutionState exec_state) {
		this.executionState = exec_state;
		if (executionState == ExecutionState.Stopped)
			resetState();
	}

	public void resetState() {
		state = new State();
	}

	public static Program combinePrograms(Program... programs) {
		if (programs.length == 0)
			throw new IllegalArgumentException("combinePrograms - At least one program must be provided");
		Program p = new Program();
		for (Program otherProgram : programs) {
			for (Entry<String, Node> otherNode : otherProgram.nodes.entrySet()) {
				if (p.nodes.containsKey(otherNode.getKey())) {
					throw new IllegalStateException(
							String.format("This program already contains a node names %s", otherNode.getKey()));

				}
				p.nodes.put(otherNode.getKey(), otherNode.getValue().clone());
			}

		}
		return p;
	}

	public static enum TokenType {

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

	public class State {
		// the name of the node that we are currently on
		public String currentNodeName;

		// the instruction number in the current node
		public int program_counter = 0;

		// list of options, where each option = <string id,destination node>
		public ArrayList<SimpleEntry<Line, String>> current_options = new ArrayList<SimpleEntry<Line, String>>();

		// the value stack
		private ArrayDeque<Value> stack = new ArrayDeque<Value>();

		/**
		 * push a value on to the value stack
		 */
		public void pushValue(Object o) {
			if (o instanceof Value)
				stack.push((Value) o);
			else
				stack.push(new Value(o));
		}

		/**
		 * pop a value from the value stack
		 *
		 * @return
		 */
		public Value popValue() {
			return stack.pop();
		}

		/**
		 * peek at a value from the stack
		 *
		 * @return
		 */
		public Value peekValue() {
			return stack.peek();
		}

		/**
		 * clear the value stack
		 */
		public void clearValueStack() {
			stack.clear();
		}
	}

	// HANDLERS

	/// <summary>
	/// Used as a return type by handlers (such as the <see
	/// cref="LineHandler"/>) to indicate whether a <see
	/// cref="Dialogue"/> should suspend execution, or continue
	/// executing, after it has called the handler.
	/// </summary>
	/// <seealso cref="LineHandler"/>
	/// <seealso cref="CommandHandler"/>
	/// <seealso cref="NodeCompleteHandler"/>
	public enum HandlerExecutionType {

		/// <summary>
		/// Indicates that the <see cref="Dialogue"/> should suspend
		/// execution.
		/// </summary>
		PauseExecution,

		/// <summary>
		/// Indicates that the <see cref="Dialogue"/> should continue
		/// execution.
		/// </summary>
		ContinueExecution,
	}

	public static interface LineHandler {
		public HandlerExecutionType handle(Line line);
	}

	public static interface OptionsHandler {
		public void handle(OptionSet options);
	}

	public static interface CommandHandler {
		public HandlerExecutionType handle(Command command);
	}

	public static interface NodeCompleteHandler {
		public HandlerExecutionType handle(String nodeName);
	}

	public static interface DialogueCompleteHandler {
		public void handle();
	}

	public static interface NodeStartHandler {
		public HandlerExecutionType handle(String nodeName);
	}

	public static class SpecialVariables {
		public static final String ShuffleOptions = "$Yarn.ShuffleOptions";
	}

}
