package de.gamedevbaden.crucified.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.gamedevbaden.crucified.userdata.EntityType;

/**
 * Created by Domenic on 27.05.2017.
 */
public class GameCommanderAppState extends AbstractAppState implements GameCommander {

    private AssetManager assetManager;
    private Node rootNode;

    private Spatial currentScene;


    public GameCommanderAppState() {
    }

    /**
     * For debug reason
     *
     * @param assetManager
     * @param rootNode
     */
    public GameCommanderAppState(AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        this.rootNode = ((SimpleApplication) app).getRootNode();
        super.initialize(stateManager, app);
    }

    @Override
    public void loadScene(String path) {

        Spatial scene = assetManager.loadModel(path);
        this.rootNode.attachChild(scene);
        // we want all objects (nodes, geometry) with an EntityType user data
        // to be removed because they will
        // be added when receiving the entities for that scene
        scene.depthFirstTraversal(spatial -> {
            for (String userDataKey : spatial.getUserDataKeys()) {
                if (spatial.getUserData(userDataKey) instanceof EntityType) {
                    spatial.removeFromParent();
                }
            }
        });

        this.currentScene = scene;

        // attach scene

    }

    public Spatial getCurrentScene() {
        return currentScene;
    }
}
