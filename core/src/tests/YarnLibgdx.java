//package tests;
//
//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input.Keys;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.utils.ObjectMap;
//import com.badlogic.gdx.utils.ObjectMap.Entry;
//import com.badlogic.gdx.utils.StringBuilder;
//import com.kyper.yarn.Dialogue;
//import com.kyper.yarn.Dialogue.CommandResult;
//import com.kyper.yarn.Dialogue.LineResult;
//import com.kyper.yarn.Dialogue.NodeCompleteResult;
//import com.kyper.yarn.Dialogue.OptionResult;
//import com.kyper.yarn.Library.Function;
//import com.kyper.yarn.DialogueData;
//import com.kyper.yarn.Value;
//
//public class YarnLibgdx extends ApplicationAdapter {
//
//	// what keys to check for input when options are presented - supports up to 5
//	// options
//	// NOTE: dialogue can support an arbitrary number of options i am just choosing
//	// to only support 5 for this example - not that we will even use that many.
//	int[] OP_KEYS = { Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5 };
//
//	// used to place font
//	int screenwidth;
//	int screenheight;
//
//	// file of ship containing all the ship dialogue
//	String ship_file = "ship.json";
//
//	// file of sally containing all the sally dialogue
//	String sally_file = "sally.json";
//
//	// print all the tokens that are spat out by the parser
//	boolean show_tokens = false;
//
//	// print out tree created by the parser from list of tokens
//	boolean show_parse_tree = false;
//
//	// will only load this node - loads entire file if this is null
//	String only_consider = null;
//
//	// Used to store values by dialogue (or your game, access is not limited) - can
//	// export/import as json
//	DialogueData data = new DialogueData("Test_data");
//
//	// this is the class that loads and hold the dialogue for now
//	Dialogue test_dialogue;
//
//	// our results
//	LineResult current_line = null;
//	OptionResult current_options = null;
//	CommandResult current_command = null;
//	NodeCompleteResult node_complete = null;
//
//	// test font & spritebatch
//	BitmapFont font;
//	SpriteBatch batch;
//
//	// String builder to avoid string creation-because you know strings in gdx is
//	// bad n stuff
//	StringBuilder option_string;
//
//	// some strings we can re-use -- because making STRINGS IS BAD!
//	final String ps = "PRESS SPACE TO CONTINUE";
//	final String finished = "You finished!";
//	final String vars = "Variables:";
//	final String see_ship_var = "$should_see_ship";
//	final String sally_warning_var = "$sally_warning";
//	final String talk_to_ship = "Talk to Ship";
//	final String talk_to_sally = "Talk to Sally";
//	final String TAB = "    ";
//
//	// use this to check if the dialogue is complete -- aka there are no more nodes
//	// to run
//	boolean complete = true;
//	// should dump bytecode when complete
//	boolean byte_code_printed = false;
//
//	// TEXTURES
//
//	ObjectMap<String, Texture> ship_textures;
//	ObjectMap<String, Texture> sally_textures;
//
//	Texture ship_face;
//	Texture sally;
//
//	@Override
//	public void create() {
//
//		// load some textures
//		ship_textures = new ObjectMap<String, Texture>();
//		ship_textures.put("happy", new Texture(Gdx.files.internal("happy.png")));
//		ship_textures.put("neutral", new Texture(Gdx.files.internal("neutral.png")));
//
//		ship_face = ship_textures.get("neutral");
//
//		sally_textures = new ObjectMap<String, Texture>();
//		sally_textures.put("default", new Texture(Gdx.files.internal("sally.png")));
//		sally_textures.put("angry", new Texture(Gdx.files.internal("sally_angry.png")));
//		sally_textures.put("talk", new Texture(Gdx.files.internal("sally_talk.png")));
//
//		sally = sally_textures.get("default");
//
//		// enter some variables for our dialogue to use -
//		// we do not need to do this but just to access them before the dialogue
//		// we do for testing purposes
//		data.put(see_ship_var, false);
//		data.put(sally_warning_var, false);
//		data.put(poop, "empty");
//		option_string = new StringBuilder(400);
//
//		font = new BitmapFont(Gdx.files.internal("default.fnt"));
//
//		batch = new SpriteBatch();
//
//		// we first create the dialogue and pass in what data storage we would like for
//		// it to use
//		test_dialogue = new Dialogue(data);
//
//		// we will register a custom function to the library that takes in
//		// one parameter - sally's action sprite
//		test_dialogue.getLibrary().registerFunction("setSallyAction", 1, new Function() {
//			@Override
//			public void invoke(Value... params) {
//				// this function only has one parameter so check like so
//				Value action = params[0];// this parameter will be the name of the action sprite to set sally to
//
//				// possible actions include
//				// angry
//				// talk
//				// and default
//
//				// see if the sprite exists
//				if (sally_textures.containsKey(action.getStringValue())) {
//					sally = sally_textures.get(action.getStringValue());// set it
//				} else {
//					// otherwise set it to default
//					sally = sally_textures.get("default");
//				}
//
//			}
//		});
//
//		// alternatively we could pass in custom loggers
//		// test_dialogue = new Dialogue(data,YarnLogger_debug,YarnLogger_error)
//
//		// load the ship dialogue from file
//		test_dialogue.loadFile(ship_file, show_tokens, show_parse_tree, only_consider);
//
//		// load the sally dialogue from file -- notice that we load it to the same
//		// dialogue. This allows us to retain the same continuity and use the same
//		// libraries -
//		test_dialogue.loadFile(sally_file, show_tokens, show_parse_tree, null);
//
//		// in order to begin updating and receiving results from our dialogue we must
//		// start it
//		// test_dialogue.start();
//		// this will start the dialogue at the default node of Start
//		// we could specify a different node if we wish
//		// test_dialogue.start("nameofnode");
//		// we will handle this in our input method down below
//
//	}
//
//	final String poop ="$poop";
//
//	@Override
//	public void render() {
//
//		// --- ignore
//
//
//		if (screenwidth == 0) {
//			screenwidth = Gdx.graphics.getWidth();
//			screenheight = Gdx.graphics.getHeight();
//		}
//
//		Gdx.gl.glClearColor(0, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		// --- end ignore
//
//		// UPDATE -----------------
//
//		// if we currently dont have any command available check if next result is a
//		// command
//		if (current_command == null && test_dialogue.isNextCommand()) {
//			// assign it
//			current_command = test_dialogue.getNextAsCommand();
//		}
//		// if we dont have a line - check if next result is a line
//		else if (current_line == null && test_dialogue.isNextLine()) {
//			// if there is a command result - execute it before the next line
//			executeCommand();
//			// assign the line
//			current_line = test_dialogue.getNextAsLine();
//
//		}
//		// if we dont have any options check if the next result is options
//		else if (current_options == null && test_dialogue.isNextOptions()) {
//			// assign the options
//			current_options = test_dialogue.getNextAsOptions();
//		}
//		// if the node has not found a complete result - check if next result is a node
//		// complete result
//		else if (node_complete == null && test_dialogue.isNextComplete()) {
//			// assign node complete result
//			node_complete = test_dialogue.getNextAsComplete();
//		} else {
//			// waiting to proccess line or no results available
//
//			// check if the current line is proccessed(null) and that we have a node
//			// complete result
//			if (current_line == null && node_complete != null) {
//				// execute any lingering commands
//				executeCommand();
//				// set complete to true
//				complete = true;
//
//				// lets clean up the results
//				resetAllResults();
//
//				// stop the dialogue
//				test_dialogue.stop();
//			}
//		}
//
//		updateInput();
//
//		// UPDATE END ---------------
//
//		// DRAW --------------------------
//
//		batch.begin();
//		if (!complete) {
//
//			// draw dialogue
//			if (current_line != null) {
//				font.draw(batch, current_line.getText(), screenwidth * .1f, screenheight * .8f);
//
//				if (current_options == null)
//					font.draw(batch, ps, screenwidth * .3f, screenheight * .1f);
//			}
//
//			// draw options
//			if (current_options != null) {
//				int check_limit = Math.min(current_options.getOptions().size, OP_KEYS.length); // we do this to avoid
//																								// array
//																								// index exceptions
//				for (int i = 0; i < check_limit; i++) {
//					String option = current_options.getOptions().get(i);
//					option_string.setLength(0);
//					option_string.append('[').append(i + 1).append(']').append(':').append(' ').append(option);
//					font.draw(batch, option_string, screenwidth * .3f, (screenheight * .5f) - (20 * i));
//				}
//			}
//
//		} else {
//
//			option_string.setLength(0);
//			option_string.append('[').append(1).append(']').append(':').append(talk_to_ship);
//			option_string.append(TAB).append(TAB).append(TAB).append(TAB).append(TAB).append(TAB).append(TAB)
//					.append(TAB);
//			option_string.append('[').append(2).append(']').append(':').append(talk_to_sally);
//			font.draw(batch, option_string, screenwidth * .23f, screenheight * .5f);
//		}
//
//		// draw debug vars ---
//		option_string.setLength(0);
//		option_string.appendLine(vars);
//		option_string.append(see_ship_var).append('=').append(data.getBoolean(see_ship_var)).append('\n');
//		option_string.append(sally_warning_var).append('=').append(data.getBoolean(sally_warning_var)).append('\n');
//		option_string.append(poop).append('=').append(data.getString(poop));
//
//		font.draw(batch, option_string, screenwidth * .3f, screenheight);
//
//		// draw sprites
//		if (sally != null)
//			batch.draw(sally, screenwidth - sally.getWidth() * 2f, screenheight * .4f);
//
//		if (ship_face != null)
//			batch.draw(ship_face, ship_face.getWidth(), screenheight * .4f);
//
//		batch.end();
//
//		// DRAW END -------------------
//	}
//
//	final String setsprite_command = "setsprite";
//	final String shipface_identifier = "ShipFace";
//
//	private void executeCommand() {
//		if (current_command != null) {
//			String params[] = current_command.getCommand().split("\\s+"); // commands are space delimited-- any space
//			for (int i = 0; i < params.length; i++) {
//				params[i] = params[i].trim(); // just trim to make sure
//			}
//
//			// check the first param since it will almost always be the command
//			if (params[0].equals(setsprite_command)) {
//
//				// check if this is not the correct size
//				if (params.length == 3) {
//
//					// check to see if this is the ship face identifier
//					if (params[1].equals(shipface_identifier)) {
//						// check if we have the right texture
//						if (ship_textures.containsKey(params[2])) {
//							// set the ship face to the correct texture
//							ship_face = ship_textures.get(params[2]);
//						} else {
//							// set it to the default state of neutral
//							ship_face = ship_textures.get("neutral");
//						}
//					}
//
//				}
//			}
//
//			current_command = null;
//		}
//	}
//
//	/**
//	 * used to update the input so we can progress the dialogue
//	 */
//	public void updateInput() {
//
//		if (Gdx.input.isKeyJustPressed(Keys.D)) {
//			// dump data
//			System.out.println(data.toJson());
//		}
//
//		if (complete) {
//
//			if (Gdx.input.isKeyJustPressed(OP_KEYS[0])) {
//				// talk to ship
//				test_dialogue.start("Ship");
//				complete = false;
//				resetAllResults();
//			}
//
//			if (Gdx.input.isKeyJustPressed(OP_KEYS[1])) {
//				// talk to sally
//				test_dialogue.start("Sally");
//				complete = false;
//				resetAllResults();
//			}
//
//			return;
//
//		}
//
//		// space goes to next line unless there is options
//		if (current_line != null && current_options == null) {
//			if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
//				clearLine();
//			}
//		}
//
//		// there is options so check all corresponding keys(1-5)
//		if (current_options != null) {
//			// check to see what is less - the amount of options or the size of keys we are
//			// using to accept options
//			int check_limit = Math.min(current_options.getOptions().size, OP_KEYS.length); // we do this to avoid array
//																							// index exceptions
//			for (int i = 0; i < check_limit; i++) {
//				// loop to see if any of the corresponding keys to options is pressed
//				if (Gdx.input.isKeyJustPressed(OP_KEYS[i])) {
//					// if yes then choose
//					current_options.choose(i);
//
//					// then clear options and current line - break out of for loop
//					current_options = null;
//					clearLine();
//					break;
//				}
//			}
//		}
//	}
//
//	private void clearLine() {
//		current_line = null;
//	}
//
//	private void resetAllResults() {
//		current_line = null;
//		current_options = null;
//		node_complete = null;
//		current_command = null;
//	}
//
//	@Override
//	public void dispose() {
//		batch.dispose();
//		font.dispose();
//		for (Entry<String, Texture> t : ship_textures) {
//			t.value.dispose();
//		}
//
//		for (Entry<String, Texture> t : sally_textures)
//			t.value.dispose();
//	}
//}
