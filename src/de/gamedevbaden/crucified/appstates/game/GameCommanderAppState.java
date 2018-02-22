package de.gamedevbaden.crucified.appstates.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.TechniqueDef;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import de.gamedevbaden.crucified.appstates.gui.HudAppState;
import de.gamedevbaden.crucified.appstates.net.PredictionAppState;
import de.gamedevbaden.crucified.appstates.paging.GameWorldPagingManager;
import de.gamedevbaden.crucified.appstates.paging.WorldChunk;
import de.gamedevbaden.crucified.appstates.view.ShadowRendererAppState;
import de.gamedevbaden.crucified.appstates.view.TerrainGrassGeneratorAppState;
import de.gamedevbaden.crucified.enums.GameDecisionType;
import de.gamedevbaden.crucified.enums.PaperScript;
import de.gamedevbaden.crucified.enums.Scene;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.userdata.EntityType;
import de.gamedevbaden.crucified.userdata.PagingOptionsUserData;
import de.gamedevbaden.crucified.utils.GameConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * This class implements the {@link GameCommander} interface.
 * Created by Domenic on 27.05.2017.
 */
public class GameCommanderAppState extends AbstractAppState implements GameCommander {

    private final Node mainWorldNode = new Node("MainSceneNode");
    private AssetManager assetManager;
    private Node rootNode;
    private Camera cam;
    private SimpleApplication app;
    private HudAppState hudAppState;
    private GameWorldPagingManager pagingManager;
    private PredictionAppState predictionAppState;
    private AppStateManager stateManager;

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
        this.app = (SimpleApplication) app;
        this.assetManager = app.getAssetManager();
        this.cam = app.getCamera();
        this.stateManager = stateManager;
        this.rootNode = ((SimpleApplication) app).getRootNode();
        this.hudAppState = stateManager.getState(HudAppState.class);
        this.rootNode.attachChild(mainWorldNode);
        this.pagingManager = stateManager.getState(GameWorldPagingManager.class);
        this.predictionAppState = stateManager.getState(PredictionAppState.class);

        app.getRenderManager().setPreferredLightMode(TechniqueDef.LightMode.SinglePassAndImageBased);
        app.getRenderManager().setSinglePassLightBatchSize(3);

        // load script file
        try {
            FileInputStream stringFileInput = new FileInputStream(new File("assets/Interface/Scripts.xml"));
            this.scripts = new Properties();
            this.scripts.loadFromXML(stringFileInput);
            stringFileInput.close();
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
                stateManager.getState(ShadowRendererAppState.class).addShadowRenderer((DirectionalLight) light);
            }
        }

        // add grass to terrain
        for (Spatial spatial : world.getChildren()) {
            if (false && spatial instanceof TerrainQuad) {
                if (spatial.getUserData(GameConstants.USER_DATA_GRASS_TEXTURE_INDEX) != null) {
                    int grassTextureIndex = spatial.getUserData(GameConstants.USER_DATA_GRASS_TEXTURE_INDEX);
                    TerrainQuad terrain = (TerrainQuad) spatial;
                    Node grassNode = stateManager.getState(TerrainGrassGeneratorAppState.class).createGrassForTerrain(terrain, grassTextureIndex);
                    grassNode.setCullHint(Spatial.CullHint.Always);

                    System.out.println(grassNode.getChildren().size());
                    // we add paging options to the grass node, so it will
                    // be handled by the paging system
                    PagingOptionsUserData pagingOptions = new PagingOptionsUserData();
                    pagingOptions.setUseBatching(true);
                    grassNode.setUserData(GameConstants.USER_DATA_PAGING_OPTIONS, pagingOptions);
                    world.attachChild(grassNode);
                    break;
                }
            }
        }

        // init filter if available --> need to be the last thing to add!
        if (scene.getFilterPath() != null) {
            FilterPostProcessor fpp = assetManager.loadFilter(scene.getFilterPath());
            app.getViewPort().addProcessor(fpp);
        }

        // we need to add local physics if we run a client
        if (stateManager.getState(PredictionAppState.class) != null) {
            stateManager.getState(PredictionAppState.class).initStaticPhysicalObjects(world);
        }

        // create chunks for game world
        List<WorldChunk> chunks = stateManager.getState(GameWorldPagingManager.class).createChunksForGameWorld(world, 7, assetManager);
        stateManager.getState(GameWorldPagingManager.class).setChunks(chunks);


        // play predefined audio nodes
        world.depthFirstTraversal(spatial -> {
            if (spatial instanceof AudioNode) {
                ((AudioNode) spatial).setVolume(0.5f);
                ((AudioNode) spatial).play();
            }
        });

    }

    @Override
    public void readNote(PaperScript script) {
        if (scripts != null) {
            String text = scripts.getProperty(script.getKey());
            System.out.println(text);

            hudAppState.showPaper(text);
        }
    }

    @Override
    public void onGameDecided(GameDecisionType decisionType) {
        // Todo
        System.out.println(decisionType);
    }

    public Node getMainWorldNode() {
        return mainWorldNode;
    }
}
