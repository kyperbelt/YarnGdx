package com.kyper.yarn;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.kyper.yarn.Dialogue.CommandResult;
import com.kyper.yarn.Dialogue.LineResult;
import com.kyper.yarn.Dialogue.NodeCompleteResult;
import com.kyper.yarn.Dialogue.OptionChooser;
import com.kyper.yarn.Dialogue.OptionResult;
import com.kyper.yarn.Library.FunctionInfo;
import com.kyper.yarn.Program.Instruction;
import com.kyper.yarn.Program.Node;

public class VirtualMachine {

	public static enum ExecutionState {
		/** Stopped */
		Stopped,
		/** Waiting on option selection */
		WaitingOnOptionSelection,
		/** Running */
		Running
	}
	
	protected static final String EXEC_COMPLETE = "execution_complete_command";

	private LineHandler line_handler;
	private OptionsHandler option_handler;
	private CommandHandler command_handler;
	private NodeCompleteHandler node_complte_handler;

	private Dialogue dialogue;
	private Program program;
	private State state = new State();

	private ExecutionState execution_state;

	private Node current_node;

	protected VirtualMachine(Dialogue d, Program p) {
		this.dialogue = d;
		this.program = p;
		execution_state = ExecutionState.Running;
	}

	/**
	 * set the current node the program is on log an error and stop execution if the
	 * node does not exist
	 */
	public boolean setNode(String name) {
		if (!program.nodes.containsKey(name)) {
			String error = "no node named " + name;
			dialogue.error_logger.log(error);
			setExecutionState(ExecutionState.Stopped);
			return false;
		}

		dialogue.debug_logger.log("Running node " + name);

		// clear the special variables
		dialogue.continuity.setValue(SpecialVariables.ShuffleOptions, new Value(false));

		current_node = program.nodes.get(name);
		resetState();
		state.current_node_name = name;
		return true;
	}

	public String currentNodeName() {
		return state.current_node_name;
	}

	public void stop() {
		setExecutionState(ExecutionState.Stopped);
	}

	public boolean hasOptions() {
		return state.current_options.size > 0;
	}

	/**
	 * Executes the next instruction in the current node
	 */
	protected void runNext() {

		if (execution_state == ExecutionState.WaitingOnOptionSelection) {
			dialogue.error_logger.log("Cannot continue running dialogue. Still waiting on option selection.");
			//execution_state = ExecutionState.Stopped;
			setExecutionState(ExecutionState.Stopped);
			
			return;
		}

		if (execution_state == ExecutionState.Stopped)
			setExecutionState(ExecutionState.Running);
		
		
		
		
		Instruction current_instruction = current_node.instructions.get(state.program_counter);

		runInstruction(current_instruction);
		
		//DEBUG instruction sets ---
		//System.out.println(current_instruction.toString(program, dialogue.library));

		state.program_counter++;
		
		if (state.program_counter >= current_node.instructions.size) {
			node_complte_handler.handle(new NodeCompleteResult(null));
			//execution_state = ExecutionState.Stopped;
		    setExecutionState(ExecutionState.Stopped);
			dialogue.debug_logger.log("Run complete");
			return;
		}


		

		
	}

	/**
	 * looks up the instruction number for a named label in the current node.
	 */
	protected int findInstructionForLabel(String label) {
		if (!current_node.labels.containsKey(label))
			throw new IndexOutOfBoundsException("Unknown label " + label + " in node " + state.current_node_name);
		return current_node.labels.get(label);
	}

