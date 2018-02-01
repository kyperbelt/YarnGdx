package com.kyper.yarn;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.StringBuilder;
import com.kyper.yarn.Lexer.Regex;
import com.kyper.yarn.Lexer.Token;
import com.kyper.yarn.Lexer.TokenList;
import com.kyper.yarn.Lexer.TokeniserException;
import com.kyper.yarn.Loader.NodeInfo.Position;
import com.kyper.yarn.Parser.Node;
import com.kyper.yarn.Program.ParseException;

public class Loader {
	public enum NodeFormat {
		Unkown, //type not known

		SingleNodeText, //plain text file with single node & no metadata

		Json, //json file containing multiple nodes with metadata

		Text //text containing multiple nodes with metadata
	}

	private Dialogue dialogue;
	private Program program;

	public Loader(Dialogue d) {
		if (d == null)
			throw new IllegalArgumentException("dialogue d is null");
		this.dialogue = d;
	}

	public Program getProgram() {
		return program;
	}

	/**
	 * print tokens
	 */
	public void printTokenList(TokenList token_list) {
		StringBuilder b = new StringBuilder();

		for (Token token : token_list) {
			b.appendLine(String.format("%1$s (%2$s line %3$s)", token.toString(), token.value, token.line_number));
		}

		dialogue.debug_logger.log("Tokens:");
		dialogue.debug_logger.log(b.toString());
	}

	/**
	 * print the prase tree of node
	 */
	public void printParseTree(Parser.ParseNode root) {
		dialogue.debug_logger.log("Parse Tree:");
		dialogue.debug_logger.log(root.printTree(0));
	}

	// the preprocessor that cleans up things to make it easier on ANTLR which we do not use yet lol
	// replaces \r\n with \n
	// adds in INDENTS and DEDENTS where necessary
	// replaces \t with four spaces
	// takes in a string of yarn and returns a string the compiler can then use
	//	private static class EmissionTupple {
	//			public final int depth;
	//			public final boolean emmited;
	//			public EmissionTupple(int depth,boolean emitted) {
	//				this.depth = depth;
	//				this.emmited = emitted;
	//			}
	//	}
	//	
	//	private String preProcessor(String node_text) {
	//		String processed = null;
	//		
	//		return processed;
	//	}

	/**
	 * given a bunch of raw text load all nodes that were inside it. You can call
	 * this multiple times to append to the collection of nodes, but note that new
	 * nodes will replace older ones with the same name.
	 * 
	 * @return the number of nodes that were loaded
	 */
	public Program load(String text, Library library, String file_name, Program include, boolean show_tokens,
			boolean show_parse_tree, String onlyconsider_node, NodeFormat format) {

		if (format == NodeFormat.Unkown) {
			format = getFormatFromFileName(file_name);
		}

		//final parsed nodes that were in file
		ObjectMap<String, Parser.Node> nodes = new ObjectMap<String, Parser.Node>();

		//load the raw data and get an array of node title-text pairs

		NodeInfo[] infos = getNodesFromText(text, format);
		
		//for soem weird reason its not used wtf
		@SuppressWarnings("unused")
		int nodes_loaded = 0;

		for (NodeInfo info : infos) {
			if (onlyconsider_node != null && !info.title.equals(onlyconsider_node))
				continue;

			//attempt to parse every nodel log if we encounter any errors

			try {

				if (nodes.containsKey(info.title)) {
					throw new IllegalStateException("Attempted to load node called " + info.title
							+ ", but a node with that name already exists!");
				}

				Lexer lexer = new Lexer();
				
				
				TokenList tokens = lexer.tokenise(info.body);
				
				

				if (show_tokens)
					printTokenList(tokens);
				
				Node node = new Parser(tokens, library).parse();
				

				//if this node is tagged "rawText", then preserve its source
				if (info.tags != null && !info.tags.isEmpty() && info.tags.contains("rawText")) {
					node.setSource(info.getBody());
				}

				node.setName(info.title);

				node.setNodeTags(info.tagsList());

				if (show_parse_tree)
					printParseTree(node);
				
				nodes.put(node.getName(), node);

				nodes_loaded++;

			} catch (Exception e) {
				if (e instanceof TokeniserException) {
					String message = String.format("In file %s: Error reading %s:%s", file_name, info.title,
							e.getMessage());
					throw new TokeniserException(message);

				} else if (e instanceof ParseException) {
					String message = String.format("In file %s: Error parsing node %s:%s", file_name, info.title,
							e.getMessage());
					throw new ParseException(message);
				} else if (e instanceof IllegalStateException) {
					String message = String.format("in file %s: Error reading node %s:%s", file_name, info.title,
							e.getMessage());
					throw new IllegalStateException(message);
				}else {
					throw new IllegalStateException("Something went wrong in Yarn:"+e.getMessage());
				}
			}

		}
		
		Compiler compiler = new Compiler(file_name);
		for (Entry<String, Parser.Node> n : nodes) {
			compiler.compileNode(n.value);
		}
		
		if(include!=null)
			compiler.program.include(include);

		return compiler.program;
	}

	private static NodeFormat getFormatFromFileName(String file_name) {
		NodeFormat format;
		String fn = file_name.toLowerCase();
		if (fn.endsWith(".json")) {
			format = NodeFormat.Json;
		} else if (fn.endsWith("yarn.txt")) {
			format = NodeFormat.Text;
		} else if (fn.endsWith(".node")) {
			format = NodeFormat.SingleNodeText;
		} else {
			throw new IllegalArgumentException(String.format("Unknown file format for file '%s'", file_name));
		}
		return format;
	}

