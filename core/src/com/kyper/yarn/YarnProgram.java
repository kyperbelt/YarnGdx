package com.kyper.yarn;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.StringBuilder;
import com.kyper.yarn.Lexer.Token;
import com.kyper.yarn.Lexer.TokenType;
import com.kyper.yarn.FunctionLibrary.FunctionInfo;

public class YarnProgram {

  protected ObjectMap<String, String>   strings  = new ObjectMap<String, String>();
  protected ObjectMap<String, LineInfo> lineInfo = new ObjectMap<String, YarnProgram.LineInfo>();

  protected ObjectMap<String, Node> nodes = new ObjectMap<String, Node>();

  private int stringCount = 0;

  /// Loads a new string table into the yarnProgram.

  /**
   * The string table is merged with any existing strings, with the new table taking precedence over the old.
   */
  public void loadStrings(ObjectMap<String, String> newStrings){
    for (Entry<String, String> line : newStrings) {
      strings.put(line.key, line.value);
    }
  }

  public String registerString(String string, String nodeName, String lineId, int lineNumber, boolean localisable){
    String key;

    if (lineId == null) key = String.format("%1$s - %2$s", nodeName, stringCount++);
    else key = lineId;

    //its not int he list; append it
    strings.put(key, string);

    if (localisable) {
      //additionally, keep info about this string around
      lineInfo.put(key, new LineInfo(nodeName, lineNumber));
    }

    return key;
  }

  public String getString(String key){
    String value = null;
    if (strings.containsKey(key)) value = strings.get(key);
    return value;
  }

  public String dumpCode(FunctionLibrary lib){
    StringBuilder sb = new StringBuilder();

    for (Entry<String, Node> entry : nodes) {
      sb.appendLine("Node " + entry.key + ":");
      int instructionCount = 0;

      Array<Instruction> instructions = entry.value.instructions;
      for (int i = 0; i < instructions.size; i++) {
        Instruction instruction     = instructions.get(i);
        String      instructionText = null;

        if (instruction.getOperation() == ByteCode.Label) {
          instructionText = instruction.toString(this, lib);
        } else {
          instructionText = "    " + instruction.toString(this, lib);
        }

        String preface;
        if (instructionCount % 5 == 0 || instructionCount == entry.value.instructions.size - 1) {
          preface = String.format("%1$6s", instructionCount + "");
        } else {
          preface = String.format("%1$6s    ", " ");
        }

        sb.appendLine(preface + instructionText);
        instructionCount++;
      }
      sb.appendLine("");
    }

    for (Entry<String, String> entry : strings) {
      LineInfo lineInfo = this.lineInfo.get(entry.key);
      if (lineInfo == null) continue;
      sb.appendLine(String.format("%1$s: %2$s  (%3$s:%4$s)",
                                  entry.key,
                                  entry.value,
                                  lineInfo.getNodeName(),
                                  lineInfo.getLineNumber()));
    }

    return sb.toString();
  }

  public String getTextForNode(String nodeName){
    String key = nodes.get(nodeName).sourceStringId;
    return this.getString(key == null ? "" : key);
  }

  public void include(YarnProgram otherYarnProgram){
    for (Entry<String, Node> other : otherYarnProgram.nodes) {
      if (nodes.containsKey(other.key)) {
        throw new IllegalStateException(String.format("This yarnProgram already contains a node named %s", other.key));
      }

      nodes.put(other.key, other.value);
    }

    for (Entry<String, String> other : otherYarnProgram.strings) {
      //TODO: this seems fishy -- maybe check strings map instead?
      if (nodes.containsKey(other.key)) {
        throw new IllegalStateException(String.format("This yarnProgram already contains a string with key %s",
                                                      other.key));
      }
      strings.put(other.key, other.value);
    }
  }

  // When saving programs, we want to save only lines that do NOT have a line: key.
  // This is because these lines will be loaded from a string table.
  // However, because certain strings (like those used in expressions) won't have tags,
  // they won't be included in generated string tables, so we need to export them here.

  // We do this by NOT including the main strings list, and providing a property
  // that gets serialised as "strings" in the output, which includes all untagged strings.
  protected ObjectMap<String, String> untaggedStrings(){
    ObjectMap<String, String> result = new ObjectMap<String, String>();

    for (Entry<String, String> line : strings) {
      if (line.key.startsWith("line:"))//TODO ============maybe change to Line since thats what the parser spits out
        continue;
      result.put(line.key, line.value);
    }
    return result;
  }

