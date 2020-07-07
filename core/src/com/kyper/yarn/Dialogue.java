package com.kyper.yarn;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.kyper.yarn.Analyser.Context;
import com.kyper.yarn.Library.ReturningFunc;
import com.kyper.yarn.VirtualMachine.CommandHandler;
import com.kyper.yarn.VirtualMachine.DialogueCompleteHandler;
import com.kyper.yarn.VirtualMachine.ExecutionState;
import com.kyper.yarn.VirtualMachine.LineHandler;
import com.kyper.yarn.VirtualMachine.NodeCompleteHandler;
import com.kyper.yarn.VirtualMachine.NodeStartHandler;
import com.kyper.yarn.VirtualMachine.OptionsHandler;
import com.kyper.yarn.VirtualMachine.TokenType;

public class Dialogue {

	protected VariableStorage continuity;

	public YarnLogger debug_logger;
	public YarnLogger error_logger;

	// node we start from
	public static final String DEFAULT_START = "Start";
	public static final String FORMAT_FUNCTION_PLACEHOLDER = "<VALUE PLACEHOLDER>";

	// the program is the compiled yarn program
	private Program program;

	// the library contains all the functions and operators we know about
	protected Library library;

	private VirtualMachine vm;

	// collection of nodes that we've seen
	public HashMap<String, Integer> visited_node_count = new HashMap<String, Integer>();

	protected boolean executionComplete;

	/**
	 * creates a yarn dialogue
	 *
	 * @param continuity - will be used to store/get values
	 * @param debug      - debug logger implementation
	 * @param error      - error logger implementation
	 */
	public Dialogue(VariableStorage continuity, YarnLogger debug, YarnLogger error) {
		if (continuity == null) {
			throw new IllegalArgumentException(StringUtils.format("Dialogue %s cannot be null", "VariableStorage"));
		}
		this.continuity = continuity;

		this.vm = new VirtualMachine(this);

		library = new Library();

		this.debug_logger = debug;
		this.error_logger = error;
		this.executionComplete = false;

		library.importLibrary(new StandardLibrary());

		// register the "visited" function which returns true if we've visited
		// a node previously (nodes are marked as visited when we leave them)
		library.registerFunction("visited", -1, yarnFunctionIsNodeVisited);

		// register the visitCount function which returns athe number of times
		// a node has been run(increments on node end)
		// no parameters = check the current node
		library.registerFunction("visitCount", -1, yarnFunctionNodeVisitCount);

	}

	public Library getLibrary() {
		return library;
	}

	protected void setLibrary(Library lib) {
		this.library = lib;
	}

	public ExecutionState getExecutionState() {
		return vm.getExecutionState();
	}

	/**
	 * creates a dialogue with a default debug and error implementation
	 *
	 * @param continuity - will be used to store/get values
	 */
	public Dialogue(VariableStorage continuity) {
		this(continuity, new YarnLogger() {

			@Override
			public void log(String message) {
				System.out.println("YarnInfo:" + message);
			}
		}, new YarnLogger() {
			@Override
			public void log(String message) {
				System.out.println("YarnWarning:" + message);
			}
		});
	}

	public boolean isActive() {
		return vm.getExecutionState() != ExecutionState.Stopped;
	}

	public void setSelectedOption(int selectedOptionId) {
		vm.setSelectedOption(selectedOptionId);
	}

	public void setNode() {
		setNode(DEFAULT_START);
	}

	public void setNode(String name) {
		if (vm != null) {
			vm.setNode(name);
		} else {
			throw new IllegalStateException("VirtualMachine is null");
		}

	}

	public void continueRunning() {
		if (vm.getExecutionState() == VirtualMachine.ExecutionState.Running)
			return;
		vm.continueRunning();
	}

	public void stop() {
		if (vm != null)
			vm.stop();
	}

	public Set<String> allNodes() {
		return program.nodes.keySet();
	}

	public String currentNode() {
		return vm == null ? null : vm.currentNodeName();
	}

	public String getStringIDForNode(String nodeName) {
		if (program.nodes.size() == 0) {
			error_logger.log("No nodes are loaded!");
			return null;
		} else if (program.nodes.containsKey(nodeName)) {
			return "line:" + nodeName;
		} else {
			error_logger.log("No node named " + nodeName);
			return null;
		}

	}

