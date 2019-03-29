package com.kyper.yarn;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.kyper.yarn.Analyser.Diagnosis.Severity;
import com.kyper.yarn.Program.Instruction;

public class Analyser {

  public static class Diagnosis {

    public String message;
    public String nodeName;
    public int    lineNumber;
    public int    colNumber;
    public Severity severity;

    public Diagnosis(String message, Severity severity, String nodeName, int lineNumber, int colNumber){
      this.message = message;
      this.severity = severity;
      this.nodeName = nodeName;
      this.lineNumber = lineNumber;
      this.colNumber = colNumber;
    }

    public Diagnosis(String message, Severity severity, String nodeName, int lineNumber){
      this(message, severity, nodeName, lineNumber, -1);
    }

    public Diagnosis(String message, Severity severity, String nodeName){
      this(message, severity, nodeName, -1);
    }

    public Diagnosis(String message, Severity severity){
      this(message, severity, null);
    }

    public String toString(boolean showSeverity){
      String contextLabel = "";
      if (showSeverity) {
        switch (severity) {
          case Error:
            contextLabel = "ERROR: ";
            break;
          case Warning:
            contextLabel = "WARNING: ";
            break;
          case Note:
            contextLabel = "Note: ";
            break;
          default:
            throw new IllegalArgumentException();
        }
      }

      if (nodeName != null) {
        contextLabel += nodeName;

        if (lineNumber != -1) {
          contextLabel += StringUtils.format(": %s", lineNumber);

          if (colNumber != -1) {
            contextLabel += StringUtils.format(":%s", colNumber);
          }
        }
      }

      String message;

      if (contextLabel == null || contextLabel.isEmpty()) {
        message = this.message;
      } else {
        message = StringUtils.format(" %1$s: %2$s", contextLabel, this.message);
      }

      return message;

    }

    public enum Severity {
      Error,
      Warning,
      Note
    }
  }

  public static class Context {

    private Array<CompiledProgramAnalyser> analysers;

    public Context(){
      analysers = new Array<Analyser.CompiledProgramAnalyser>();
      analysers.add(new VariableLister());
      analysers.add(new UnusedVariableChecker());
    }

    public Context(CompiledProgramAnalyser... analysers){
      this.analysers = new Array<Analyser.CompiledProgramAnalyser>(analysers);
    }


    protected void addProgramToAnalysis(Program program){
      for (CompiledProgramAnalyser a : analysers) {
        a.diagnose(program);
      }
    }

    public Iterable<Diagnosis> finalAnalysis(){
      Array<Diagnosis> diagnoses = new Array<Analyser.Diagnosis>();
      for (CompiledProgramAnalyser a : analysers) {
        diagnoses.addAll(a.gatherDiagnoses());
      }
      return diagnoses;
    }


  }

  protected static abstract class ASTAnalyser {
    public abstract Iterable<Diagnosis> diagnose(Parser.Node node);
  }

  protected static abstract class CompiledProgramAnalyser {
    public abstract void diagnose(Program program);

    public abstract Array<Diagnosis> gatherDiagnoses();
  }

  protected static class VariableLister extends CompiledProgramAnalyser {

    protected ObjectSet<String> variables = new ObjectSet<String>();

    @Override
    public void diagnose(Program program){
      //each node, find all reads and writes to variables
      for (Entry<String, Program.Node> nodeinfo : program.nodes) {

        Program.Node theNode = nodeinfo.value;

        for (Program.Instruction instruction : theNode.instructions) {
          switch (instruction.getOperation()) {
            case PushVariable:
            case StoreVariable:
              variables.add((String) instruction.operandA());
            default:
              break;
          }
        }
      }
    }

    @Override
    public Array<Diagnosis> gatherDiagnoses(){
      Array<Diagnosis> diagnoses = new Array<Analyser.Diagnosis>();

      for (String var : variables) {
        Diagnosis d = new Diagnosis("Script uses variable " + var, Severity.Note);
        diagnoses.add(d);
      }
      return diagnoses;
    }

  }

  protected static class UnusedVariableChecker extends CompiledProgramAnalyser {
    private ObjectSet<String> readVars    = new ObjectSet<String>();
    private ObjectSet<String> writtenVars = new ObjectSet<String>();

    @Override
    public void diagnose(Program program){
      //in each node, find all reads and writes to variables
      for (Entry<String, Program.Node> nodeinfo : program.nodes) {


        Program.Node       node         = nodeinfo.value;
        Array<Instruction> instructions = node.instructions;
        for (int i = 0; i < instructions.size; i++) {
          Instruction instruction = instructions.get(i);

          switch (instruction.getOperation()) {
            case PushVariable:
              readVars.add((String) instruction.operandA());
              break;
            case StoreVariable:
              writtenVars.add((String) instruction.operandA());
              break;
            default:
              break;
          }
        }
      }
    }

    @Override
    public Array<Diagnosis> gatherDiagnoses(){

      //exclude read variables that are also written
      Array<String> readOnly = new Array<String>();
      readOnly.addAll(readVars.iterator().toArray());
      readOnly.removeAll(writtenVars.iterator().toArray(), false);

      //exclude write vars that are read
      Array<String> writeOnly = new Array<String>();
      writeOnly.addAll(writtenVars.iterator().toArray());
      writeOnly.removeAll(readVars.iterator().toArray(), false);

      //generate diagnoses
      Array<Diagnosis> diagnoses = new Array<Analyser.Diagnosis>();

      for (String ro : readOnly) {
        String message = StringUtils.format("Variable %s is read from, but nevver assigned", ro);
        diagnoses.add(new Diagnosis(message, Severity.Warning));
      }

      for (String wo : writeOnly) {
        String message = StringUtils.format("Variable %s is assigned, but never read from", wo);
        diagnoses.add(new Diagnosis(message, Severity.Warning));
      }

      return diagnoses;
    }

  }

}
