package de.gamedevbaden.crucified.enums;

/**
 * Created by Domenic on 25.04.2017.
 */
public enum ModelType {

    TestScene("Models/Plane.j3o"),
    TestTerrain("Scenes/Terrains/TestTerrain.j3o"),
    TestBox("Models/TestBox.j3o"),
    Ground("Models/Ground.j3o"),
    Player("Models/Player/Player.j3o");


    private String modelPath;

    ModelType(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getModelPath() {
        return modelPath;
    }
}
