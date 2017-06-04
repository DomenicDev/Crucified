package de.gamedevbaden.crucified.enums;

/**
 * This enum contains all scenes with their filters for the game.
 * Created by Domenic on 04.06.2017.
 */
public enum Scene {

    TestScene("Scenes/TestScene.j3o", null),
    BeachScene("Scenes/IslandVersion1.j3o", "Scenes/IslandFilter.j3f");

    private String scenePath;
    private String filterPath;

    Scene(String scenePath, String filterPath) {
        this.scenePath = scenePath;
        this.filterPath = filterPath;
    }

    public String getScenePath() {
        return scenePath;
    }

    public String getFilterPath() {
        return filterPath;
    }
}
