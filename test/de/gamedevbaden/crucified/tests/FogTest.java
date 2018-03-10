package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Spatial;
import com.jme3.water.WaterFilter;
import de.gamedevbaden.crucified.enums.Scene;

public class FogTest extends SimpleApplication {

    public static void main(String[] args) {
        new FogTest().start();
    }

    @Override
    public void simpleInitApp() {

        flyCam.setMoveSpeed(30);

        cam.setFrustumPerspective(45f, (float) getContext().getSettings().getWidth() / getContext().getSettings().getHeight(), 0.05f, 300);

        Spatial scene = assetManager.loadModel(Scene.FinalIslandScene.getScenePath());
        rootNode.attachChild(scene);


        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        FogFilter fog = new FogFilter(ColorRGBA.Black, 1f, 500);
        fpp.addFilter(fog);

        viewPort.addProcessor(fpp);

        FilterPostProcessor fpp2 = assetManager.loadFilter(Scene.FinalIslandScene.getFilterPath());
        getViewPort().addProcessor(fpp2);

        WaterFilter waterFilter = fpp2.getFilter(WaterFilter.class);
        System.out.println(waterFilter.getWaterHeight() + " " + waterFilter.getSpeed() + " " + waterFilter.getMaxAmplitude());

    }
}
