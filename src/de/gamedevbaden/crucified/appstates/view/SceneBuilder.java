package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import de.gamedevbaden.crucified.userdata.EntityType;

import java.util.ArrayList;

/**
 * ToDo: Make this class be used
 * Created by Domenic on 29.05.2017.
 */
public class SceneBuilder extends AbstractAppState {

    private AssetManager assetManager;

    private ArrayList<Node> scenes;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        this.scenes = new ArrayList<>();
        super.initialize(stateManager, app);
    }

    public void loadScene(String pathToScene) {
        Node scene = (Node) assetManager.loadModel(pathToScene);

        removeEntities(scene);
        initAudio(scene);
        //....
        this.scenes.add(scene);
    }

    private void removeEntities(Node scene) {
        scene.depthFirstTraversal(spatial -> {
            for (String userDataKey : spatial.getUserDataKeys()) {
                if (spatial.getUserData(userDataKey) instanceof EntityType) {
                    spatial.removeFromParent();
                }
            }
        });
    }

    private void initAudio(Node scene) {
        // ToDo
    }
}
