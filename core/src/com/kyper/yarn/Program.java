package com.kyper.yarn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.esotericsoftware.jsonbeans.Json;
import com.kyper.yarn.Library.FunctionInfo;

public class Program {
	public String name;
	public HashMap<String, String> strings = new HashMap<String, String>();
//	public HashMap<String, LineInfo> line_info = new HashMap<String, Program.LineInfo>();

	public HashMap<String, Node> nodes = new HashMap<String, Node>();

//	private int string_count = 0;

	public Map<String, Node> getNodes() {
		return nodes;
	}

	/// Loads a new string table into the program.
	/**
	 * The string table is merged with any existing strings, with the new table
	 * taking precedence over the old.
	 */
//	public void loadStrings(Map<String, String> new_strings) {
//		for (Map.Entry<String, String> line : new_strings.entrySet()) {
//			strings.put(line.getKey(), line.getValue());
//		}
//	}

//	public String registerString(String string, String node_name, String line_id, int line_number,
//			boolean localisable) {
//		String key;
//
//		if (line_id == null)
//			key = String.format("%1$s - %2$s", node_name, string_count++);
//		else
//			key = line_id;
//
//		// its not int he list; append it
//		strings.put(key, string);
//
//		if (localisable) {
//			// additionally, keep info about this string around
//			line_info.put(key, new LineInfo(node_name, line_number));
//		}
//
//		return key;
//	}
	
	protected List<String> getTagsForNode(String name){
		return nodes.get(name).tags;
	}
	
    public void mergeFrom(Program other) {
        if (other == null) {
          return;
        }
        if (other.name.length() != 0) {
          this.name = other.name;
        }
        nodes.putAll(other.nodes);
      }

	public String getString(String key) {
		String value = null;
		if (strings.containsKey(key))
			value = strings.get(key);
		return value;
	}

	public String dumpCode(Library lib) {
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Node> entry : nodes.entrySet()) {
			sb.append("Node \n" + entry.getKey() + ":");
			int instruction_count = 0;

			ArrayList<Instruction> instructions = entry.getValue().instructions;
			for (int i = 0; i < instructions.size(); i++) {
				Instruction instruction = instructions.get(i);
				String instruction_text = null;

				if (instruction.operation == ByteCode.Label) {
					instruction_text = instruction.toString(this, lib);
				} else {
					instruction_text = "    " + instruction.toString(this, lib);
				}

				String preface;
				
				if (instruction_count % 5 == 0 || instruction_count == entry.getValue().instructions.size() - 1) {
					preface = String.format("%1$6s", instruction_count + "");
				} else {
					preface = String.format("%1$6s    ", " ");
				}

				sb.append(preface + instruction_text + "\n");
				instruction_count++;
			}
			sb.append("\n\n");
		}
//
//		for (Map.Entry<String, String> entry : strings.entrySet()) {
//			LineInfo line_info = this.line_info.get(entry.getKey());
//			if (line_info == null)
//				continue;
//			sb.append(String.format("%1$s: %2$s  (%3$s:%4$s)\n", entry.getKey(), entry.getValue(),
//					line_info.getNodeName(), line_info.getLineNumber()));
//		}

