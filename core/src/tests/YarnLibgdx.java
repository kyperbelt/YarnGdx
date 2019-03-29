package tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.StringBuilder;
import com.kyper.yarn.Dialogue;
import com.kyper.yarn.Dialogue.CommandResult;
import com.kyper.yarn.Dialogue.LineResult;
import com.kyper.yarn.Dialogue.NodeCompleteResult;
import com.kyper.yarn.Dialogue.OptionResult;
import com.kyper.yarn.DialogueStorage;
import com.kyper.yarn.FunctionLibrary.Function;
import com.kyper.yarn.Value;

public class YarnLibgdx extends ApplicationAdapter {

  // some strings we can re-use -- because making STRINGS IS BAD!
  final String ps                 = "PRESS SPACE TO CONTINUE";
  final String finished           = "You finished!";
  final String vars               = "Variables:";
  final String seeShipVar         = "$shouldSeeShip";
  final String sallyWarningVar    = "$sallyWarning";
  final String talkToShip         = "Talk to Ship";
  final String talkToSally        = "Talk to Sally";
  final String TAB                = "    ";
  final String poop               = "$poop";
  final String setspriteCommand   = "setsprite";
  final String shipfaceIdentifier = "ShipFace";
  // what keys to check for input when options are presented - supports up to 5
  // options
  // NOTE: dialogue can support an arbitrary number of options i am just choosing
  // to only support 5 for this example - not that we will even use that many.
  int[]              OP_KEYS         = {Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4, Keys.NUM_5};
  // used to place font
  int                screenwidth;
  int                screenheight;
  // file of ship containing all the ship dialogue
  String             shipFile        = "ship.json";
  // file of sally containing all the sally dialogue
  String             sallyFile       = "sally.json";
  // print all the tokens that are spat out by the parser
  boolean            logTokens       = false;
  // print out tree created by the parser from list of tokens
  boolean            logParseTree    = false;
  // will only load this node - loads entire file if this is null
  String             onlyConsider    = null;
  // Used to store values by dialogue (or your game, access is not limited) - can
  // export/import as json
  DialogueStorage    dialogueStorage = new DialogueStorage("TestData");
  // this is the class that loads and hold the dialogue for now
  Dialogue           testDialogue;
  // our results
  LineResult         currentLine     = null;
  OptionResult       currentOptions  = null;
  CommandResult      currentCommand  = null;
  NodeCompleteResult nodeComplete    = null;
  // test font & spritebatch
  BitmapFont         font;
  SpriteBatch        batch;

  // TEXTURES
  // String builder to avoid string creation-because you know strings in gdx is
  // bad n stuff
  StringBuilder              optionString;
  // use this to check if the dialogue is complete -- aka there are no more nodes
  // to run
  boolean                    complete        = true;
  // should dump bytecode when complete
  boolean                    byteCodePrinted = false;
  ObjectMap<String, Texture> shipTextures;
  ObjectMap<String, Texture> sallyTextures;
  Texture                    shipFace;
  Texture                    sally;

