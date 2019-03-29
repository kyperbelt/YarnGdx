package com.kyper.yarn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.kyper.yarn.Analyser.Context;
import com.kyper.yarn.DialogueRunner.*;
import com.kyper.yarn.Lexer.TokenType;
import com.kyper.yarn.Library.ReturningFunc;
import com.kyper.yarn.Loader.NodeFormat;
import com.kyper.yarn.YarnProgram.LineInfo;

public class Dialogue {

  // node we start from
  public static final String                     DEFAULT_START    = "Start";
  public              YarnLogger                 debugLogger;
  public              YarnLogger                 errorLogger;
  // collection of nodes that we've seen
  public              ObjectMap<String, Integer> visitedNodeCount = new ObjectMap<String, Integer>();
  protected           VariableStorage            storage;
  // loader contains all the nodes we're going to run
  protected           Loader                     loader;
  // the yarnProgram is the compiled yarn yarnProgram
  protected           YarnProgram                yarnProgram;
  // the library contains all the functions and operators we know about
  protected           Library                    library;
  protected           boolean                    executionCompleted;
  protected ReturningFunc  yarnFunctionIsNodeVisited  = new ReturningFunc() {
    @Override
    public Object invoke(Value... params){
      boolean visited = (Integer) yarnFunctionNodeVisitCount.invoke(params) > 0;
      return visited;
    }
  };
  ObjectMap<String, String> textForNodes;
  private   DialogueRunner runner;
  /**
   * A function exposed to yarn that returns the number of times a node has been run. if no parameters are supplied,
   * returns the number of times the current node has been run.
   */
  protected ReturningFunc  yarnFunctionNodeVisitCount = new ReturningFunc() {
    @Override
    public Object invoke(Value... params){

      // determine the node were checking
      String nodeName;

      if (params.length == 0) {
        // no marams? check the current node
        nodeName = runner.currentNodeName();
      } else if (params.length == 1) {
        // a parameter? check the named node
        nodeName = params[0].asString();
        // ensure node existance
        if (!nodeExists(nodeName)) {
          String error = String.format(" the node %s does not exist.", nodeName);
          errorLogger.log(error);
          return 0;
        }
      } else {
        // we go ttoo many parameters
        String error = String.format("incorrect number of parameters visitcount expect 0 or 1, got %s", params.length);
        errorLogger.log(error);
        return 0;
      }
      int visitCount = 0;
      if (visitedNodeCount.containsKey(nodeName)) visitCount = visitedNodeCount.get(nodeName);
      return visitCount;
    }
  };
  private   RunnerResult   runnerResult;
  /** Whether to log token occurrences. */
  private   boolean        logTokens;
  /** Whether to log the parse tree. */
  private   boolean        logTree;

  /**
   * creates a yarn dialogue
   *
   * @param storage - will be used to store/get values
   * @param debug   - debug logger implementation
   * @param error   - error logger implementation
   */
  public Dialogue(VariableStorage storage, YarnLogger debug, YarnLogger error){
    this.storage = storage;
    loader = new Loader(this);
    library = new Library();

    this.debugLogger = debug;
    this.errorLogger = error;
    this.executionCompleted = false;

    library.importLibrary(new StandardLibrary());

    // register the "visited" function which returns true if we've visited
    // a node previously (nodes are marked as visited when we leave them)
    library.registerFunction("visited", -1, yarnFunctionIsNodeVisited);

    // register the visitCount function which returns athe number of times
    // a node has been run(increments on node end)
    // no parameters = check the current node
    library.registerFunction("visitCount", -1, yarnFunctionNodeVisitCount);

  }

  /**
   * creates a dialogue with a default debug and error implementation
   *
   * @param storage - will be used to store/get values
   */
  public Dialogue(VariableStorage storage){
    this(storage, new YarnLogger() {

      @Override
      public void log(String message){
        Gdx.app.log("YarnGdx:", message);
      }
    }, new YarnLogger() {
      @Override
      public void log(String message){
        Gdx.app.error("YarnGdx:", message);
      }
    });
  }

  public Library getLibrary(){
    return library;
  }