	public List<String> getTagsForNode(String nodeName) {
		if (program.nodes.size() == 0) {
			error_logger.log("No nodes are loaded!");
			return null;
		} else if (program.nodes.containsKey(nodeName)) {
			return program.getTagsForNode(nodeName);
		} else {
			error_logger.log("No node named " + nodeName);
			return null;
		}
	}

	HashMap<String, String> _tx4n;

	protected Map<String, Program.Node> getAllNodes() {
		return program.nodes;
	}

	public HashMap<String, String> getTextForAllNodes() {
		if (_tx4n == null)
			_tx4n = new HashMap<String, String>();
		_tx4n.clear();
		for (Map.Entry<String, Program.Node> entry : program.nodes.entrySet()) {
			String text = program.getTextForNode(entry.getKey());

			if (text == null)
				continue;

			_tx4n.put(entry.getKey(), text);
		}

		return _tx4n;
	}

	/**
	 * get the source code for the node
	 *
	 * @param node
	 * @return
	 */
	public String getTextForNode(String node) {
		if (program.nodes.size() == 0) {
			error_logger.log("no nodes are loaded!");
			return null;
		} else if (program.nodes.containsKey(node)) {
			return program.getTextForNode(node);
		} else {
			error_logger.log("no node named " + node);
			return null;
		}
	}

//	public void addStringTable(HashMap<String, String> string_table) {
//		program.loadStrings(string_table);
//	}

	public HashMap<String, String> getStringTable() {
		return program.strings;
	}

//	protected HashMap<String, LineInfo> getStringInfoTable() {
//		return program.line_info;
//	}

	/**
	 * unload all nodes
	 *
	 * @param clear_visisted_nodes
	 */
	public void unloadAll(boolean clear_visisted_nodes) {
		if (clear_visisted_nodes)
			visited_node_count.clear();
		program = null;
	}

	public LineHandler getLineHandler() {
		return vm.lineHandler;
	}

	public void setLineHandler(LineHandler lineHandler) {
		this.vm.lineHandler = lineHandler;
	}

	public OptionsHandler getOptionsHandler() {
		return vm.optionHandler;
	}

	public void setOptionsHandler(OptionsHandler optionsHandler) {
		this.vm.optionHandler = optionsHandler;
	}

	public CommandHandler getCommandHandler() {
		return vm.commandHandler;
	}

	public void setCommandHandler(CommandHandler commandHandler) {
		this.vm.commandHandler = commandHandler;
	}

	public NodeCompleteHandler getCompleteHandler() {
		return vm.nodeCompleteHandler;
	}

	public void setNodeCompleteHandler(NodeCompleteHandler nodeCompleteHandler) {
		this.vm.nodeCompleteHandler = nodeCompleteHandler;
	}

	public NodeStartHandler getStartHandler() {
		return vm.nodeStartHandler;
	}

	public void setNodeStartHandler(NodeStartHandler startHandler) {
		this.vm.nodeStartHandler = startHandler;
	}

	public DialogueCompleteHandler getDialogueCompleteHandler() {
		return vm.dialogueCompleteHandler;
	}

	public void setDialogueCompleteHandler(DialogueCompleteHandler diagCompleteHandler) {
		this.vm.dialogueCompleteHandler = diagCompleteHandler;
	}

	public String getByteCode() {
		return program.dumpCode(library);
	}

	public boolean nodeExists(String node_name) {
		if (program == null) {
			error_logger.log("no nodes compiled");
			return false;
		}
		if (program.nodes.size() == 0) {
			error_logger.log("no nodes in program");
			return false;
		}

		return program.nodes.containsKey(node_name);

	}

	public void setProgram(Program program) {
		this.program = program;
		vm.setProgram(program);
		vm.resetState();
	}

	public Program getProgram() {
		return program;
	}

	public void addProgram(Program program) {
		if (this.program == null) {
			setProgram(program);
			return;
		} else {
			this.program = VirtualMachine.combinePrograms(this.program, program);
		}
	}