		return sb.toString();
	}

	public String getTextForNode(String node_name) {
		String key = nodes.get(node_name).sourceStringId;
		return this.getString(key == null ? "" : key);
	}

	public void include(Program other_program) {
		for (Map.Entry<String, Node> other : other_program.nodes.entrySet()) {
			if (nodes.containsKey(other.getKey())) {
				throw new IllegalStateException(
						String.format("This program already contains a node named %s", other.getKey()));
			}

			nodes.put(other.getKey(), other.getValue());
		}

		for (Map.Entry<String, String> other : other_program.strings.entrySet()) {
			// TODO: this seems fishy -- maybe check strings map instead?
			if (nodes.containsKey(other.getKey())) {
				throw new IllegalStateException(
						String.format("This program already contains a string with key %s", other.getKey()));
			}
			strings.put(other.getKey(), other.getValue());
		}
	}

	// When saving programs, we want to save only lines that do NOT have a line:
	// key.
	// This is because these lines will be loaded from a string table.
	// However, because certain strings (like those used in expressions) won't have
	// tags,
	// they won't be included in generated string tables, so we need to export them
	// here.

	// We do this by NOT including the main strings list, and providing a property
	// that gets serialised as "strings" in the output, which includes all untagged
	// strings.
	public HashMap<String, String> untaggedStrings() {
		HashMap<String, String> result = new HashMap<String, String>();

		for (Map.Entry<String, String> line : strings.entrySet()) {
			if (line.getKey().startsWith("line:"))// TODO ============maybe change to Line since thats what the parser
													// spits out
				continue;
			result.put(line.getKey(), line.getValue());
		}
		return result;
	}

	public static class LineInfo {
		private int line_number;
		private String node_name;

		public LineInfo(String node_name, int line_number) {
			this.node_name = node_name;
			this.line_number = line_number;
		}

		public int getLineNumber() {
			return line_number;
		}

		public String getNodeName() {
			return node_name == null ? "null" : node_name;
		}
	}
	

