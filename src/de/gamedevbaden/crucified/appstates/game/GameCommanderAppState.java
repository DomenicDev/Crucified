package de.gamedevbaden.crucified.appstates.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import de.gamedevbaden.crucified.appstates.view.ShadowRendererAppState;
import de.gamedevbaden.crucified.enums.PaperScript;
import de.gamedevbaden.crucified.enums.Scene;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.userdata.EntityType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Created by Domenic on 27.05.2017.
 */
public class GameCommanderAppState extends AbstractAppState implements GameCommander {

    private final Node mainWorldNode = new Node("MainSceneNode");
    private AssetManager assetManager;
    private Node rootNode;
    private Camera cam;
    private SimpleApplication app;
    private ShadowRendererAppState shadowRendererAppState;

    // scripts
    private Properties scripts;

    public GameCommanderAppState() {
    }

    /**
     * For debug reason
     */
    public GameCommanderAppState(SimpleApplication app) {
        this.app = app;
        this.cam = app.getCamera();
        this.assetManager = app.getAssetManager();
        this.rootNode = app.getRootNode();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        this.cam = app.getCamera();
        this.rootNode = ((SimpleApplication) app).getRootNode();
        this.shadowRendererAppState = stateManager.getState(ShadowRendererAppState.class);

        this.rootNode.attachChild(mainWorldNode);

        // load script file
        try {
            FileInputStream stringFileInput = new FileInputStream(new File("assets/Interface/Scripts.xml"));
            this.scripts = new Properties();
            this.scripts.loadFromXML(stringFileInput);
            stringFileInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidPropertiesFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void loadScene(Scene scene) {

        System.out.println("Load Scene: " + scene);

        Node world = (Node) assetManager.loadModel(scene.getScenePath());
        this.mainWorldNode.attachChild(world);
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

        // activate LOD for terrain
        for (Spatial spatial : world.getChildren()) {
            if (spatial instanceof TerrainQuad) {
                TerrainQuad terrainQuad = (TerrainQuad) spatial;
                TerrainLodControl lodControl = terrainQuad.getControl(TerrainLodControl.class);
                lodControl.setCamera(cam);
                lodControl.setEnabled(true);
                break;
            }
        }

        // we need to transfer the lights from the loaded scene
        // to out main scene node otherwise later added
        // spatial might not be affected by that light
        if (world.getLocalLightList().size() > 0) {
            // first we clone the light list from the loaded scene
            Light[] lights = new Light[world.getLocalLightList().size()];
            for (int i = 0; i < world.getLocalLightList().size(); i++) {
                lights[i] = world.getLocalLightList().get(i).clone();
            }
            // we remove the lights from the loaded scene otherwise we would have 2 lights each
            world.getLocalLightList().clear();
            // lastly we add our cloned lights to the mainWorldNode
            for (Light light : lights) {
                mainWorldNode.addLight(light);
            }
        }

        // create shadows
        for (Light light : mainWorldNode.getLocalLightList()) {
            if (light instanceof DirectionalLight) {
                shadowRendererAppState.addShadowRenderer((DirectionalLight) light);
            }
        }


        // init filter if available --> need to be the last thing to add!
        if (scene.getFilterPath() != null) {
            FilterPostProcessor fpp = assetManager.loadFilter(scene.getFilterPath());
            app.getViewPort().addProcessor(fpp);
        }

    }

    @Override
    public void readNote(PaperScript script) {
        if (scripts != null) {
            String text = scripts.getProperty(script.getKey());
            System.out.println(text);
        }
    }

    public Node getMainWorldNode() {
        return mainWorldNode;
    }
}