	protected void printState() {
		if (!isActive())
			return;
		System.out.println("Current VM State:" + vm.getExecutionState().name());
	}

	public void analyse(Context context) {
		context.addProgramToAnalysis(program);
	}

	public Set<String> getvisitedNodes() {
		return visited_node_count.keySet();
	}

	public void setVisitedNodes(ArrayList<String> visited) {
		visited_node_count.clear();
		for (String string : visited) {
			visited_node_count.put(string, 1);
		}
	}

	/**
	 * unload all nodes clears visited nodes
	 */
	public void unloadAll() {
		unloadAll(true);
	}

	public static String expandFormatFunctions(String input, String locale) {
		StringBuilder builder = new StringBuilder();
		ArrayList<ParsedFormatFunction> formatFunctions = new ArrayList<ParsedFormatFunction>();
		 parseFormatFunctions(input, builder,  formatFunctions);
		 String lineWithReplacements = builder.toString();

         for (int i = 0; i < formatFunctions.size(); i++)
         {
             ParsedFormatFunction function = formatFunctions.get(i);

             // Apply the "select" format function
             if (function.functionName == "select")
             {
            	 String replacement = null;
                 if ((replacement = function.data.get(function.value)) == null)
                 {
                     replacement = StringUtils.format("<no replacement for %s>",function.value);
                 }

                 // Insert the value if needed
                 replacement = replacement.replace(FORMAT_FUNCTION_PLACEHOLDER, function.value);

                 lineWithReplacements = lineWithReplacements.replace("{" + i + "}", replacement);
             }
             else
             {
                 // Apply the "plural" or "ordinal" format function
            	 double value = 0.0;
            	 try {
            	 value = Double.parseDouble(function.value);
            	 }catch(NumberFormatException e) {
            		 throw new IllegalArgumentException(StringUtils.format("Error while pluralising line '%s': '%s' is not a number",input,function.value));
            	 }

                 NumberPlurals.PluralCase pluralCase;

                 switch (function.functionName)
                 {
                     case "plural":
                         pluralCase = NumberPlurals.GetCardinalPluralCase(locale, value);
                         break;
                     case "ordinal":
                         pluralCase = NumberPlurals.GetOrdinalPluralCase(locale, value);
                         break;
                     default:
                    	 throw new IllegalArgumentException(StringUtils.format("Unknown formatting function '%s}' in line '%s'",function.functionName,input));
                 }
                 
                 String replacement = null;
                 if ((replacement = function.data.get(pluralCase.toString().toLowerCase(Locale.ROOT))) == null)
                 {
                     replacement = StringUtils.format("<no replacement for %s",function.value);
                 }

                 // Insert the value if needed
                 replacement = replacement.replace(FORMAT_FUNCTION_PLACEHOLDER, function.value);

                 lineWithReplacements = lineWithReplacements.replace("{" + i + "}", replacement);

             }
         }
         return lineWithReplacements;
		
	}

