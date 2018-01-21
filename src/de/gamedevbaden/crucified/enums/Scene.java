package de.gamedevbaden.crucified.enums;

import com.jme3.math.Vector3f;

/**
 * This enum contains all scenes with their filters for the game.
 * Created by Domenic on 04.06.2017.
 */
public enum Scene {

    TestScene("Scenes/TestScene.j3o", null),
    BeachScene("Scenes/BeachScene.j3o", "Scenes/BeachSceneFilter.j3f"),
    FinalIslandScene("Scenes/NewIslandSetting/IslandNew.j3o", "Scenes/NewIslandSetting/IslandFilterNew.j3f", new Vector3f(44.062027f, 2.62f, 152.44156f)),
    GameLogicTestScene("Scenes/TestGameLogicScene.j3o", null);

    private String scenePath;
    private String filterPath;
    private Vector3f startPosition;

    Scene(String scenePath, String filterPath) {
        this.scenePath = scenePath;
        this.filterPath = filterPath;
    }

    Scene(String scenePath, String filterPath, Vector3f startPosition) {
        this(scenePath, filterPath);
        this.startPosition = startPosition;
    }

    public String getScenePath() {
        return scenePath;
    }

    public String getFilterPath() {
        return filterPath;
    }

    public Vector3f getStartPosition() {
        return startPosition;
    }
}
