package com.kyper.yarn;

import com.badlogic.gdx.utils.Array;
import com.kyper.yarn.Lexer.TokenType;
import com.kyper.yarn.Parser.*;
import com.kyper.yarn.Parser.IfStatement.Clause;
import com.kyper.yarn.Program.ByteCode;
import com.kyper.yarn.Program.Instruction;

public class Compiler {

  public    String  programName;
  protected Program program;
  CompileFlags flags = new CompileFlags();
  private int labelCount = 0;

  protected Compiler(String programName){
    program = new Program();
    this.programName = programName;
  }

  public Program getProgram(){
    return program;
  }

  protected void compileNode(Parser.Node node){
    if (program.nodes.containsKey(node.getName())) {
      throw new IllegalArgumentException("Diplicate node name " + node.getName());
    }

    Program.Node compiledNode = new Program.Node();

    compiledNode.name = node.getName();
    compiledNode.tags = node.getNodeTags();

    //register the entire text of this node if we hav eit
    if (node.getSource() != null) {
      //dump the entire contents of this node into the string table
      //instead of compiling its contents
      //the line number is 0 because the string starts at the begining of the node
      compiledNode.sourceStringId = program.registerString(node.getSource(),
                                                           node.getName(),
                                                           "line:" + node.getName(),
                                                           0,
                                                           true);
    } else {

      //compile the node

      String startLabel = registerLabel();
      emit(compiledNode, ByteCode.Label, startLabel);

      for (Statement statement : node.getStatements()) {
        generateCode(compiledNode, statement);
      }

      // Does this node end after emitting AddOptions codes
      // without calling ShowOptions?

      // Note: this only works when we know that we don't have
      // AddOptions and then Jump up back into the code to run them.
      // TODO: A better solution would be for the parser to flag
      // whether a node has Options at the end.

      boolean hasRemainingOptions = false;

      for (Instruction instruction : compiledNode.instructions) {
        if (instruction.getOperation() == ByteCode.AddOption) {
          hasRemainingOptions = true;
        }
        if (instruction.getOperation() == ByteCode.ShowOptions) hasRemainingOptions = false;
      }

      //if this compiled node has no lingering options to show at the end of the node, then we stop at the end
      if (!hasRemainingOptions) {
        emit(compiledNode, ByteCode.Stop);
      } else {

        //otherwise show the accumulated nodes and then jump to the selected node

        emit(compiledNode, ByteCode.ShowOptions);

        if (flags.DisableShuffleOptionsAfterNextSet) {
          emit(compiledNode, ByteCode.PushBool, false);
          emit(compiledNode, ByteCode.StoreVariable, DialogueRunner.SpecialVariables.ShuffleOptions);
          emit(compiledNode, ByteCode.Pop);
          flags.DisableShuffleOptionsAfterNextSet = false;
        }

        emit(compiledNode, ByteCode.RunNode);
      }

    }

    program.nodes.put(compiledNode.name, compiledNode);
  }

  protected void emit(Program.Node node, ByteCode code, Object operandA, Object operandB){
    Instruction instruction = new Instruction();
    instruction.setOperation(code);
    instruction.setOperandA(operandA);
    instruction.setOperandB(operandB);

    node.instructions.add(instruction);

    if (code == ByteCode.Label) {
      //add this label to the label table
      node.labels.put((String) instruction.operandA(), node.instructions.size - 1);
    }
  }

  protected void emit(Program.Node node, ByteCode code, Object operandA){
    emit(node, code, operandA, null);
  }

  protected void emit(Program.Node node, ByteCode code){
    emit(node, code, null);
  }

  protected String getLineIDFromNodeTags(Parser.ParseNode node){
    for (String tag : node.tags) {
      if (tag.startsWith("line:")) return tag;
    }
    return null;
  }

