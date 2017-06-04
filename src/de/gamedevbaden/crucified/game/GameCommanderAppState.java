package de.gamedevbaden.crucified.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.gamedevbaden.crucified.enums.Scene;
import de.gamedevbaden.crucified.userdata.EntityType;

/**
 * Created by Domenic on 27.05.2017.
 */
public class GameCommanderAppState extends AbstractAppState implements GameCommander {

    private AssetManager assetManager;
    private Node rootNode;

    private SimpleApplication app;

    private Spatial currentScene;


    public GameCommanderAppState() {
    }

    /**
     * For debug reason
     */
    public GameCommanderAppState(SimpleApplication app) {
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.rootNode = app.getRootNode();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        this.rootNode = ((SimpleApplication) app).getRootNode();


        super.initialize(stateManager, app);
    }

    @Override
    public void loadScene(Scene scene) {

        Spatial world = assetManager.loadModel(scene.getScenePath());
        this.rootNode.attachChild(world);
        // we want all objects (nodes, geometry) with an EntityType user data
        // to be removed because they will
        // be added when receiving the entities for that scene
        world.depthFirstTraversal(spatial -> {
            for (String userDataKey : spatial.getUserDataKeys()) {
                if (spatial.getUserData(userDataKey) instanceof EntityType) {
                    spatial.removeFromParent();
                }
            }
        });

        // ToDo: Activate LOD for terrain (set cam)

        // init filter if available
//        if (scene.getFilterPath() != null) {
//            FilterPostProcessor fpp = assetManager.loadFilter(scene.getFilterPath());
//            app.getViewPort().addProcessor(fpp);
//        }

        this.currentScene = world;

        // attach scene

    }

    public Spatial getCurrentScene() {
        return currentScene;
    }
}