  public boolean isRunning(){
    return runner != null && runner.getExecutionState() != ExecutionState.Stopped;
  }

  // /**
  // * Start a thread that spits out results waits for results to be consumed
  // */
  // private Array<RunnerResult> results = new Array<Dialogue.RunnerResult>();

  public ExecutionState getExecutionState(){
    return runner.getExecutionState();
  }

  /**
   * @param enabled Whether to perform logging of token occurences.
   */
  public void setTokenLogging(boolean enabled){
    this.logTokens = enabled;
  }

  /**
   * @param enabled Whether to perform logging of the parse tree.
   */
  public void setTreeLogging(boolean enabled){
    this.logTree = enabled;
  }

  /**
   * load all nodes contained in the text to the dialogue unless otherwise specified
   *
   * @param text              - the text containing node info
   * @param fileName          - the name of the 'file' used for debug purposes
   * @param exclusiveNodeName - if not null, only the specified node will be considered for loading;all else will be
   *                          ignored.
   */
  public void loadString(String text, String fileName, String exclusiveNodeName){
    if (debugLogger == null) {
      throw new YarnRuntimeException("DebugLogger must be set before loading");
    }

    if (errorLogger == null) throw new YarnRuntimeException("ErrorLogger must be set before loading");

    // try to infer type
    NodeFormat format;
    if (text.startsWith("[")) {
      format = NodeFormat.Json;
    } else if (text.contains("---")) {
      format = NodeFormat.Text;
    } else {
      format = NodeFormat.SingleNodeText;
    }

    yarnProgram = loader.load(text, library, fileName, yarnProgram, logTokens, logTree, exclusiveNodeName, format);
  }

  /**
   * load all nodes contained in the text to the dialogue unless otherwise specified
   *
   * @param text - the text containing node info
   * @param name - the name of the 'file' used for debug purposes
   */
  public void loadString(String text, String name){
    loadString(text, name, null);
  }

  public boolean start(String start){
    runnerResult = null;
    executionCompleted = false;

    if (debugLogger == null) {
      throw new YarnRuntimeException("debugLogger must be set before running");
    }

    if (errorLogger == null) {
      throw new YarnRuntimeException("errorLogger must be set before running");
    }

    if (yarnProgram == null) {
      errorLogger.log("Dialogue.run was called but no yarnProgram was loaded.");
      return false;
    }

    runner = new DialogueRunner(this, yarnProgram);

    runner.setLineHandler(new LineHandler() {
      @Override
      public void handle(LineResult line){
        runnerResult = line;
      }
    });

    runner.setCommandHandler(new CommandHandler() {
      @Override
      public void handle(CommandResult command){
        // if stop
        if (command.command.getCommand().equals("stop")) {
          runner.stop();
        } else if (command.getCommand().equals(DialogueRunner.EXEC_COMPLETE)) {
          executionCompleted = true;
        } else {
          runnerResult = command;
        }
      }
    });

    runner.setCompleteHandler(new NodeCompleteHandler() {
      @Override
      public void handle(NodeCompleteResult complete){
        if (runner.currentNodeName() != null) {
          int count = 0;
          if (visitedNodeCount.containsKey(runner.currentNodeName()))
            count = visitedNodeCount.get(runner.currentNodeName());

          visitedNodeCount.put(runner.currentNodeName(), count + 1);
        }
        runnerResult = complete;
      }
    });

    runner.setOptionsHandler(new OptionsHandler() {
      @Override
      public void handle(OptionResult options){
        runnerResult = options;
      }
    });

    return runner.setNode(start);
  }

  public boolean start(){
    return start(DEFAULT_START);
  }

  /**
   * update the virtual machine counter.
   */
  protected boolean update(){
    if (runner != null && !executionCompleted && runner.getExecutionState() != ExecutionState.WaitingOnOptionSelection) {
      runner.runNext();
      return true;
    }
    return false;
  }

  /**
   * get the next result - if it is null it will attempt to populate it
   *
   * @return
   */
  public RunnerResult getNext(){
    // TODO: remove?
    if (runner == null) // we are not running so return null
      return null;
    checkNext();// make sure there is a next result
    RunnerResult r = runnerResult;
    runnerResult = null;
    return r;
  }

