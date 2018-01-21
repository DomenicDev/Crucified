package de.gamedevbaden.crucified.game;

import de.gamedevbaden.crucified.enums.GameDecisionType;
import de.gamedevbaden.crucified.enums.PaperScript;
import de.gamedevbaden.crucified.enums.Scene;

/**
 * This interface is used when the game manager wants the client to do certain things, such as loading a scene.
 * <p>
 * Created by Domenic on 27.05.2017.
 */
public interface GameCommander {

    /**
     * The game requires to load the supplied scene.
     * @param scene the path to the scene
     */
    void loadScene(Scene scene);

    /**
     * Tells the player to open up a special screen (gui) to show the specified text.
     * @param script the script to read
     */
    void readNote(PaperScript script);

    /**
     * This is called when the game is finished.
     *
     * @param decisionType tells who has won.
     */
    void onGameDecided(GameDecisionType decisionType);
}
