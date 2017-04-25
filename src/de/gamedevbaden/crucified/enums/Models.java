package de.gamedevbaden.crucified.enums;

/**
 * Created by Domenic on 25.04.2017.
 */
public enum Models {

    TestScene("Models/Plane.j3o"),
    TestBox("Models/TestBox.j3o"),
    Player("Models/Player/Player.j3o");


    Models(String modelPath) {
        this.modelPath = modelPath;
    }

    private String modelPath;

    public String getModelPath() {
        return modelPath;
    }
}