	protected void runInstruction(Instruction instruction) {
		switch (instruction.getOperation()) {
		case Label:
			// label no-op, used as a destination for jumpto and jump
			break;
		case JumpTo:
			// jumps to named label
			state.program_counter = findInstructionForLabel((String) instruction.operandA());
			break;
		case RunLine:
			// looks up a string from the string table
			// and passes it to the client as a line
			String line_text = program.getString((String) instruction.operandA());
			if (line_text == null) {
				dialogue.error_logger.log("no loaded string table includes line " + instruction.operandA());
				break;
			}
			line_handler.handle(new LineResult(line_text));
			break;
		case RunCommand:
			// passes a string to the client as a custom command
			command_handler.handle(new CommandResult((String) instruction.operandA()));
			break;
		case PushString:
			// pushes a string value onto the stack. the operand is an index into
			// the string table, so thats looked up first
			state.pushValue(program.getString((String) instruction.operandA()));
			break;
		case PushNumber:
			// pushes a number onto the stack
			state.pushValue(Float.parseFloat(String.valueOf(instruction.operandA())));
			break;
		case PushBool:
			// pushes a boolean value onto the stack.
			state.pushValue(Boolean.parseBoolean(String.valueOf(instruction.operandA())));
			break;
		case PushNull:
			// pushes a null value onto the stack
			state.pushValue(Value.NULL);
			break;
		case JumpIfFalse:
			// jumps to a named label if the value of the top of the stack
			// evaluates to the boolean value 'false'
			if (!state.peekValue().asBool()) {
				state.program_counter = findInstructionForLabel((String) instruction.operandA());
			}
			break;
		case Jump:
			// jumps to a label whose name is on the stack
			String jump_dest = state.peekValue().asString();
			state.program_counter = findInstructionForLabel(jump_dest);

			break;
		case Pop:
			// pop a value from the stack
			state.popValue();
			break;
		case CallFunc:
			// call a function, whose parameters are expected to
			// be on the stack. pushes the functions return value,
			// if it returns one
			String function_name = (String) instruction.operandA();

			FunctionInfo function = dialogue.library.getFunction(function_name);

			{
				int param_count = function.getParamCount();
		
				// if this function takes -1 params, it is variadic.
				// expect the compiler to have palced the number of params
				// actually passed at the top of the stack.
				if (param_count == -1) {
					param_count = (int) state.popValue().asNumber();
				}
		
				Value result;
		
				if (param_count == 0) {
					result = function.invoke();
				} else {
					// get the parameters, which are pushed in reverse
					Value[] params = new Value[param_count];
					for (int i = param_count - 1; i >= 0; i--) {
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
			String var_name = (String)instruction.operandA();
			Value loaded = dialogue.continuity.getValue(var_name);
			state.pushValue(loaded);
			break;
		case StoreVariable:
			// store the top value on the stack in a variable
			Value topval = state.peekValue();
			String destinationVarName = String.valueOf(instruction.operandA());
			dialogue.continuity.setValue(destinationVarName, topval);
			break;
		case Stop:
			// stop execution immidiately and report it
			node_complte_handler.handle(new NodeCompleteResult(null));
			command_handler.handle(new CommandResult(EXEC_COMPLETE));
			
			//execution_state = ExecutionState.Stopped;
			setExecutionState(ExecutionState.Stopped);
			break;
		case RunNode:
			// run a node
			String node_name;

			if (instruction.operandA() == null || ((String)instruction.operandA()).isEmpty()) {
				// get a string from the stack, and jump to a node with that name
				node_name = state.peekValue().asString();
			} else {
				// jump straight to the node
				node_name = (String) instruction.operandA();
			}

			node_complte_handler.handle(new NodeCompleteResult(node_name));
			setNode(node_name);

			break;
		case AddOption:
			// add an option to the current state
			Entry<String, String> option = new Entry<String, String>();
			option.key = (String) instruction.operandA();
			option.value = (String) instruction.operandB();
			state.current_options.add(option);
			break;
		case ShowOptions:
			// if we have no options to show, immidiately stop
			if (state.current_options.size == 0) {
				node_complte_handler.handle(new NodeCompleteResult(null));
				//execution_state = ExecutionState.Stopped;
				setExecutionState(ExecutionState.Stopped);
				break;
			}

			// if we have a single option, and it has no label, select it and continue
			// execution
			if (state.current_options.size == 1 && state.current_options.get(0).key == null) {
				String dest = state.current_options.first().value;
				state.pushValue(dest);
				state.current_options.clear();
				break;
			}

			if (dialogue.continuity.getValue(SpecialVariables.ShuffleOptions).asBool()) {
				// shuffle the dialogue options if needed
				int n = state.current_options.size;
				for (int opt1 = 0; opt1 < n; opt1++) {
					int opt2 = opt1 + (int) (MathUtils.random() * (n - opt1));
					state.current_options.swap(opt1, opt2);
				}
			}

			// present options to user to choose
			Array<String> option_strings = new Array<String>();
			for (Entry<String, String> op : state.current_options) {
				option_strings.add(program.getString(op.key));
			}

			// cant continue until client chooses option
			setExecutionState(ExecutionState.WaitingOnOptionSelection);

			option_handler.handle(new OptionResult(option_strings, new OptionChooser() {

				@Override
				public void choose(int selected_option_index) {
					// we now know what number option was selected; push the corresponding node name
					// to the stack
					String dest_node = state.current_options.get(selected_option_index).value;
					state.pushValue(dest_node);

					// we no longer need the accum list of optionsl clear it so that ist
					// ready for the next one
					state.current_options.clear();

					// we can now keep running
					setExecutionState(ExecutionState.Running);

				}
			}));

			break;

		default:
			// no acepted bytecode, stop the program
			// and throw exeption
			//execution_state = ExecutionState.Stopped;
			setExecutionState(ExecutionState.Stopped);
			throw new IllegalArgumentException(instruction.getOperation().name());

		}
	}

	public LineHandler getLineHandler() {
		return line_handler;
	}

	public void setLineHandler(LineHandler line_handler) {
		this.line_handler = line_handler;
	}

	public OptionsHandler getOptionsHandler() {
		return option_handler;
	}

	public void setOptionsHandler(OptionsHandler options_handler) {
		this.option_handler = options_handler;
	}

	public CommandHandler getCommandHandler() {
		return command_handler;
	}

	public void setCommandHandler(CommandHandler command_handler) {
		this.command_handler = command_handler;
	}

	public NodeCompleteHandler getCompleteHandler() {
		return node_complte_handler;
	}

	public void setCompleteHandler(NodeCompleteHandler node_complete_handler) {
		this.node_complte_handler = node_complete_handler;
	}

	public ExecutionState getExecutionState() {
		return execution_state;
	}

	private void setExecutionState(ExecutionState exec_state) {
		this.execution_state = exec_state;
		if (execution_state == ExecutionState.Stopped)
			resetState();
	}

	public void resetState() {
		state = new State();
	}

	protected class State {
		// the name of the node that we are currently on
		public String current_node_name;

		// the instruction number in the current node
		public int program_counter = 0;

		// list of options, where each option = <string id,destination node>
		public Array<Entry<String, String>> current_options = new Array<Entry<String, String>>();

		// the value stack
		private Array<Value> stack = new Array<Value>();

		/**
		 * push a value on to the value stack
		 */
		public void pushValue(Object o) {
			if (o instanceof Value)
				stack.add((Value) o);
			else
				stack.add(new Value(o));
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

	public static interface LineHandler {
		public void handle(LineResult line);
	}

	public static interface OptionsHandler {
		public void handle(OptionResult options);
	}

	public static interface CommandHandler {
		public void handle(CommandResult command);
	}

	public static interface NodeCompleteHandler {
		public void handle(NodeCompleteResult compelte);
	}

	protected static class SpecialVariables {
		public static final String ShuffleOptions = "$Yarn.ShuffleOptions";
	}

}