  @Override
  public void create(){

    // load some textures
    shipTextures = new ObjectMap<String, Texture>();
    shipTextures.put("happy", new Texture(Gdx.files.internal("happy.png")));
    shipTextures.put("neutral", new Texture(Gdx.files.internal("neutral.png")));

    shipFace = shipTextures.get("neutral");

    sallyTextures = new ObjectMap<String, Texture>();
    sallyTextures.put("default", new Texture(Gdx.files.internal("sally.png")));
    sallyTextures.put("angry", new Texture(Gdx.files.internal("sally_angry.png")));
    sallyTextures.put("talk", new Texture(Gdx.files.internal("sally_talk.png")));

    sally = sallyTextures.get("default");

    // enter some variables for our dialogue to use -
    // we do not need to do this but just to access them before the dialogue
    // we do for testing purposes
    dialogueStorage.put(seeShipVar, false);
    dialogueStorage.put(sallyWarningVar, false);
    dialogueStorage.put(poop, "empty");
    optionString = new StringBuilder(400);

    font = new BitmapFont(Gdx.files.internal("default.fnt"));

    batch = new SpriteBatch();

    // we first create the dialogue and pass in what dialogueStorage storage we would like for
    // it to use
    testDialogue = new Dialogue(dialogueStorage);

    // we will register a custom function to the functionLibrary that takes in
    // one parameter - sally's action sprite
    testDialogue.getFunctionLibrary().registerFunction("setSallyAction", 1, new Function() {
      @Override
      public void invoke(Value... params){
        // this function only has one parameter so check like so
        Value action = params[0];// this parameter will be the name of the action sprite to set sally to

        // possible actions include
        // angry
        // talk
        // and default

        // see if the sprite exists
        if (sallyTextures.containsKey(action.getStringValue())) {
          sally = sallyTextures.get(action.getStringValue());// set it
        } else {
          // otherwise set it to default
          sally = sallyTextures.get("default");
        }

      }
    });

    // alternatively we could pass in custom loggers
    // testDialogue = new Dialogue(dialogueStorage,YarnLoggerDebug,YarnLoggerError)

    // Set the debug levels of the dialogue.
    testDialogue.setTreeLogging(logParseTree);
    testDialogue.setTokenLogging(logTokens);

    // load the ship dialogue from file
    testDialogue.loadString(Gdx.files.internal(shipFile).readString(), shipFile, onlyConsider);

    // load the sally dialogue from file -- notice that we load it to the same
    // dialogue. This allows us to retain the same storage and use the same
    // libraries -
    testDialogue.loadString(Gdx.files.internal(sallyFile).readString(), sallyFile, null);

    // in order to begin updating and receiving results from our dialogue we must
    // start it
    // testDialogue.start();
    // this will start the dialogue at the default node of Start
    // we could specify a different node if we wish
    // testDialogue.start("nameofnode");
    // we will handle this in our input method down below

  }

  @Override
  public void render(){

    // --- ignore


    if (screenwidth == 0) {
      screenwidth = Gdx.graphics.getWidth();
      screenheight = Gdx.graphics.getHeight();
    }

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    // --- end ignore

    // UPDATE -----------------

    // if we currently dont have any command available check if next result is a
    // command
    if (currentCommand == null && testDialogue.isNextCommand()) {
      // assign it
      currentCommand = testDialogue.getNextAsCommand();
    }
    // if we dont have a line - check if next result is a line
    else if (currentLine == null && testDialogue.isNextLine()) {
      // if there is a command result - execute it before the next line
      executeCommand();
      // assign the line
      currentLine = testDialogue.getNextAsLine();

    }
    // if we dont have any options check if the next result is options
    else if (currentOptions == null && testDialogue.isNextOptions()) {
      // assign the options
      currentOptions = testDialogue.getNextAsOptions();
    }
    // if the node has not found a complete result - check if next result is a node
    // complete result
    else if (nodeComplete == null && testDialogue.isNextComplete()) {
      // assign node complete result
      nodeComplete = testDialogue.getNextAsComplete();
    } else {
      // waiting to proccess line or no results available

      // check if the current line is proccessed(null) and that we have a node
      // complete result
      if (currentLine == null && nodeComplete != null) {
        // execute any lingering commands
        executeCommand();
        // set complete to true
        complete = true;

        // lets clean up the results
        resetAllResults();

        // stop the dialogue
        testDialogue.stop();
      }
    }

    updateInput();

    // UPDATE END ---------------

    // DRAW --------------------------

    batch.begin();
    if (!complete) {

      // draw dialogue
      if (currentLine != null) {
        font.draw(batch, currentLine.getText(), screenwidth * .1f, screenheight * .8f);

        if (currentOptions == null) font.draw(batch, ps, screenwidth * .3f, screenheight * .1f);
      }

      // draw options
      if (currentOptions != null) {
        int checkLimit = Math.min(currentOptions.getOptions().size, OP_KEYS.length); // we do this to avoid
        // array
        // index exceptions
        for (int i = 0; i < checkLimit; i++) {
          String option = currentOptions.getOptions().get(i);
          optionString.setLength(0);
          optionString.append('[').append(i + 1).append(']').append(':').append(' ').append(option);
          font.draw(batch, optionString, screenwidth * .3f, (screenheight * .5f) - (20 * i));
        }
      }

    } else {

      optionString.setLength(0);
      optionString.append('[').append(1).append(']').append(':').append(talkToShip);
      optionString.append(TAB).append(TAB).append(TAB).append(TAB).append(TAB).append(TAB).append(TAB).append(TAB);
      optionString.append('[').append(2).append(']').append(':').append(talkToSally);
      font.draw(batch, optionString, screenwidth * .23f, screenheight * .5f);
    }

    // draw debug vars ---
    optionString.setLength(0);
    optionString.appendLine(vars);
    optionString.append(seeShipVar).append('=').append(dialogueStorage.getBoolean(seeShipVar)).append('\n');
    optionString.append(sallyWarningVar).append('=').append(dialogueStorage.getBoolean(sallyWarningVar)).append('\n');
    optionString.append(poop).append('=').append(dialogueStorage.getString(poop));

    font.draw(batch, optionString, screenwidth * .3f, screenheight);

    // draw sprites
    if (sally != null) batch.draw(sally, screenwidth - sally.getWidth() * 2f, screenheight * .4f);

    if (shipFace != null) batch.draw(shipFace, shipFace.getWidth(), screenheight * .4f);

    batch.end();

    // DRAW END -------------------
  }

