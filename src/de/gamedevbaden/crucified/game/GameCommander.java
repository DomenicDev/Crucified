package de.gamedevbaden.crucified.game;

/**
 * This interface is used when the game manager wants the client to do certain things, such as loading a scene.
 * <p>
 * Created by Domenic on 27.05.2017.
 */
public interface GameCommander {

    /**
     * The game requires to load the supplied scene
     *
     * @param path the path to the scene
     */
    void loadScene(String path);
}