	/**
	 * given either twine,json or xml input, return an array containing info about
	 * the nodes in text file
	 */
	@SuppressWarnings("unchecked")
	protected NodeInfo[] getNodesFromText(String text, NodeFormat format) {
		Array<NodeInfo> nodes = new Array<Loader.NodeInfo>();

		switch (format) {
		case SingleNodeText:
			//if it starts with a comment, treat it as a single line node file
			NodeInfo node = new NodeInfo();
			node.setTitle("Start");
			node.setBody(text);
			nodes.add(node);
			break;
		case Json:
			//parse it as json
			Json json = new Json();
			try {
				nodes = json.fromJson(Array.class, Loader.NodeInfo.class, text);
			} catch (Exception e) {
				if(e instanceof SerializationException) {
					dialogue.error_logger.log("Error parsing Yarn input: " + e.getMessage());
				}else if(e instanceof ClassCastException) {
					dialogue.error_logger.log("Unable to cast yarn");
				}
			}
			break;
		case Text:

			//check for the existance of atleast one '---'+newline sentinel, which divides 
			//the headers frmo the body

			//we use a regex to match either \r\n or \n line endings
			Matcher match = new Regex("---.?\n").match(text);
			if (!match.find()) {
				dialogue.error_logger.log("Error parsing input: text appears corrupt(no header)");
				break;
			}

			BufferedReader reader = new BufferedReader(new StringReader(text));
			Regex header_regex = new Regex("(?<field>.*): *(?<value>.*)");
			int line_number = 0;
			String line;
			try {
				while ((line = reader.readLine()) != null) {

					//create a new node
					NodeInfo info = new NodeInfo();

					//read header lines
					do {

						line_number++;

						//skip empty lines
						if (line.length() == 0) {
							continue;
						}

						//attempt ot parse header
						Matcher header_match = header_regex.match(line);

						if (!header_match.find()) {
							dialogue.error_logger
									.log(String.format("Line %s: cant parse header '%s'", line_number, line));
							continue;
						}

						String field = header_match.group("field");
						String value = header_match.group("value");

						Object convert_value;

						try {
							@SuppressWarnings("rawtypes")
							Class type = info.typeOfField(field);
							if (type == String.class) {
								convert_value = value;
							} else if (type == Integer.class) {
								convert_value = Integer.parseInt(value);
							} else if (type == Position.class) {
								String[] components = value.split(",");

								//expect 2 components x and y
								if (components.length != 2) {
									throw new IllegalStateException();
								}

								Position pos = new Position();
								pos.setX(Integer.parseInt(components[0]));
								pos.setY(Integer.parseInt(components[1]));

								convert_value = pos;
							} else {
								throw new IllegalStateException();
							}

							if (!info.setField(field, convert_value))
								throw new IllegalStateException();

						} catch (Exception e) {
							if (e instanceof IllegalArgumentException) {

							} else if (e instanceof IllegalStateException) {
								dialogue.error_logger.log(String.format("%s:Error setting %s: invalid value '%s'",
										line_number, field, value));
							} else if (e instanceof NumberFormatException) {
								dialogue.error_logger.log("could not convert " + field + ":" + e.getMessage());
							}
						}

					} while (!(line = reader.readLine()).equals("---"));

					line_number++;

					//were past the header
					Array<String> lines = new Array<String>();

					while ((line = reader.readLine()) != null && !line.equals("===")) {
						line_number++;
						lines.add(line);
					}

					//were done reading lines, zip em up into a string and store it in the body
					info.body = String.join("\n", lines);

					//add thsi node to the list
					nodes.add(info);

					//move on to the next line

				}

				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		default:
			throw new IllegalStateException("Unkown format " + format.name());
		}

		return nodes.toArray( NodeInfo.class);
	}

	public static class NodeInfo {

		private String title;
		private String body;

		//the raw tags
		private String tags;
		private int colorID;
		private Position position;

		//getters
		public String getTitle() {
			return title;
		}

		public String getBody() {
			return body;
		}

		public String getTags() {
			return tags;
		}

		public int getColorID() {
			return colorID;
		}

		public Position getPosition() {
			return position;
		}

		//setters
		public void setTitle(String title) {
			this.title = title;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public void setTags(String tags) {
			this.tags = tags;
		}

		public void setColorId(int colorId) {
			this.colorID = colorId;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		public Array<String> tagsList() {
			//no tags return empty
			if (tags == null || tags.length() == 0) {
				return new Array<String>();
			}
			return new Array<String>(tags.trim().split(" "));
		}

		public static class Position {
			private int x;
			private int y;

			public Position() {
				x = 0;
				y = 0;
			}

			public Position(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public void setX(int x) {
				this.x = x;
			}

			public void setY(int y) {
				this.y = y;
			};

			public int getX() {
				return x;
			}

			public int getY() {
				return y;
			}
		}

		@Override
		public String toString() {
			return String.format("Node{ title:%1$s ,body:%2$s}", title, body);
		}

		public boolean setField(String name, Object value) {
			if (name.equals("title")) {
				title = (String) value;
				return true;
			} else if (name.equals("tags")) {
				tags = (String) value;
				return true;
			} else if (name.equals("colorID")) {
				colorID = (Integer) value;
				return true;
			} else if (name.equals("position")) {
				position = (Position) value;
				return true;
			} else if (name.equals("body")) {
				body = (String) value;
				return true;
			}

			return false;
		}

		@SuppressWarnings("rawtypes")
		public Class typeOfField(String name) {
			if (name.equals("title")) {
				return String.class;
			} else if (name.equals("tags")) {
				return String.class;
			} else if (name.equals("colorID")) {
				return Integer.class;
			} else if (name.equals("position")) {
				return Position.class;
			} else if (name.equals("body")) {
				return String.class;
			}

			throw new IllegalArgumentException("field not found " + name);
		}

	}

}