  //statements
  protected void generateCode(Program.Node node, Statement statement){
    switch (statement.getType()) {
      case CustomCommand:
        generateCode(node, statement.getCustomCommand());
        break;
      case ShortcutOptionGroup:
        generateCode(node, statement.getShortcutOptionGroup());
        break;
      case Block:
        //blocks are just groups of statements
        for (Statement s : statement.getBlock().getStatements()) {
          generateCode(node, s);
        }
        break;
      case IfStatement:
        generateCode(node, statement.getIfStatement());
        break;
      case OptionStatement:
        generateCode(node, statement.getOptionStatement());
        break;
      case AssignmentStatement:
        generateCode(node, statement.getAssignmentStatement());
        break;
      case Line:
        generateCode(node, statement, statement.getLine());
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  protected void generateCode(Program.Node node, CustomCommand statement){

    //if this command is an evaluable expression, evaluate it
    if (statement.getExpression() != null) {
      generateCode(node, statement.getExpression());
    } else {
      String customCommand = statement.getClientCommand();
      if (customCommand.equals("stop")) {
        //CASE: stop

        emit(node, ByteCode.Stop);

      } else if (customCommand.equals("shuffleNextOptions")) {
        //CASE : shuffleNextOptions

        //emit code that sets VARSHUFFLE OPPTIONS to true
        emit(node, ByteCode.PushBool, true);
        emit(node, ByteCode.StoreVariable, DialogueRunner.SpecialVariables.ShuffleOptions);
        emit(node, ByteCode.Pop);
        flags.DisableShuffleOptionsAfterNextSet = true;

      } else {
        //DEFAULT
        emit(node, ByteCode.RunCommand, customCommand);
      }
    }

  }

  protected void generateCode(Program.Node node, Statement parseNode, String line){
    //does this line have #line:LINENUM tag? use it
    String lineId = getLineIDFromNodeTags(parseNode);
    String num    = program.registerString(line, node.name, lineId, parseNode.lineNumber, true);

    emit(node, ByteCode.RunLine, num);
  }

  protected void generateCode(Program.Node node, ShortcutOptionGroup statement){
    String endofGroup = registerLabel("groupEnd");

    Array<String> labels = new Array<String>();

    int optionCount = 0;

    for (ShortcutOption option : statement.getOptions()) {
      String optionDestination = registerLabel("option_" + (optionCount + 1));
      labels.add(optionDestination);

      String endofClause = null;

      if (option.getCondition() != null) {
        endofClause = registerLabel("conditional_" + optionCount);
        generateCode(node, option.getCondition());

        emit(node, ByteCode.JumpIfFalse, endofClause);
      }

      String labelLineId = getLineIDFromNodeTags(option);
      String labelStringId = program.registerString(option.getLabel(), node.name, labelLineId, option.lineNumber, true);

      emit(node, ByteCode.AddOption, labelStringId, optionDestination);

      if (option.getCondition() != null) {
        emit(node, ByteCode.Label, endofClause);
        emit(node, ByteCode.Pop);
      }
      optionCount++;
    }

    emit(node, ByteCode.ShowOptions);

    if (flags.DisableShuffleOptionsAfterNextSet == true) {
      emit(node, ByteCode.PushBool, false);
      emit(node, ByteCode.StoreVariable, DialogueRunner.SpecialVariables.ShuffleOptions);
      emit(node, ByteCode.Pop);
      flags.DisableShuffleOptionsAfterNextSet = false;
    }

    emit(node, ByteCode.Jump);

    optionCount = 0;
    for (ShortcutOption option : statement.getOptions()) {
      emit(node, ByteCode.Label, labels.get(optionCount));

      if (option.getOptionNode() != null) generateCode(node, option.getOptionNode().getStatements());

      emit(node, ByteCode.JumpTo, endofGroup);

      optionCount++;

    }

    //reached the end of option group
    emit(node, ByteCode.Label, endofGroup);

    //clean up after jump
    emit(node, ByteCode.Pop);

  }

  protected void generateCode(Program.Node node, Array<Statement> statements){
    if (statements == null) return;
    for (Statement statement : statements) {
      generateCode(node, statement);
    }
  }

  protected void generateCode(Program.Node node, IfStatement statement){
    //we will jump to this label at the end of every clause
    String endofIf = registerLabel("endif");

    for (Clause clause : statement.clauses) {
      String endofClause = registerLabel("skipclause");

      if (clause.getExpression() != null) {
        generateCode(node, clause.getExpression());
        emit(node, ByteCode.JumpIfFalse, endofClause);
      }

      generateCode(node, clause.getStatements());
      emit(node, ByteCode.JumpTo, endofIf);

      if (clause.getExpression() != null) {
        emit(node, ByteCode.Label, endofClause);
      }

      //clean the stack by popping the expression that was tested earlier
      if (clause.getExpression() != null) {
        emit(node, ByteCode.Pop);
      }

    }

    emit(node, ByteCode.Label, endofIf);

  }

  protected void generateCode(Program.Node node, OptionStatement statement){
    String destination = statement.getDestination();

    if (statement.getLabel() == null || statement.getLabel().isEmpty()) {
      //this is a jump to another node
      emit(node, ByteCode.RunNode, destination);
    } else {
      String lineId = getLineIDFromNodeTags(statement.parent);
      String stringId = program.registerString(statement.getLabel(), node.name, lineId, statement.lineNumber, true);

      emit(node, ByteCode.AddOption, stringId, destination);
    }

  }

  protected void generateCode(Program.Node node, AssignmentStatement statement){
    //is it a straight assignment
    if (statement.getOperation() == TokenType.EqualToOrAssign) {
      //evaluate the expression which will result in a value
      //on the stack
      generateCode(node, statement.getValueExpression());
      //stack now contains destionation value
    } else {

      //its combined operation plus assignment

      //get the current value of the variable
      emit(node, ByteCode.PushVariable, statement.getDestinationVariable());

      //evaluate the expression , which will result in a value
      //on the stack
      generateCode(node, statement.getValueExpression());

      //stack now contains currentValue,expresionValue

      switch (statement.getOperation()) {
        case AddAssign:
          emit(node, ByteCode.CallFunc, TokenType.Add.name());
          break;
        case MinusAssign:
          emit(node, ByteCode.CallFunc, TokenType.Minus.name());
          break;
        case MultiplyAssign:
          emit(node, ByteCode.CallFunc, TokenType.Multiply.name());
          break;
        case DivideAssign:
          emit(node, ByteCode.CallFunc, TokenType.Divide.name());
          break;
        default:
          throw new IllegalArgumentException();
      }

      //stack contains destinationValue
    }

    //store the top of the stack in a variable
    emit(node, ByteCode.StoreVariable, statement.getDestinationVariable());

    //clean up the stack
    emit(node, ByteCode.Pop);

  }

  protected void generateCode(Program.Node node, Expression expression){

    //expressions are either plain values or function calls
    switch (expression.type) {
      case Value:
        //plain vallue emmit that
        generateCode(node, expression.value);
        break;
      case FunctionCall:
        //evaluate all parameter expressions
        //which will push them to the stack
        for (Expression param : expression.params) {
          generateCode(node, param);
        }

        //if this function has a variable number of params,
        //put the number of params that we passed t the stack
        if (expression.function.getParamCount() == -1) {
          emit(node, ByteCode.PushNumber, expression.params.size);
        }

        //and then call the function
        emit(node, ByteCode.CallFunc, expression.function.getName());
        break;
      default:
        break;
    }

  }

  protected void generateCode(Program.Node node, ValueNode value){
    //push value to on to the stack
    switch (value.getValue().getType()) {
      case NUMBER:
        emit(node, ByteCode.PushNumber, value.getValue().getNumberValue());
        break;
      case STRING:
        String id = program.registerString(value.getValue().getStringValue(), node.name, null, value.lineNumber, false);
        emit(node, ByteCode.PushString, id);
        break;
      case BOOL:
        emit(node, ByteCode.PushBool, value.getValue().getBoolValue());
        break;
      case VARNAME:
        emit(node, ByteCode.PushVariable, value.getValue().getVarName());
        break;
      case NULL:
        emit(node, ByteCode.PushNull);
      default:
        throw new IllegalArgumentException();
    }
  }

  protected String registerLabel(String commentary){
    return "L" + (labelCount++) + commentary;
  }

  protected String registerLabel(){
    return registerLabel("");
  }

  public static class CompileFlags {
    //should we emmit code that turns VAR_SHUFFLE_OPTIONS off
    //after the next RunOptions bytecode
    public boolean DisableShuffleOptionsAfterNextSet;
  }

}