	public static void parseFormatFunctions(String input,StringBuilder lineWithReplacements,ArrayList<ParsedFormatFunction> parsedFunctions) {
		StringReader reader = new StringReader(input);
		int next;
		try {
			while((next = reader.read())!=-1) {
				
				char c = (char)next;
				if(c!='[') {
					//plain text
					lineWithReplacements.append(c);
				}else {
					ParsedFormatFunction function = new ParsedFormatFunction();
					
					//start of format function
					//struct
					//[ name "value" key1="value1" key2="value2" ]
					
					 // Read the name
                    function.functionName = FormatFunctionHelpers.expectID(input, reader);
					
                 // Ensure that only valid function names are used
                    switch (function.functionName) {
                        case "select":
                        break;
                        case "plural":
                        break;
                        case "ordinal":
                        break;
                        default:
                        	throw new IllegalArgumentException(StringUtils
            						.format("Invalid formatting function '%s' in line \'%s'",function.functionName, input));
                    }
                    
                    function.value = FormatFunctionHelpers.expectString(input, reader);
                    function.data = new HashMap<String, String>();
                    
                    // parse and read the data for this format function
                    while (true)
                    {
                        FormatFunctionHelpers.consumeWhiteSpace(input, reader);

                        reader.mark(input.getBytes().length);
                        int peek = reader.read();
                        reader.reset();
                        if ((char)peek == ']')
                        {
                            // we're done adding parameters
                            break;
                        }

                        // this is a key-value pair
                        String key = FormatFunctionHelpers.expectID(input,reader);
                        FormatFunctionHelpers.expectCharacter(input,reader,'=');
                        String value = FormatFunctionHelpers.expectString(input, reader);

                        if (function.data.containsKey(key))
                        {
                        	throw new IllegalArgumentException(StringUtils
            						.format("Duplicate value '%s' in format function inside line '%s'",key, input));
                        }

                        function.data.put(key, value);

                    }
                    
                    FormatFunctionHelpers.expectCharacter(input, reader, ']');
                    
                    parsedFunctions.add(function);
                    
                    lineWithReplacements.append("{" + (parsedFunctions.size() - 1) + "}");
                    
				}
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class FormatFunctionHelpers {

		private static String expectID(String input,StringReader stringReader) throws IOException {
			consumeWhiteSpace(input, stringReader);
	        StringBuilder idStringBuilder = new StringBuilder();
	        
	        // Read the first character, which must be a letter
	        int tempNext = stringReader.read();
	        assertNotEndOfInput(input,tempNext);
	        char nextChar = (char)tempNext;

	        if (Character.isLetter(nextChar) || nextChar == '_')
	        {
	            idStringBuilder.append((char)tempNext);
	        }
	        else
	        {
	        	System.out.println("offendingChar:"+Character.toString(nextChar));
	        	throw new IllegalArgumentException(StringUtils
						.format("Expected an identifier inside a format function in line '%s' ", input));
	        }

	        // Read zero or more letters, numbers, or underscores
	        while (true)
	        {
	        	stringReader.mark(100);
	            tempNext = stringReader.read();
	            stringReader.reset();
	            if (tempNext == -1)
	            {
	                break;
	            }
	            nextChar = (char)tempNext;
	            if (Character.isLetterOrDigit(nextChar) || (char)tempNext == '_')
	            {
	                idStringBuilder.append((char)tempNext);
	                stringReader.read(); // consume it
	            }
	            else
	            {
	                // no more
	                break;
	            }
	        }
	        return idStringBuilder.toString();
		}

		private static String expectString(String input,StringReader stringReader) throws IOException {
			consumeWhiteSpace(input, stringReader);
			StringBuilder builder = new StringBuilder();
			
			int tempNext = stringReader.read();
			assertNotEndOfInput(input, tempNext);
			
			char nextChar = (char)tempNext;
			if(nextChar != '"') {
				System.out.println("offendingChar:"+Character.toString(nextChar));
				throw new IllegalArgumentException(StringUtils
						.format("Expected a string inside a format function in line '%s' ", input));
			}
			
			while(true) {
				tempNext = stringReader.read();
                assertNotEndOfInput(input,tempNext);
                nextChar = (char)tempNext;                            

                if (nextChar == '"')
                {
                    // end of string - consume it but don't
                    // append to the final collection
                    break;
                }
                else if (nextChar == '\\')
                {
                    // an escaped quote or backslash
                    int nextNext = stringReader.read();
                    assertNotEndOfInput(input,nextNext);
                    int nextNextChar = (char)nextNext;
                    if (nextNextChar == '\\' || nextNextChar == '"' || nextNextChar == '%')
                    {
                    	builder.append(nextNextChar);
                    } 
                } else if (nextChar == '%') {
                	builder.append(FORMAT_FUNCTION_PLACEHOLDER);
                }
                else
                {
                	builder.append(nextChar);
                }

			}
			return builder.toString();
		}

		private static void expectCharacter(String input, StringReader stringReader, char character)
				throws IOException {
			consumeWhiteSpace(input, stringReader);
			int tempNext = stringReader.read();
			assertNotEndOfInput(input, tempNext);
			if ((char) tempNext != character) {
				System.out.println("offendingChar:"+Character.toString((char)tempNext));
				throw new IllegalArgumentException(StringUtils
						.format("Expected a '%s' inside of format function in line '%s' ", character, input));
			}
		}

		private static void assertNotEndOfInput(String input, int value) {
			if (value == -1) {
				throw new IllegalArgumentException(
						StringUtils.format("Unexpected end of line inside a format function in line'%s'", input));
			}
		}

		private static void consumeWhiteSpace(String input, StringReader stringReader) throws IOException {
			consumeWhiteSpace(input, stringReader, false);
		}

		private static void consumeWhiteSpace(String input, StringReader stringReader, boolean allowEndOfLine)
				throws IOException {
			while (true) {
				stringReader.mark(100);
				int tempNext = stringReader.read();
				stringReader.reset();
				if (tempNext == -1 && allowEndOfLine == false) {
					throw new IllegalArgumentException(
							StringUtils.format("Unexpected end of line inside a format function in line %s", input));
				}

				if (Character.isWhitespace((char)tempNext)) {
					// consume it and continue
					stringReader.read();
				} else {
					// no more whitespace ahead; don't
					// consume it, but instead stop eating
					// whitespace
					return;
				}
			}
		}
	}

	/**
	 * A function exposed to yarn that returns the number of times a node has been
	 * run. if no parameters are supplied, returns the number of times the current
	 * node has been run.
	 */
	protected ReturningFunc yarnFunctionNodeVisitCount = new ReturningFunc() {
		@Override
		public Object invoke(Value... params) {

			// determin ethe node were checking
			String node_name;

			if (params.length == 0) {
				// no marams? check the current node
				node_name = vm.currentNodeName();
			} else if (params.length == 1) {
				// a parameter? check the named node
				node_name = params[0].asString();
				// ensure node existance
				if (!nodeExists(node_name)) {
					String error = String.format(" the node %s does not exist.", node_name);
					error_logger.log(error);
					return 0;
				}
			} else {
				// we go ttoo many parameters
				String error = String.format("incorrect number of parameters visitcount expect 0 or 1, got %s",
						params.length);
				error_logger.log(error);
				return 0;
			}
			int visit_count = 0;
			if (visited_node_count.containsKey(node_name))
				visit_count = visited_node_count.get(node_name);
			return visit_count;
		}
	};

	protected ReturningFunc yarnFunctionIsNodeVisited = new ReturningFunc() {
		@Override
		public Object invoke(Value... params) {
			boolean visited = (Integer) yarnFunctionNodeVisitCount.invoke(params) > 0;
			return visited;
		}
	};

	protected Program.Node getCurrentNode() {
		return (vm != null) ? vm.getCurrentNode() : null;
	}

	// ======================================================================================
	/**
	 * load all nodes contained in the text to the dialogue unless otherwise
	 * specified
	 *
	 * @param text          - the text containing node info
	 * @param file_name     - the name of the 'file' used for debug purposes
	 * @param show_tokens   - if true will show the tokens generated by the lexer
	 * @param show_tree     - if true will show a tree structure generated by the
	 *                      parser
	 * @param only_consider - if not null, only the specified node will be
	 *                      considered for loading;all else will be ignored.
	 */
//		public void loadString(String text, String file_name, boolean show_tokens, boolean show_tree,
//				String only_consider) {
//				System.err.println("Not implemented - Dialogue.loadString");
//				System.exit(1);
//			if (debug_logger == null) {
//				throw new YarnRuntimeException("DebugLogger must be set before loading");
//			}
	//
//			if (error_logger == null)
//				throw new YarnRuntimeException("ErrorLogger must be set before loading");
	//
//			// try to infer type
//			NodeFormat format;
//			if (text.startsWith("[")) {
//				format = NodeFormat.Json;
//			} else if (text.contains("---")) {
//				format = NodeFormat.Text;
//			} else {
//				format = NodeFormat.SingleNodeText;
//			}
	//
	//
//			program = loader.load(text, library, file_name, program, show_tokens, show_tree, only_consider, format);
//		}

	/**
	 * load all nodes contained in the text to the dialogue unless otherwise
	 * specified
	 *
	 * @param text          - the text containing node info
	 * @param name          - the name of the 'file' used for debug purposes
	 * @param only_consider - if not null, only the specified node will be
	 *                      considered for loading;all else will be ignored.
	 */
//		public void loadString(String text, String name, String only_consider) {
//			loadString(text, name, false, false, only_consider);
//		}

	/**
	 * load all nodes contained in the text to the dialogue unless otherwise
	 * specified
	 *
	 * @param text - the text containing node info
	 * @param name - the name of the 'file' used for debug purposes
	 */
//		public void loadString(String text, String name) {
//			loadString(text, name, null);
//		}

	/**
	 *
	 * @param path          - path to the file to load
	 * @param show_tokens   - if true will show the tokens generated by the lexer
	 * @param show_tree     - if true will show a tree structure generated by the
	 *                      parser
	 * @param only_consider - if not null, only the specified node will be
	 *                      considered for loading;all else will be ignored.
	 */
//		public void loadFile(Path path, boolean show_tokens, boolean show_tree, String only_consider) throws IOException {
////			String input = Gdx.files.internal(file).readString();
//				String input = new String(Files.readAllBytes(path));
//				loadString(input, path.toString(), show_tokens, show_tree, only_consider);
//		}

//		public void loadFile(String file, boolean show_tokens, boolean show_tree, String only_consider) {
//			String input=null;
//			try {
//				input = new String(Files.readAllBytes(Paths.get(file)));
//			} catch (IOException e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
	//
//			loadString(input, file, show_tokens, show_tree, only_consider);
	//
//		}

	/**
	 *
	 * @param path          - path to the file to load
	 * @param only_consider - if not null, only the specified node will be
	 *                      considered for loading;all else will be ignored.
	 */
//		public void loadFile(Path path, String only_consider) throws IOException {
//			loadFile(path, false, false, only_consider);
//		}

	/**
	 * @param path - path to the file to load
	 *
	 */
//		public void loadFile(Path path) throws IOException {
//			loadFile(path, null);
//		}

	/**
	 * get the next result - if it is null it will attempt to populate it
	 *
	 * @return
	 */
//	public RunnerResult getNext() {
//		// TODO: remove?
//		if (vm == null) // we are not running so return null
//			return null;
//		checkNext();// make sure there is a next result
//		RunnerResult r = next_result;
//		next_result = null;
//		return r;
//	}

	/**
	 * get the next result - will return null if there is no result
	 *
	 * @return //
	 */
//	public RunnerResult nextRaw() {
//		return next_result;
//	}

//	/**
//	 * checks the next result - if it is null it will attempt to populate it
//	 *
//	 * @return
//	 */
//	public RunnerResult checkNext() {
//		if (next_result == null)
//			populateNext();
//		return next_result;
//	}
//
//	/**
//	 * check if the next result is an options result
//	 *
//	 * @return
//	 */
//	public boolean isNextOptions() {
//		return checkNext() instanceof OptionResult;
//	}
//
//	/**
//	 * check if the next result is a line result
//	 *
//	 * @return
//	 */
//	public boolean isNextLine() {
//		return checkNext() instanceof LineResult;
//	}
//
//	/**
//	 * check if the next result is a custom command result
//	 *
//	 * @return
//	 */
//	public boolean isNextCommand() {
//		return checkNext() instanceof CommandResult;
//	}
//
//	/**
//	 * check if the next result is a node complete result
//	 *
//	 * @return
//	 */
//	public boolean isNextComplete() {
//		return checkNext() instanceof NodeCompleteResult;
//	}
//
//	/**
//	 * get the next result as a line if the next result is not a line it will return
//	 * null
//	 *
//	 * @return
//	 */
//	public LineResult getNextAsLine() {
//		return isNextLine() ? (LineResult) getNext() : null;
//	}

//	/**
//	 * get the next result as an options result if the next result is not an options
//	 * result then this will return null
//	 *
//	 * @return
//	 */
//	public OptionResult getNextAsOptions() {
//		return isNextOptions() ? (OptionResult) getNext() : null;
//	}
//
//	/**
//	 * get the next result as a command result (must be parsed by the programmer) if
//	 * the next result is not a commandreslt then this will return null
//	 *
//	 * @return
//	 */
//	public CommandResult getNextAsCommand() {
//		return isNextCommand() ? (CommandResult) getNext() : null;
//	}
//
//	/**
//	 * get the next result as a node complete result if the next result is not a
//	 * node compelte result then this will return null
//	 *
//	 * @return
//	 */
//	public NodeCompleteResult getNextAsComplete() {
//		return isNextComplete() ? (NodeCompleteResult) getNext() : null;
//	}
//
//	// we update the vm until the next result is no longer null
//	private void populateNext() {
//		while (next_result == null)
//			if (!update())
//				break;
//
//	}

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
//	/**
//	 * indicates something the client should do
//	 */
//	public static abstract class RunnerResult {
//		// private boolean consumed = false;
//		// public void consume() {consumed = true;}
//		// private boolean isConsumed() {return consumed;}
//	}

//	/**
//	 * the client should run a line of dialogueQQ
//	 */
//	public static class LineResult extends RunnerResult {
//		protected Line line;
//
//		public LineResult(String text) {
//			line = new Line(text);
//		}
//
//		public String getText() {
//			return line.;
//		}
//	}

//	/**
//	 * client should run and parse command
//	 */
//	public static class CommandResult extends RunnerResult {
//		protected Command command;
//
//		public CommandResult(String text) {
//			command = new Command(text);
//		}
//
//		public String getCommand() {
//			return command.command;
//		}
//
//	}

//	/**
//	 * Client should show a list of options and call the chooser choose before
//	 * asking for the next line.
//	 */
//	public static class OptionResult extends RunnerResult {
//		protected Options options;
//		protected OptionChooser chooser;
//
//		public OptionResult(ArrayList<String> options, OptionChooser chooser) {
//			this.chooser = chooser;
//			this.options = new Options(options);
//		}
//
//		public ArrayList<String> getOptions() {
//			return options.getOptions();
//		}
//
//		public void choose(int choice) {
//			chooser.choose(choice);
//		}
//	}

//	/**
//	 * end of node reached
//	 */
//	public static class NodeCompleteResult extends RunnerResult {
//		public String next_node;
//
//		public NodeCompleteResult(String next_node) {
//			this.next_node = next_node;
//		}
//	}

	/**
	 * something went wrong
	 *
	 */
	public static class YarnRuntimeException extends RuntimeException {
		private static final long serialVersionUID = -5732778106783039900L;

		public YarnRuntimeException(String message) {
			super(message);
		}

		public YarnRuntimeException(Throwable t) {
			super(t);
		}

		public YarnRuntimeException(String message, Throwable t) {
			super(message, t);
		}

	}

	/**
	 * option chooser lets client tell dialogue the response selected by the user
	 */
	public static interface OptionChooser {
		public void choose(int selected_option_index);
	}

	/**
	 * logger to let the client send output to the console logging/error logging
	 */
	public static interface YarnLogger {
		public void log(String message);
	}

	/**
	 *
	 *
	 * information that the client should handle
	 */
	public static class Line {
		public String id;
		public String[] substitutions;

		public Line(String id) {
			this.id = id;
			substitutions = new String[0];
		}
	}

	public static class OptionSet {
		private Option[] options;

		public OptionSet(Option... options) {
			this.options = options;
		}

		public Option[] getOptions() {
			return options;
		}
	}

	public static class Option {
		private Line line;
		private int id;
		private String destination;

		public Option(Line line, int id, String destination) {
			this.setLine(line);
			this.setId(id);
			this.setDestination(destination);
		}

		public Line getLine() {
			return line;
		}

		private void setLine(Line line) {
			this.line = line;
		}

		public int getId() {
			return id;
		}

		private void setId(int id) {
			this.id = id;
		}

		public String getDestination() {
			return destination;
		}

		private void setDestination(String destination) {
			this.destination = destination;
		}

	}

//	public static class Options {
//		private ArrayList<String> options;
//
//		public Options(ArrayList<String> options) {
//			this.options = options;
//		}
//
//		public ArrayList<String> getOptions() {
//			return options;
//		}
//
//		public void setOptions(ArrayList<String> options) {
//			this.options = options;
//		}
//	}

	public static class Command {
		private String command;

		public Command(String command) {
			this.command = command;
		}

		public String getCommand() {
			return command;
		}
	}

	/**
	 * variable storage TODO: try to use {@link com.kyper.yarn.DialogueData
	 * UserData}
	 */
	public static interface VariableStorage {
		public void setValue(String name, Value value);

		public void setValue(String name, String stringValue);

		public void setValue(String name, float floatValue);

		public void setValue(String name, boolean boolValue);

		public Value getValue(String name);

		public void clear();
	}

	public static abstract class BaseVariableStorage implements VariableStorage {
		@Override
		public void setValue(String name, String stringValue) {
			Value val = new Value(stringValue);
			setValue(name, val);
		}

		@Override
		public void setValue(String name, float floatValue) {
			Value val = new Value(floatValue);
			setValue(name, val);

		}

		@Override
		public void setValue(String name, boolean boolValue) {
			Value val = new Value(boolValue);
			setValue(name, val);

		}
	}

	public static class MemoryVariableStorage extends BaseVariableStorage {

		HashMap<String, Value> variables = new HashMap<String, Value>();

		@Override
		public void setValue(String name, Value value) {
			variables.put(name, value);
		}

		@Override
		public Value getValue(String name) {
			Value value = Value.NULL;
			if (variables.containsKey(name))
				value = variables.get(name);
			return value;
		}

		@Override
		public void clear() {
			variables.clear();
		}

	}

	public static class ParsedFormatFunction {
		public String functionName = "";
		public String value = "";
		public HashMap<String, String> data = new HashMap<String, String>();
	}

	/**
	 * a line localized into the current locale that is used in lines, options and
	 * shortcut options. Anything that is user-facing.
	 */
//	public static class LocalisedLine {
//		private String code;
//		private String text;
//		private String comment;
//
//		public LocalisedLine(String code, String text, String comment) {
//			this.code = code;
//			this.text = text;
//			this.comment = comment;
//		}
//
//		public String getCode() {
//			return code;
//		}
//
//		public String getText() {
//			return text;
//		}
//
//		public String getComment() {
//			return comment;
//		}
//
//		public void setCode(String code) {
//			this.code = code;
//		}
//
//		public void setText(String text) {
//			this.text = text;
//		}
//
//		public void setComment(String comment) {
//			this.comment = comment;
//		}
//	}

	/**
	 * the standrad built in lib of functions and operators
	 */
	private static class StandardLibrary extends Library {

		public StandardLibrary() {
			// operations

			registerFunction(TokenType.Add.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].add(params[1]);
				}
			});

			registerFunction(TokenType.Minus.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].sub(params[1]);
				}
			});

			registerFunction(TokenType.UnaryMinus.name(), 1, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].negative();
				}
			});

			registerFunction(TokenType.Divide.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].div(params[1]);
				}
			});

			registerFunction(TokenType.Multiply.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].mul(params[1]);
				}
			});

			registerFunction(TokenType.Modulo.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].mod(params[1]);
				}
			});

			registerFunction(TokenType.EqualTo.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].equals(params[1]);
				}
			});

			registerFunction(TokenType.NotEqualTo.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return !params[0].equals(params[1]);
				}
			});

			registerFunction(TokenType.GreaterThan.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].greaterThan(params[1]);
				}
			});

			registerFunction(TokenType.GreaterThanOrEqualTo.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].greaterThanOrEqual(params[1]);
				}
			});

			registerFunction(TokenType.LessThan.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].lessThan(params[1]);
				}
			});

			registerFunction(TokenType.LessThanOrEqualTo.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].lessThanOrEqual(params[1]);
				}
			});

			registerFunction(TokenType.And.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].asBool() && params[1].asBool();
				}
			});

			registerFunction(TokenType.Or.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].asBool() || params[1].asBool();
				}
			});

			registerFunction(TokenType.Xor.name(), 2, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return params[0].asBool() ^ params[1].asBool();
				}
			});

			registerFunction(TokenType.Not.name(), 1, new ReturningFunc() {
				@Override
				public Object invoke(Value... params) {
					return !params[0].asBool();
				}
			});

			// end operations ===

		}
	}

}
