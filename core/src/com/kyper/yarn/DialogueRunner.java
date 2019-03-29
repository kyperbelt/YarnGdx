package com.kyper.yarn;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.kyper.yarn.Dialogue.*;
import com.kyper.yarn.Library.FunctionInfo;
import com.kyper.yarn.YarnProgram.Instruction;
import com.kyper.yarn.YarnProgram.Node;

public class DialogueRunner {

  protected static final String              EXEC_COMPLETE = "executionCompleteCommand";
  private                LineHandler         lineHandler;
  private                OptionsHandler      optionsHandler;
  private                CommandHandler      commandHandler;
  private                NodeCompleteHandler nodeCompleteHandler;
  private                Dialogue            dialogue;
  private                YarnProgram         yarnProgram;
  private                State               state         = new State();
  private                ExecutionState      executionState;
  private                Node                currentNode;

  protected DialogueRunner(Dialogue d, YarnProgram p){
    this.dialogue = d;
    this.yarnProgram = p;
    executionState = ExecutionState.Running;
  }

  /**
   * set the current node the yarnProgram is on log an error and stop execution if the node does not exist
   */
  public boolean setNode(String name){
    if (!yarnProgram.nodes.containsKey(name)) {
      String error = "no node named " + name;
      dialogue.errorLogger.log(error);
      setExecutionState(ExecutionState.Stopped);
      return false;
    }

    dialogue.debugLogger.log("Running node " + name);

    // clear the special variables
    dialogue.storage.setValue(SpecialVariables.ShuffleOptions, new Value(false));

    currentNode = yarnProgram.nodes.get(name);
    resetState();
    state.currentNodeName = name;
    return true;
  }

  public String currentNodeName(){
    return state.currentNodeName;
  }

  public void stop(){
    setExecutionState(ExecutionState.Stopped);
  }

  public boolean hasOptions(){
    return state.currentOptions.size > 0;
  }

  /**
   * Executes the next instruction in the current node
   */
  protected void runNext(){

    if (executionState == ExecutionState.WaitingOnOptionSelection) {
      dialogue.errorLogger.log("Cannot continue running dialogue. Still waiting on option selection.");
      //executionState = ExecutionState.Stopped;
      setExecutionState(ExecutionState.Stopped);

      return;
    }

    if (executionState == ExecutionState.Stopped) setExecutionState(ExecutionState.Running);


    Instruction currentInstruction = currentNode.instructions.get(state.programCounter);

    runInstruction(currentInstruction);

    //DEBUG instruction sets ---
    //System.out.println(currentInstruction.toString(yarnProgram, dialogue.library));

    state.programCounter++;

    if (state.programCounter >= currentNode.instructions.size) {
      nodeCompleteHandler.handle(new NodeCompleteResult(null));
      //executionState = ExecutionState.Stopped;
      setExecutionState(ExecutionState.Stopped);
      dialogue.debugLogger.log("Run complete");
      return;
    }


  }

  /**
   * looks up the instruction number for a named label in the current node.
   */
  protected int findInstructionForLabel(String label){
    if (!currentNode.labels.containsKey(label))
      throw new IndexOutOfBoundsException("Unknown label " + label + " in node " + state.currentNodeName);
    return currentNode.labels.get(label);
  }