  /**
   * get the next result - will return null if there is no result
   *
   * @return
   */
  public RunnerResult nextRaw(){
    return runnerResult;
  }

  /**
   * checks the next result - if it is null it will attempt to populate it
   *
   * @return
   */
  public RunnerResult checkNext(){
    if (runnerResult == null) populateNext();
    return runnerResult;
  }

  /**
   * check if the next result is an options result
   *
   * @return
   */
  public boolean isNextOptions(){
    return checkNext() instanceof OptionResult;
  }

  /**
   * check if the next result is a line result
   *
   * @return
   */
  public boolean isNextLine(){
    return checkNext() instanceof LineResult;
  }

  /**
   * check if the next result is a custom command result
   *
   * @return
   */
  public boolean isNextCommand(){
    return checkNext() instanceof CommandResult;
  }

  /**
   * check if the next result is a node complete result
   *
   * @return
   */
  public boolean isNextComplete(){
    return checkNext() instanceof NodeCompleteResult;
  }

  /**
   * get the next result as a line if the next result is not a line it will return null
   *
   * @return
   */
  public LineResult getNextAsLine(){
    return isNextLine() ? (LineResult) getNext() : null;
  }

  /**
   * get the next result as an options result if the next result is not an options result then this will return null
   *
   * @return
   */
  public OptionResult getNextAsOptions(){
    return isNextOptions() ? (OptionResult) getNext() : null;
  }

  // CHECK FUNCS
  // public boolean hasNext() {
  // return checkNext(0) != null;
  // }
  //
  // public boolean optionsAvailable() {
  // return checkNext(1) != null && checkNext(1) instanceof OptionResult;
  // }
  //
  // public boolean isLine() {
  // return checkNext() instanceof LineResult;
  // }
  //
  // public boolean isCommand() {
  // return checkNext() instanceof CommandResult;
  // }
  //
  // public boolean isOptions() {
  // return checkNext() instanceof OptionResult;
  // }
  //
  // public boolean isNodeComplete() {
  // return checkNext() instanceof NodeCompleteResult;
  // }
  //
  // /**
  // * offset from the end of the results stack
  // *
  // * @param offset
  // * @return
  // */
  // public RunnerResult checkNext(int offset) {
  // return results.size - Math.abs(offset) <= 0 ? null : results.get(results.size
  // - 1 - Math.abs(offset));
  // }
  //
  // public RunnerResult checkNext() {
  // return checkNext(0);
  // }
  //
  // public <t> t checkNext(Class<t> type) {
  // return type.cast(checkNext(0));
  // }
  //
  // // RETURN FUNCS
  //
  // public RunnerResult getNext() {
  // return results.size == 0 ? null : results.pop();
  // }
  //
  // public <t> t getNext(Class<t> type) {
  // return type.cast(getNext());
  // }
  //
  // public OptionResult getOptions() {
  // return getNext(OptionResult.class);
  // }
  //
  // public LineResult getLine() {
  // return getNext(LineResult.class);
  // }
  //
  // public CommandResult getCommand() {
  // return getNext(CommandResult.class);
  // }
  //
  // public NodeCompleteResult getNodeComplete() {
  // return getNext(NodeCompleteResult.class);
  // }

  /**
   * get the next result as a command result (must be parsed by the programmer) if the next result is not a commandreslt
   * then this will return null
   *
   * @return
   */
  public CommandResult getNextAsCommand(){
    return isNextCommand() ? (CommandResult) getNext() : null;
  }

  /**
   * get the next result as a node complete result if the next result is not a node compelte result then this will
   * return null
   *
   * @return
   */
  public NodeCompleteResult getNextAsComplete(){
    return isNextComplete() ? (NodeCompleteResult) getNext() : null;
  }

  // we update the runner until the next result is no longer null
  private void populateNext(){
    while (runnerResult == null) if (!update()) break;

  }

  public void stop(){
    if (runner != null) runner.stop();
  }

  public Array<String> allNodes(){
    return yarnProgram.nodes.keys().toArray();
  }

  public String currentNode(){
    return runner == null ? null : runner.currentNodeName();
  }

