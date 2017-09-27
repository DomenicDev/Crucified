package de.gamedevbaden.crucified.tests;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.DirectionalLight;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainQuad;
import de.gamedevbaden.crucified.appstates.view.TerrainGrassGeneratorAppState;
import jme3tools.optimize.GeometryBatchFactory;

public class SceneTest extends SimpleApplication {

    private Node scene;

    public static void main(String[] args) {
        new SceneTest().start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);

        this.scene = (Node) assetManager.loadModel("Scenes/NewIslandSetting/IslandNew.j3o");
        rootNode.attachChild(scene);

        DirectionalLight light = (DirectionalLight) scene.getLocalLightList().get(1);
        DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(assetManager, 2048, 2);
        shadowRenderer.setLight(light);
        viewPort.addProcessor(shadowRenderer);

        stateManager.attach(new TerrainGrassGeneratorAppState());

        FilterPostProcessor fpp = assetManager.loadFilter("Scenes/NewIslandSetting/IslandFilterNew.j3f");
        viewPort.addProcessor(fpp);

        rootNode.attachChild(assetManager.loadModel("Models/Skies/LagoonSky.j3o"));

        stateManager.attach(new Initer());
    }


    private class Initer extends AbstractAppState {

        @Override
        public void initialize(AppStateManager stateManager, Application app) {

            Node grass = stateManager.getState(TerrainGrassGeneratorAppState.class).createGrassForTerrain((TerrainQuad) scene.getChild(0), 1);
            GeometryBatchFactory.optimize(grass);
            scene.attachChild(grass);

        }
    }
}
