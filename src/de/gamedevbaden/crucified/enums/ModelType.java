package de.gamedevbaden.crucified.enums;

/**
 * Created by Domenic on 25.04.2017.
 */
public class ModelType {

    public static final String TestScene = "Models/Plane.j3o";
    public static final String TestTerrain = "Scenes/Terrains/TestTerrain.j3o";
    public static final String TestBox = "Models/TestBox.j3o";
    public static final String Player = "Models/Player/NewTest/Tom.j3o";
    public static final String TriggerableDoor = "Models/TriggerableDoor.j3o";
    public static final String Door = "Models/Door.j3o";
    public static final String TestPickup = "Models/PickableItem.j3o";
    public static final String Headlamp = "Models/Headlamp/Headlamp.j3o";
    public static final String Paper = "Models/Paper/Paper.j3o";
    public static final String Campfire = "Models/Props/Campfire.j3o";
/*
    TestScene("Models/Plane.j3o"),
    TestTerrain("Scenes/Terrains/TestTerrain.j3o"),
    TestBox("Models/TestBox.j3o"),
    Ground("Models/Ground.j3o"),
    Player("Models/Player/Player.j3o"),
    TriggerableDoor("Models/TriggerableDoor.j3o"),
    Door("Models/Door.j3o"),
    TestPickup("Models/PickableItem.j3o");


    private String modelPath;
    private ObjectCategory category;

    ModelType() {
    }

    ModelType(String modelPath) {
        this.modelPath = modelPath;
    }

    ModelType(String modelPath, ObjectCategory category) {
        this.modelPath = modelPath;
        this.category = category;
    }

    public static ModelType getModelType(String modelPath) {
        for (ModelType type : ModelType.values()) {
            if (type.getModelPath().equals(modelPath)) {
                return type;
            }
        }
        return null;
    }

    public ObjectCategory getCategory() {
        return category;
    }

    public String getModelPath() {
        return modelPath;
    }*/
}
