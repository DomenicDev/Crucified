package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

import java.util.ArrayList;
import java.util.List;

/**
 * This state provides method to generate shadows for a light source
 * Created by Domenic on 05.06.2017.
 */
public class ShadowRendererAppState extends AbstractAppState {

    private static final int SHADOW_MAP_SIZE = 2048;
    private static final int NUMBER_MAPS = 4;
    private static final EdgeFilteringMode FILTERING_MODE = EdgeFilteringMode.PCF8;

    private AssetManager assetManager;
    private ViewPort viewPort;

    private List<AbstractShadowRenderer> shadowRenderer = new ArrayList<>();

    public ShadowRendererAppState(AssetManager assetManager, ViewPort viewPort) {
        this.assetManager = assetManager;
        this.viewPort = viewPort;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        this.viewPort = app.getViewPort();
        super.initialize(stateManager, app);
    }

    public void addShadowRenderer(DirectionalLight light) {
        DirectionalLightShadowRenderer renderer = new DirectionalLightShadowRenderer(assetManager, SHADOW_MAP_SIZE, NUMBER_MAPS);
        renderer.setLight(light);
        renderer.setShadowIntensity(0.5f);
        renderer.setEdgeFilteringMode(FILTERING_MODE);
        viewPort.addProcessor(renderer);
    }


    @Override
    public void cleanup() {
        for (AbstractShadowRenderer shadowRenderer : shadowRenderer) {
            viewPort.removeProcessor(shadowRenderer);
        }
        shadowRenderer.clear();
        shadowRenderer = null;

        super.cleanup();
    }
}
