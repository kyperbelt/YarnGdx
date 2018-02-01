package tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.StringBuilder;
import com.kyper.yarn.Dialogue;
import com.kyper.yarn.Dialogue.CommandResult;
import com.kyper.yarn.Dialogue.LineResult;
import com.kyper.yarn.Dialogue.NodeCompleteResult;
import com.kyper.yarn.Dialogue.OptionResult;
import com.kyper.yarn.UserData;

public class YarnLibgdx extends ApplicationAdapter {

	// what keys to check for input when options are presented - supports up to 5
	// options
	// NOTE: dialogue can support an arbitrary number of options i am just choosing
	// to only support 5 for this example - not that we will even use that many.
	int[] OP_KEYS = { Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5 };

	// used to place font
	int screenwidth;
	int screenheight;

	// file of ship containing all the ship dialogue
	String ship_file = "ship.json";

	// file of sally containing all the sally dialogue
	String sally_file = "sally.json";

	// print all the tokens that are spat out by the parser
	boolean show_tokens = true;

	// print out tree created by the parser from list of tokens
	boolean show_parse_tree = true;

	// will only load this node - loads entire file if this is null
	String only_consider = null;

	// Used to store values by dialogue (or your game, access is not limited) - can
	// export/import as json
	UserData data = new UserData("Test_data");

	// this is the class that loads and hold the dialogue for now
	Dialogue test_dialogue;

	// our results
	LineResult current_line = null;
	OptionResult current_options = null;
	CommandResult current_command = null;
	NodeCompleteResult node_complete = null;

	// test font & spritebatch
	BitmapFont font;
	SpriteBatch batch;

	// String builder to avoid string creation-because you know strings in gdx is
	// bad n stuff
	StringBuilder option_string;

	final String ps = "PRESS SPACE TO CONTINUE";
	final String finished = "You finished!";
	final String vars = "Variables:";
	final String see_ship_var = "should_see_ship";
	final String sally_warning_var = "sally_warning";
	final String talk_to_ship = "Talk to Ship";
	final String talk_to_sally = "Talk to Sally";

	// use this to check if the dialogue is complete -- aka there are no more nodes
	// to run
	boolean complete = false;
	// should dump bytecode when complete
	boolean byte_code_printed = true;

	@Override
	public void create() {
		
		
		// enter some variables for our dialogue to use -
		// we do not need to do this but just to access them before the dialogue
		// we do for testing purposes
		data.put(see_ship_var, false);
		data.put(sally_warning_var, false);
		option_string = new StringBuilder(400);

		font = new BitmapFont(Gdx.files.internal("default.fnt"));

		batch = new SpriteBatch();

		// we first create the dialogue and pass in what data storage we would like for
		// it to use
		test_dialogue = new Dialogue(data);

		// alternatively we could pass in custom loggers
		// test_dialogue = new Dialogue(data,YarnLogger_debug,YarnLogger_error)

		// load the ship dialogue from file
		test_dialogue.loadFile(ship_file, show_tokens, show_parse_tree, only_consider);

		// load the sally dialogue from file -- notice that we load it to the same
		// dialogue. This allows us to retain the same continuity and use the same
		// libraries -
		test_dialogue.loadFile(sally_file, show_tokens, show_parse_tree, null);

		// in order to begin updating and receiving results from our dialogue we must
		// start it
		// test_dialogue.start();
		// this will start the dialogue at the default node of Start
		// we could specify a different node if we wish
		// test_dialogue.start("nameofnode");
		//we will handle this in our input method down below

	}

	@Override
	public void render() {

		// --- ignore
		if (screenwidth == 0) {
			screenwidth = Gdx.graphics.getWidth();
			screenheight = Gdx.graphics.getHeight();
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// --- end ignore

		// UPDATE -----------------

		// we must update the dialogue to step it to the next line, set of options or
		// command
		test_dialogue.update();

		// check to see if we have any results
		if (test_dialogue.hasNext()) {
			// if the next result is a line and the current line is empty
			if (test_dialogue.isLine() && current_line == null) {
				// pop the next result off the dialogue stack and set it as the current line
				current_line = test_dialogue.getNext(LineResult.class); // uses generics to get the line - could also
																		// use test_dialogue.getLine() -- Checking class
																		// type is left to you as the developer

			}

			// if the next result is a set of options
			if (test_dialogue.isOptions() && current_options == null) {
				current_options = test_dialogue.getOptions();
			}

			// ---------command still needs testing

			// -------------

			// check if the node is complete and there is no line currently active
			if (test_dialogue.isNodeComplete() && current_line == null) {
				node_complete = test_dialogue.getNodeComplete();
				if (node_complete.next_node == null) {
					complete = true;
					if (byte_code_printed)
						Gdx.app.log("::ByteCode:", "\n" + test_dialogue.getByteCode());
				}
			}
		}

		updateInput();

		// DRAW --------------------------

		batch.begin();
		if (!complete) {

			// draw dialogue
			if (current_line != null) {
				font.draw(batch, current_line.getText(), screenwidth * .1f, screenheight * .8f);

				if (current_options == null)
					font.draw(batch, ps, screenwidth * .3f, screenheight * .1f);
			}

			// draw options
			if (current_options != null) {
				int check_limit = Math.min(current_options.getOptions().size, OP_KEYS.length); // we do this to avoid
																								// array
																								// index exceptions
				for (int i = 0; i < check_limit; i++) {
					String option = current_options.getOptions().get(i);
					option_string.setLength(0);
					option_string.append('[').append(i + 1).append(']').append(':').append(' ').append(option);
					font.draw(batch, option_string, screenwidth * .3f, (screenheight * .5f) - (20 * i));
				}
			}

		} else {
			font.draw(batch, finished, screenwidth * .4f, screenheight * .5f);
		}
		
		//draw debug vars ---
		option_string.setLength(0);
		option_string.appendLine(vars);
		option_string.append(see_ship_var).append('=').append(data.getBoolean(see_ship_var)).append('\n');
		option_string.append(sally_warning_var).append('=').append(data.getBoolean(sally_warning_var));
		
		font.draw(batch, option_string, screenwidth*.3f, screenheight);

		batch.end();
	}

	/**
	 * used to update the input so we can progress the dialogue
	 */
	public void updateInput() {
		
		if(!test_dialogue.isRunning()) {
			
			if(Gdx.input.isKeyJustPressed(OP_KEYS[0])) {
				//talk to ship
				test_dialogue.start("Ship");
			}
			
			if(Gdx.input.isKeyJustPressed(OP_KEYS[1])) {
				//talk to sally
				test_dialogue.start("Sally");
			}
			
			return;
			
		}

		// space goes to next line unless there is options
		if (current_line != null && current_options == null) {
			if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
				current_line = null;
			}
		}

		// there is options so check all corresponding keys(1-5)
		if (current_options != null) {
			// check to see what is less - the amount of options or the size of keys we are
			// using to accept options
			int check_limit = Math.min(current_options.getOptions().size, OP_KEYS.length); // we do this to avoid array
																							// index exceptions
			for (int i = 0; i < check_limit; i++) {
				// loop to see if any of the corresponding keys to options is pressed
				if (Gdx.input.isKeyJustPressed(OP_KEYS[i])) {
					// if yes then choose
					current_options.choose(i);

					// then clear options and current line - break out of for loop
					current_options = null;
					current_line = null;
					break;
				}
			}
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