  protected void runInstruction(Instruction instruction){
    switch (instruction.getOperation()) {
      case Label:
        // label no-op, used as a destination for jumpto and jump
        break;
      case JumpTo:
        // jumps to named label
        state.programCounter = findInstructionForLabel((String) instruction.operandA());
        break;
      case RunLine:
        // looks up a string from the string table
        // and passes it to the client as a line
        String lineText = yarnProgram.getString((String) instruction.operandA());
        if (lineText == null) {
          dialogue.errorLogger.log("no loaded string table includes line " + instruction.operandA());
          break;
        }
        lineHandler.handle(new LineResult(lineText));
        break;
      case RunCommand:
        // passes a string to the client as a custom command
        commandHandler.handle(new CommandResult((String) instruction.operandA()));
        break;
      case PushString:
        // pushes a string value onto the stack. the operand is an index into
        // the string table, so thats looked up first
        state.pushValue(yarnProgram.getString((String) instruction.operandA()));
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
          state.programCounter = findInstructionForLabel((String) instruction.operandA());
        }
        break;
      case Jump:
        // jumps to a label whose name is on the stack
        String jumpDest = state.peekValue().asString();
        state.programCounter = findInstructionForLabel(jumpDest);

        break;
      case Pop:
        // pop a value from the stack
        state.popValue();
        break;
      case CallFunc:
        // call a function, whose parameters are expected to
        // be on the stack. pushes the functions return value,
        // if it returns one
        String functionName = (String) instruction.operandA();

        FunctionInfo function = dialogue.library.getFunction(functionName);

      {
        int paramCount = function.getParamCount();

        // if this function takes -1 params, it is variadic.
        // expect the compiler to have palced the number of params
        // actually passed at the top of the stack.
        if (paramCount == -1) {
          paramCount = (int) state.popValue().asNumber();
        }

        Value result;

        if (paramCount == 0) {
          result = function.invoke();
        } else {
          // get the parameters, which are pushed in reverse
          Value[] params = new Value[paramCount];
          for (int i = paramCount - 1; i >= 0; i--) {
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
        String varName = (String) instruction.operandA();
        Value loaded = dialogue.storage.getValue(varName);
        state.pushValue(loaded);
        break;
      case StoreVariable:
        // store the top value on the stack in a variable
        Value topval = state.peekValue();
        String destinationVarName = String.valueOf(instruction.operandA());
        dialogue.storage.setValue(destinationVarName, topval);
        break;
      case Stop:
        // stop execution immidiately and report it
        nodeCompleteHandler.handle(new NodeCompleteResult(null));
        commandHandler.handle(new CommandResult(EXEC_COMPLETE));

        //executionState = ExecutionState.Stopped;
        setExecutionState(ExecutionState.Stopped);
        break;
      case RunNode:
        // run a node
        String nodeName;

        if (instruction.operandA() == null || ((String) instruction.operandA()).isEmpty()) {
          // get a string from the stack, and jump to a node with that name
          nodeName = state.peekValue().asString();
        } else {
          // jump straight to the node
          nodeName = (String) instruction.operandA();
        }

        nodeCompleteHandler.handle(new NodeCompleteResult(nodeName));
        setNode(nodeName);

        break;
      case AddOption:
        // add an option to the current state
        Entry<String, String> option = new Entry<String, String>();
        option.key = (String) instruction.operandA();
        option.value = (String) instruction.operandB();
        state.currentOptions.add(option);
        break;
      case ShowOptions:
        // if we have no options to show, immidiately stop
        if (state.currentOptions.size == 0) {
          nodeCompleteHandler.handle(new NodeCompleteResult(null));
          //executionState = ExecutionState.Stopped;
          setExecutionState(ExecutionState.Stopped);
          break;
        }

        // if we have a single option, and it has no label, select it and continue
        // execution
        if (state.currentOptions.size == 1 && state.currentOptions.get(0).key == null) {
          String dest = state.currentOptions.first().value;
          state.pushValue(dest);
          state.currentOptions.clear();
          break;
        }

        if (dialogue.storage.getValue(SpecialVariables.ShuffleOptions).asBool()) {
          // shuffle the dialogue options if needed
          int n = state.currentOptions.size;
          for (int opt1 = 0; opt1 < n; opt1++) {
            int opt2 = opt1 + (int) (MathUtils.random() * (n - opt1));
            state.currentOptions.swap(opt1, opt2);
          }
        }

        // present options to user to choose
        Array<String> optionStrings = new Array<String>();
        for (Entry<String, String> op : state.currentOptions) {
          optionStrings.add(yarnProgram.getString(op.key));
        }

        // cant continue until client chooses option
        setExecutionState(ExecutionState.WaitingOnOptionSelection);

        optionsHandler.handle(new OptionResult(optionStrings, new OptionChooser() {

          @Override
          public void choose(int selectedOptionIndex){
            // we now know what number option was selected; push the corresponding node name
            // to the stack
            String destNode = state.currentOptions.get(selectedOptionIndex).value;
            state.pushValue(destNode);

            // we no longer need the accum list of optionsl clear it so that ist
            // ready for the next one
            state.currentOptions.clear();

            // we can now keep running
            setExecutionState(ExecutionState.Running);

          }
        }));

        break;

      default:
        // no acepted bytecode, stop the yarnProgram
        // and throw exeption
        //executionState = ExecutionState.Stopped;
        setExecutionState(ExecutionState.Stopped);
        throw new IllegalArgumentException(instruction.getOperation().name());

    }
  }

  public LineHandler getLineHandler(){
    return lineHandler;
  }

  public void setLineHandler(LineHandler lineHandler){
    this.lineHandler = lineHandler;
  }

  public OptionsHandler getOptionsHandler(){
    return optionsHandler;
  }

  public void setOptionsHandler(OptionsHandler optionsHandler){
    this.optionsHandler = optionsHandler;
  }

  public CommandHandler getCommandHandler(){
    return commandHandler;
  }

  public void setCommandHandler(CommandHandler commandHandler){
    this.commandHandler = commandHandler;
  }

  public NodeCompleteHandler getCompleteHandler(){
    return nodeCompleteHandler;
  }

  public void setCompleteHandler(NodeCompleteHandler nodeCompleteHandler){
    this.nodeCompleteHandler = nodeCompleteHandler;
  }

  public ExecutionState getExecutionState(){
    return executionState;
  }

  private void setExecutionState(ExecutionState executionState){
    this.executionState = executionState;
    if (this.executionState == ExecutionState.Stopped) resetState();
  }

  public void resetState(){
    state = new State();
  }

  public enum ExecutionState {
    /** Stopped */
    Stopped,
    /** Waiting on option selection */
    WaitingOnOptionSelection,
    /** Running */
    Running
  }

  public interface LineHandler {
    void handle(LineResult line);
  }

  // HANDLERS

  public interface OptionsHandler {
    void handle(OptionResult options);
  }

  public interface CommandHandler {
    void handle(CommandResult command);
  }

  public interface NodeCompleteHandler {
    void handle(NodeCompleteResult compelte);
  }

  protected static class SpecialVariables {
    public static final String ShuffleOptions = "$Yarn.ShuffleOptions";
  }

  protected class State {
    // the name of the node that we are currently on
    public String currentNodeName;

    // the instruction number in the current node
    public int programCounter = 0;

    // list of options, where each option = <string id,destination node>
    public Array<Entry<String, String>> currentOptions = new Array<Entry<String, String>>();

    // the value stack
    private Array<Value> stack = new Array<Value>();

    /**
     * push a value on to the value stack
     */
    public void pushValue(Object o){
      if (o instanceof Value) stack.add((Value) o);
      else stack.add(new Value(o));
    }

    /**
     * pop a value from the value stack
     *
     * @return
     */
    public Value popValue(){
      return stack.pop();
    }

    /**
     * peek at a value from the stack
     *
     * @return
     */
    public Value peekValue(){
      return stack.peek();
    }

    /**
     * clear the value stack
     */
    public void clearValueStack(){
      stack.clear();
    }
  }

}