  public ObjectMap<String, String> getTextForAllNodes(){
    if (textForNodes == null) textForNodes = new ObjectMap<String, String>();
    textForNodes.clear();
    for (Entry<String, YarnProgram.Node> entry : yarnProgram.nodes) {
      String text = yarnProgram.getTextForNode(entry.key);

      if (text == null) continue;

      textForNodes.put(entry.key, text);
    }

    return textForNodes;
  }

  /**
   * get the source code for the node
   *
   * @param node
   *
   * @return
   */
  public String getTextForNode(String node){
    if (yarnProgram.nodes.size == 0) {
      errorLogger.log("no nodes are loaded!");
      return null;
    } else if (yarnProgram.nodes.containsKey(node)) {
      return yarnProgram.getTextForNode(node);
    } else {
      errorLogger.log("no node named " + node);
      return null;
    }
  }

  public void addStringTable(ObjectMap<String, String> stringTable){
    yarnProgram.loadStrings(stringTable);
  }

  public ObjectMap<String, String> getStringTable(){
    return yarnProgram.strings;
  }

  protected ObjectMap<String, LineInfo> getStringInfoTable(){
    return yarnProgram.lineInfo;
  }

  /**
   * unload all nodes
   *
   * @param clearVisistedNodes
   */
  public void unloadAll(boolean clearVisistedNodes){
    if (clearVisistedNodes) visitedNodeCount.clear();
    yarnProgram = null;
  }

  public String getByteCode(){
    return yarnProgram.dumpCode(library);
  }

  public boolean nodeExists(String nodeName){
    if (yarnProgram == null) {
      errorLogger.log("no nodes compiled");
      return false;
    }
    if (yarnProgram.nodes.size == 0) {
      errorLogger.log("no nodes in yarnProgram");
      return false;
    }

    return yarnProgram.nodes.containsKey(nodeName);

  }

  protected void printState(){
    if (!isRunning()) return;
    System.out.println("Current VM State:" + runner.getExecutionState().name());
  }

  public void analyse(Context context){
    context.addProgramToAnalysis(yarnProgram);
  }

  public Array<String> getvisitedNodes(){
    return visitedNodeCount.keys().toArray();
  }

  public void setVisitedNodes(Array<String> visited){
    visitedNodeCount.clear();
    for (String string : visited) {
      visitedNodeCount.put(string, 1);
    }
  }

  /**
   * unload all nodes clears visited nodes
   */
  public void unloadAll(){
    unloadAll(true);
  }

  // ======================================================================================

  /**
   * option chooser lets client tell dialogue the response selected by the user
   */
  public interface OptionChooser {
    void choose(int selectedOptionIndex);
  }

  /**
   * logger to let the client send output to the console logging/error logging
   */
  public interface YarnLogger {
    void log(String message);
  }

  /**
   * variable storage TODO: try to use {@link DialogueStorage UserData}
   */
  public interface VariableStorage {
    void setValue(String name, Value value);

    Value getValue(String name);

    void clear();
  }

  /**
   * indicates something the client should do
   */
  public static abstract class RunnerResult {
    // private boolean consumed = false;
    // public void consume() {consumed = true;}
    // private boolean isConsumed() {return consumed;}
  }

  /**
   * the client should run a line of dialogue
   */
  public static class LineResult extends RunnerResult {
    protected Line line;

    public LineResult(String text){
      line = new Line(text);
    }

    public String getText(){
      return line.text;
    }
  }

  /**
   * client should run and parse command
   */
  public static class CommandResult extends RunnerResult {
    protected Command command;

    public CommandResult(String text){
      command = new Command(text);
    }

    public String getCommand(){
      return command.command;
    }

  }

  /**
   * Client should show a list of options and call the chooser choose before asking for the next line.
   */
  public static class OptionResult extends RunnerResult {
    protected Options       options;
    protected OptionChooser chooser;

    public OptionResult(Array<String> options, OptionChooser chooser){
      this.chooser = chooser;
      this.options = new Options(options);
    }

    public Array<String> getOptions(){
      return options.getOptions();
    }

    public void choose(int choice){
      chooser.choose(choice);
    }
  }

