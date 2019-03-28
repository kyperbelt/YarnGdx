package com.kyper.yarn;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Allows loading of dialogues via AssetManager. Note that the dialogue instance is kept the same through loading calls.
 * @see AssetManager
 */
public class DialogueAssetLoader extends AsynchronousAssetLoader<Dialogue, DialogueAssetLoader.DialogParameter> {

    /** The dialogue instance. */
    private Dialogue dialogue;

    private DialogParameter defaultParam = new DialogParameter();

    /**
     * Constructor, sets the {@link FileHandleResolver} to use to resolve the file associated with the asset name.
     *
     * @param resolver how to resolve the file.
     */
    public DialogueAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    /**
     * Synchronously loads a dialogue.
     * @param manager The asset manager context.
     * @param fileName The file-name of the yarn file.
     * @param file The yarn file.
     * @param parameter Additional parameters.
     * @return A Dialogue instance.
     */
    @Override
    public Dialogue loadSync(AssetManager manager, String fileName, FileHandle file, DialogParameter parameter) {
        if(parameter == null){
            parameter = defaultParam;
        }
        if(dialogue == null){
            dialogue = new Dialogue(parameter.continuity, parameter.debugLogger, parameter.errorLogger);
        }
        dialogue.loadString(resolve(fileName).readString(), fileName, parameter.showTokens, parameter.showParseTree, parameter.onlyConsider);
        return dialogue;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, DialogParameter parameter) {
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, DialogueAssetLoader.DialogParameter parameter) {
        return null;
    }

    /**
     * Parameters to be passed onto the loading of a Dialogue.
     * @see Dialogue
     */
    static public class DialogParameter extends AssetLoaderParameters<Dialogue> {
        /** The logger to log debug logs with. The default implementation does nothing. */
        public Dialogue.YarnLogger debugLogger = new Dialogue.YarnLogger() {
            @Override
            public void log(String message) {
            }
        };
        /** The logger to log errors with. The default implementation does nothing. */
        public Dialogue.YarnLogger      errorLogger   = new Dialogue.YarnLogger() {
            @Override
            public void log(String message) {
            }
        };
        public Dialogue.VariableStorage continuity;
        public boolean                  showTokens    = false;
        public boolean                  showParseTree = false;
        public String                   onlyConsider  = null;
    }

}
