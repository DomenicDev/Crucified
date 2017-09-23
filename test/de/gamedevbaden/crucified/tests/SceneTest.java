package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;

public class SceneTest extends SimpleApplication {

    public static void main(String[] args) {
        new SceneTest().start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);
        rootNode.attachChild(assetManager.loadModel("Scenes/NewIslandSetting/IslandNew.j3o"));
    }
}