  private void executeCommand(){
    if (currentCommand != null) {
      String params[] = currentCommand.getCommand().split("\\s+"); // commands are space delimited-- any space
      for (int i = 0; i < params.length; i++) {
        params[i] = params[i].trim(); // just trim to make sure
      }

      // check the first param since it will almost always be the command
      if (params[0].equals(setspriteCommand)) {

        // check if this is not the correct size
        if (params.length == 3) {

          // check to see if this is the ship face identifier
          if (params[1].equals(shipfaceIdentifier)) {
            // check if we have the right texture
            if (shipTextures.containsKey(params[2])) {
              // set the ship face to the correct texture
              shipFace = shipTextures.get(params[2]);
            } else {
              // set it to the default state of neutral
              shipFace = shipTextures.get("neutral");
            }
          }

        }
      }

      currentCommand = null;
    }
  }

  /**
   * used to update the input so we can progress the dialogue
   */
  public void updateInput(){

    if (Gdx.input.isKeyJustPressed(Keys.D)) {
      // dump dialogueStorage
      System.out.println(dialogueStorage.toJson());
    }

    if (complete) {

      if (Gdx.input.isKeyJustPressed(OP_KEYS[0])) {
        // talk to ship
        testDialogue.start("Ship");
        complete = false;
        resetAllResults();
      }

      if (Gdx.input.isKeyJustPressed(OP_KEYS[1])) {
        // talk to sally
        testDialogue.start("Sally");
        complete = false;
        resetAllResults();
      }

      return;

    }

    // space goes to next line unless there is options
    if (currentLine != null && currentOptions == null) {
      if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
        clearLine();
      }
    }

    // there is options so check all corresponding keys(1-5)
    if (currentOptions != null) {
      // check to see what is less - the amount of options or the size of keys we are
      // using to accept options
      int checkLimit = Math.min(currentOptions.getOptions().size, OP_KEYS.length); // we do this to avoid array
      // index exceptions
      for (int i = 0; i < checkLimit; i++) {
        // loop to see if any of the corresponding keys to options is pressed
        if (Gdx.input.isKeyJustPressed(OP_KEYS[i])) {
          // if yes then choose
          currentOptions.choose(i);

          // then clear options and current line - break out of for loop
          currentOptions = null;
          clearLine();
          break;
        }
      }
    }
  }

  private void clearLine(){
    currentLine = null;
  }

  private void resetAllResults(){
    currentLine = null;
    currentOptions = null;
    nodeComplete = null;
    currentCommand = null;
  }

  @Override
  public void dispose(){
    batch.dispose();
    font.dispose();
    for (Entry<String, Texture> t : shipTextures) {
      t.value.dispose();
    }

    for (Entry<String, Texture> t : sallyTextures)
      t.value.dispose();
  }
}
