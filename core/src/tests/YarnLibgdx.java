package tests;

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
	int[] OP_KEYS = { Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5 };

	// used to place font
	int screenwidth;
	int screenheight;

	// Example file we will be using taken straight from the yarnspinner tests :
	// https://github.com/thesecretlab/YarnSpinner/tree/master/Tests
	String example_file = "example.json";

	// print all the tokens that are spat out by the parser
	boolean show_tokens = false;

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

	// use this to check if the dialogue is complete -- aka there are no more nodes
	// to run
	boolean complete = false;

	@Override
	public void create() {

		option_string = new StringBuilder(400);

		font = new BitmapFont(Gdx.files.internal("default.fnt"));

		batch = new SpriteBatch();

		// we first create the dialogue and pass in what data storage we would like for
		// it to use
		test_dialogue = new Dialogue(data);

		// alternatively we could pass in custom loggers
		// test_dialogue = new Dialogue(data,YarnLogger_debug,YarnLogger_error)

		// load the dialogue from file
		test_dialogue.loadFile(example_file, show_tokens, show_parse_tree, only_consider);

		// in order to begin updating and receiving results from our dialogue we must
		// start it
		test_dialogue.start(); // this will start the dialogue at the default node of Start
		// we could specify a different node if we wish
		// test_dialogue.start("nameofnode");

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
				if(node_complete.next_node == null)
					complete = true;
			}
		}

		updateInput();

		// DRAW --------------------------

		batch.begin();
		if (!complete) {
			if (current_line != null) {
				font.draw(batch, current_line.getText(), screenwidth * .1f, screenheight * .8f);

				if (current_options == null)
					font.draw(batch, ps, screenwidth * .3f, screenheight * .1f);
			}

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

		}else {
			font.draw(batch, finished, screenwidth*.4f, screenheight*.5f);
		}

		batch.end();
	}

	/**
	 * used to update the input so we can progress the dialogue
	 */
	public void updateInput() {

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

		font.dispose();
	}
}