  protected enum ByteCode {
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
    /// present the current list of options, then clear the list; most recently selected option will be on the top of
    // the stack
    ShowOptions,
    /// opA = int: string number in table; push string to stack
    PushString,
    /// opA = float: number to push to stack
    PushNumber,
    /// opA = int (0 or 1): bool to push to stack
    PushBool,
    /// pushes a null value onto the stack
    PushNull,
    /// opA = string: label name if top of stack is not null, zero or false, jumps to that label
    JumpIfFalse,
    /// discard top of stack
    Pop,
    /// opA = string; looks up function, pops as many arguments as needed, result is pushed to stack
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

  protected static class ParseException extends RuntimeException {
    private static final long serialVersionUID = -6422941521497633431L;

    protected int lineNumber = 0;

    public ParseException(String message){
      super(message);
    }

    protected static ParseException make(Token foundToken, TokenType... expectedTypes){
      int lineNumber = foundToken.lineNumber + 1;

      Array<String> expectedTypeNames = new Array<String>();
      for (TokenType type : expectedTypes) {
        expectedTypeNames.add(type.name());
      }
      String possibleValues = String.join(",", expectedTypeNames);
      String message = String.format("Line %1$s:%2$s: Expected %3$s, but found %4$s",
                                     lineNumber,
                                     foundToken.columnNumber,
                                     possibleValues,
                                     foundToken.type.name());
      ParseException e = new ParseException(message);
      e.lineNumber = lineNumber;
      return e;
    }

    protected static ParseException make(Token mostRecentToken, String message){
      int            lineNumber = mostRecentToken.lineNumber + 1;
      String         m          = String.format("Line %1$s:%2$s: %3$s",
                                                lineNumber,
                                                mostRecentToken.columnNumber,
                                                message);
      ParseException e          = new ParseException(m);
      e.lineNumber = lineNumber;
      return e;
    }
  }

  protected static class LineInfo {
    private int    lineNumber;
    private String nodeName;

    public LineInfo(String nodeName, int lineNumber){
      this.nodeName = nodeName;
      this.lineNumber = lineNumber;
    }

    public int getLineNumber(){
      return lineNumber;
    }

    public String getNodeName(){
      return nodeName == null ? "null" : nodeName;
    }
  }

  protected static class Node {

    public Array<Instruction> instructions = new Array<YarnProgram.Instruction>();
    public String             name;

    //the entry in the programs string table that contains
    //the original text of this node. null if not available
    public String sourceStringId = null;

    public ObjectMap<String, Integer> labels = new ObjectMap<String, Integer>();

    public Array<String> tags;

  }

  protected static class Instruction {
    private ByteCode operation;
    private Object   operandA;
    private Object   operantB;

    public Instruction(){
    }

    public Instruction(ByteCode operation, Object operandA, Object operandB){
      this.operandA = operandA;
      this.operantB = operandB;
      this.operation = operation;
    }

    public ByteCode getOperation(){
      return operation;
    }

    public void setOperation(ByteCode operation){
      this.operation = operation;
    }

    public Object operandA(){
      return operandA;
    }

    public Object operandB(){
      return operantB;
    }

    public void setOperandA(Object operandA){
      this.operandA = operandA;
    }

    public void setOperandB(Object operandB){
      this.operantB = operandB;
    }

    public String toString(YarnProgram p, FunctionLibrary l){
      // Labels are easy: just dump out the name
      if (operation == ByteCode.Label) {
        return operandA + ":";
      }

      // Convert the operands to strings
      String opAString = operandA != null ? operandA.toString() : "";
      String opBString = operantB != null ? operantB.toString() : "";

      // Generate a comment, if the instruction warrants it
      String comment = "";

      // Stack manipulation comments
      int pops   = 0;
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
          FunctionInfo function = l.getFunction((String) operandA);

          pops = function.getParamCount();

          if (function.returnsValue()) pushes = 1;

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

      if (pops > 0 && pushes > 0) comment += String.format("Pops %1$s, Pushes %2$s", pops, pushes);
      else if (pops > 0) comment += String.format("Pops %s", pops);
      else if (pushes > 0) comment += String.format("Pushes %s", pushes);

      // String lookup comments
      switch (operation) {
        case PushString:
        case RunLine:
        case AddOption:

          // Add the string for this option, if it has one
          if (operandA != null) {
            String text = p.getString((String) operandA);
            comment += String.format("\"%s\"", text);
          }

          break;
        default:
          break;

      }

      if (comment != "") {
        comment = "; " + comment;
      }

      return String.format("%1$-15s %2$-10s %3$-10s %4$-10s", operation.name(), opAString, opBString, comment);
    }
  }

}