  /**
   * end of node reached
   */
  public static class NodeCompleteResult extends RunnerResult {
    public String nextNode;

    public NodeCompleteResult(String nextNode){
      this.nextNode = nextNode;
    }
  }

  /**
   * something went wrong
   */
  public static class YarnRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -5732778106783039900L;

    public YarnRuntimeException(String message){
      super(message);
    }

    public YarnRuntimeException(Throwable t){
      super(t);
    }

    public YarnRuntimeException(String message, Throwable t){
      super(message, t);
    }

  }

  /**
   * information that the client should handle
   */
  public static class Line {
    private String text;

    public Line(String text){
      this.text = text;
    }

    public String getText(){
      return text;
    }

    public void setText(String text){
      this.text = text;
    }
  }

  public static class Options {
    private Array<String> options;

    public Options(Array<String> options){
      this.options = options;
    }

    public Array<String> getOptions(){
      return options;
    }

    public void setOptions(Array<String> options){
      this.options = options;
    }
  }

  public static class Command {
    private String command;

    public Command(String command){
      this.command = command;
    }

    public String getCommand(){
      return command;
    }

    public void setCommand(String command){
      this.command = command;
    }
  }

  public static abstract class BaseVariableStorage implements VariableStorage {

  }

  public static class MemoryVariableStorage extends BaseVariableStorage {

    ObjectMap<String, Value> variables = new ObjectMap<String, Value>();

    @Override
    public void setValue(String name, Value value){
      variables.put(name, value);
    }

    @Override
    public Value getValue(String name){
      Value value = Value.NULL;
      if (variables.containsKey(name)) value = variables.get(name);
      return value;
    }

    @Override
    public void clear(){
      variables.clear();
    }

  }

  /**
   * a line localized into the current locale that is used in lines, options and shortcut options. Anything that is
   * user-facing.
   */
  public static class LocalisedLine {
    private String code;
    private String text;
    private String comment;

    public LocalisedLine(String code, String text, String comment){
      this.code = code;
      this.text = text;
      this.comment = comment;
    }

    public String getCode(){
      return code;
    }

    public void setCode(String code){
      this.code = code;
    }

    public String getText(){
      return text;
    }

    public void setText(String text){
      this.text = text;
    }

    public String getComment(){
      return comment;
    }

    public void setComment(String comment){
      this.comment = comment;
    }
  }

  /**
   * the standrad built in lib of functions and operators
   */
  private static class StandardLibrary extends Library {

    public StandardLibrary(){
      // operations

      registerFunction(TokenType.Add.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].add(params[1]);
        }
      });

      registerFunction(TokenType.Minus.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].sub(params[1]);
        }
      });

      registerFunction(TokenType.UnaryMinus.name(), 1, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].negative();
        }
      });

      registerFunction(TokenType.Divide.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].div(params[1]);
        }
      });

      registerFunction(TokenType.Multiply.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].mul(params[1]);
        }
      });

      registerFunction(TokenType.Modulo.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].mod(params[1]);
        }
      });

      registerFunction(TokenType.EqualTo.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].equals(params[1]);
        }
      });

      registerFunction(TokenType.NotEqualTo.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return !params[0].equals(params[1]);
        }
      });

      registerFunction(TokenType.GreaterThan.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].greaterThan(params[1]);
        }
      });

      registerFunction(TokenType.GreaterThanOrEqualTo.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].greaterThanOrEqual(params[1]);
        }
      });

      registerFunction(TokenType.LessThan.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].lessThan(params[1]);
        }
      });

      registerFunction(TokenType.LessThanOrEqualTo.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].lessThanOrEqual(params[1]);
        }
      });

      registerFunction(TokenType.And.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].asBool() && params[1].asBool();
        }
      });

      registerFunction(TokenType.Or.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].asBool() || params[1].asBool();
        }
      });

      registerFunction(TokenType.Xor.name(), 2, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return params[0].asBool() ^ params[1].asBool();
        }
      });

      registerFunction(TokenType.Not.name(), 1, new ReturningFunc() {
        @Override
        public Object invoke(Value... params){
          return !params[0].asBool();
        }
      });

      // end operations ===

    }
  }

}
