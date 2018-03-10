package de.gamedevbaden.crucified.appstates;

import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import de.gamedevbaden.crucified.enums.Scene;

public class ScenePreloader extends AbstractAppState {

    private Node scene;

    public ScenePreloader(AssetManager assetManager) {
        this.scene = (Node) assetManager.loadModel(Scene.FinalIslandScene.getScenePath());
    }

    public Node getScene() {
        return scene;
    }
}