//	public static class Node {
//
//		public ArrayList<Instruction> instructions = new ArrayList<Program.Instruction>();
//		public String name;
//
//		// the entry in the programs string table that contains
//		// the original text of this node. null if not available
//		public String source_string_id = null;
//
//		public HashMap<String, Integer> labels = new HashMap<String, Integer>();
//
//		public ArrayList<String> tags;
//
//	}
//	
	public static class Node {

		public String name;
		public ArrayList<Instruction> instructions = new ArrayList<Instruction>();
		public HashMap<String, Integer> labels = new HashMap<String, Integer>();
		public ArrayList<String> tags = new ArrayList<String>();
		public String sourceStringId = null;

		public Node() {
			OnConstruction();
		}

		public void OnConstruction() {
			// DO somthing on create?
		}

		public Node(Node other) {
			name = other.name;
			instructions.addAll(instructions);
			labels.putAll(other.labels);
			tags.addAll(other.tags);
			sourceStringId = other.sourceStringId;
		}

		public Node clone() {
			return new Node(this);
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Node))
				return false;
			Node o = (Node) other;
			if (this.name != o.name)
				return false;
			if (!this.instructions.equals(o.instructions))
				return false;
			if (!this.labels.equals(o.labels))
				return false;
			if (!this.tags.equals(o.tags))
				return false;
			if (!this.sourceStringId.equals(o.sourceStringId))
				return false;
			return true;
		}

		@Override
		public int hashCode() {

			int hash = 1;
			if (name.length() != 0)
				hash ^= name.hashCode();
			hash ^= instructions.hashCode();
			hash ^= labels.hashCode();
			hash ^= tags.hashCode();
			if (sourceStringId.length() != 0)
				hash ^= sourceStringId.hashCode();

			return hash;
		}

		@Override
		public String toString() {
			Json json = new Json();
			return json.prettyPrint(this);
		}

		public int calculateSize() {
			throw new UnsupportedOperationException("Not Implemented!");
//	      int size = 0;
//	      if (name.length() != 0) {
//	        size += 1 + name.getBytes().length;
//	      }
//	      size += instructions
//	      size += labels_.CalculateSize(_map_labels_codec);
//	      size += tags_.CalculateSize(_repeated_tags_codec);
//	      if (sourceStringId.length() != 0) {
//	        size += 1 + sourceStringId.getBytes().length;
//	      }
//	      return size;
		}

		public void mergeFrom(Node other) {
			if (other == null) {
				return;
			}
			if (other.name.length() != 0) {
				this.name = other.name;
			}
			instructions.addAll(other.instructions);
			labels.putAll(other.labels);
			tags.addAll(other.tags);
			if (other.sourceStringId.length() != 0) {
				sourceStringId = other.sourceStringId;
			}
		}

	}

	public static enum ByteCode {
		/// opA = string: label name
		Label,
		/// opA = string: label name
		JumpTo,
		/// peek string from stack and jump to that label
		Jump,
		/// opA = int: string number
		RunLine,
		/// opA = string: command text
		RunCommand,
		/// opA = int: string number for option to add
		AddOption,
		/// present the current list of options, then clear the list; most recently
		/// selected option will be on the top of the stack
		ShowOptions,
		/// opA = int: string number in table; push string to stack
		PushString,
		/// opA = float: number to push to stack
		PushNumber,
		/// opA = int (0 or 1): bool to push to stack
		PushBool,
		/// pushes a null value onto the stack
		PushNull,
		/// opA = string: label name if top of stack is not null, zero or false, jumps
		/// to that label
		JumpIfFalse,
		/// discard top of stack
		Pop,
		/// opA = string; looks up function, pops as many arguments as needed, result is
		/// pushed to stack
		CallFunc,
		/// opA = name of variable to get value of and push to stack
		PushVariable,
		/// opA = name of variable to store top of stack in
		StoreVariable,
		/// stops execution
		Stop,
		/// run the node whose name is at the top of the stack
		RunNode
	}

	public static class Operand {
		public enum ValueOneofCase {
			None, StringValue, BoolValue, FloatValue,
		}

		private Object value;
		private ValueOneofCase valueCase = ValueOneofCase.None;

		public Operand(String value) {
			setStringValue(value);
		}
		
		public Operand(float value) {
			setFloatValue(value);
		}
		
		public Operand(boolean value) {
			setBoolValue(value);
		}

		public Operand(Operand other) {
			switch (other.getValueCase()) {
			case StringValue:
				setStringValue(other.getStringValue());
				break;
			case BoolValue:
				setBoolValue(other.getBoolValue());
				break;
			case FloatValue:
				setFloatValue(other.getFloatValue());
				break;
			default:
				throw new IllegalArgumentException(
						StringUtils.format("Unknown Operand ValueCase [%s]", other.getValueCase().name()));
			}
		}

		public Operand clone() {
			return new Operand(this);
		}

		public String getStringValue() {
			return valueCase == ValueOneofCase.StringValue ? (String) value : "";
		}

		public Operand setStringValue(String string) {
			value = string == null ? "null" : string;
			valueCase = ValueOneofCase.StringValue;
			return this;
		}

		public boolean getBoolValue() {

			return valueCase == ValueOneofCase.BoolValue ? (boolean) value : false;
		}

		public Operand setBoolValue(boolean bool) {
			value = bool;
			valueCase = ValueOneofCase.BoolValue;
			return this;
		}

		public float getFloatValue() {
			return valueCase == ValueOneofCase.FloatValue ? (float) value : 0.0f;
		}

		public Operand setFloatValue(float f) {
			value = f;
			valueCase = ValueOneofCase.FloatValue;
			return this;
		}

		public ValueOneofCase getValueCase() {
			return valueCase;
		}

		public void clearValue() {
			valueCase = ValueOneofCase.None;
			value = null;
		}

		public boolean equals(Object other) {
			if (!(other instanceof Operand))
				return false;
			Operand o = (Operand) other;
			if (o.getValueCase() != this.getValueCase())
				return false;
			if (this.getStringValue() == o.getStringValue())
				return true;
			if (this.getBoolValue() == o.getBoolValue())
				return true;
			if (this.getFloatValue() == o.getFloatValue())
				return false;
			return false;
		}

		@Override
		public int hashCode() {
			int hash = 1;
			if (valueCase == ValueOneofCase.StringValue)
				hash ^= getStringValue().hashCode();
			if (valueCase == ValueOneofCase.BoolValue)
				hash ^= Boolean.hashCode(getBoolValue());
			if (valueCase == ValueOneofCase.FloatValue)
				hash ^= Float.hashCode(getFloatValue());
			hash ^= valueCase.hashCode();

			return hash;
		}

		@Override
		public String toString() {
			return StringUtils.format("Operand[%s:%s]", valueCase.name(), value);
		}

		public int calculateSize() {
			int size = 0;
			if (valueCase == ValueOneofCase.StringValue) {
				size += 1 + getStringValue().length();
			}
			if (valueCase == ValueOneofCase.BoolValue) {
				size += 1 + 1;
			}
			if (valueCase == ValueOneofCase.FloatValue) {
				size += 1 + 4;
			}
			return size;
		}

		public void mergeFrom(Operand other) {
			if (other == null) {
				return;
			}
			switch (other.getValueCase()) {
			case StringValue:
				setStringValue(other.getStringValue());
				break;
			case BoolValue:
				setBoolValue(other.getBoolValue());
				break;
			case FloatValue:
				setFloatValue(other.getFloatValue());
				break;
			default:
				throw new IllegalArgumentException(
						StringUtils.format("Unknown Operand ValueCase [%s]", other.getValueCase().name()));
			}
		}

	}

	public static class Instruction {
		public ByteCode operation;
		public ArrayList<Operand> operands = new ArrayList<Program.Operand>();

		public Instruction() {
		}

		public Instruction(Instruction other) {
			this.operation = other.operation;
			this.operands.addAll(operands);
		}

		@Override
		public int hashCode() {
			  int hash = 1;
		      if (operation != ByteCode.JumpTo) hash ^= operation.hashCode();
		      hash ^= operands.hashCode();
		      return hash;
		}

		public void mergeFrom(Instruction other) {
			if (other == null) {
				return;
			}
			if (other.operation != ByteCode.JumpTo) {
				operation = other.operation;
			}
			operands.addAll(other.operands);
		}

		public String toString(Program p, Library l) {
			// Labels are easy: just dump out the name
			if (operation == ByteCode.Label) {
				return operands.get(0).getStringValue();
			}

			// Convert the operands to strings
			String opString = operands.stream().map(Object::toString).collect(Collectors.joining(","));

			// Generate a comment, if the instruction warrants it
			String comment = "";

			// Stack manipulation comments
			int pops = 0;
			int pushes = 0;

			switch (operation) {

			// These operations all push a single value to the stack
			case PushBool:
			case PushNull:
			case PushNumber:
			case PushString:
			case PushVariable:
			case ShowOptions:
				pushes = 1;
				break;

			// Functions pop 0 or more values, and pop 0 or 1
			case CallFunc:
				FunctionInfo function = l.getFunction((String) operands.get(0).getStringValue());

				pops = function.getParamCount();

				if (function.returnsValue())
					pushes = 1;

				break;

			// Pop always pops a single value
			case Pop:
				pops = 1;
				break;

			// Switching to a different node will always clear the stack
			case RunNode:
				comment += "Clears stack";
				break;
			default:
				break;
			}

			// If we had any pushes or pops, report them

			if (pops > 0 && pushes > 0)
				comment += String.format("Pops %1$s, Pushes %2$s", pops, pushes);
			else if (pops > 0)
				comment += String.format("Pops %s", pops);
			else if (pushes > 0)
				comment += String.format("Pushes %s", pushes);

			// String lookup comments
			switch (operation) {
			case PushString:
			case RunLine:
			case AddOption:

				// Add the string for this option, if it has one
				if ((String) operands.get(0).getStringValue() != null) {
					String text = p.getString((String) operands.get(0).getStringValue());
					comment += String.format("\"%s\"", text);
				}

				break;
			default:
				break;

			}

			if (comment != "") {
				comment = "; " + comment;
			}

			return String.format("%1$-15s %2$-10s %3$-10s", operation.name(), opString, comment);
		}
	}

}
