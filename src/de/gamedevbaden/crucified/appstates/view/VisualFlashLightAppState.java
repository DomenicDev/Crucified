package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.Light;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.es.components.FlashLight;
import de.gamedevbaden.crucified.es.components.Model;

import java.util.HashMap;

/**
 * This state watches for "FlashLight" entities and creates a SpotLight for each flash light.
 * <p>
 * Created by Domenic on 09.06.2017.
 */
public class VisualFlashLightAppState extends AbstractAppState {

    private EntitySet flashLights;
    private HashMap<EntityId, SpotLight> lights = new HashMap<>();
    private ModelViewAppState modelViewAppState;
    private Node rootNode;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);
        this.rootNode = ((SimpleApplication) app).getRootNode();
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.flashLights = entityData.getEntities(Model.class, FlashLight.class);
        for (Entity entity : flashLights) {
            addFlashLight(entity);
        }
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        // refresh logic states
        if (flashLights.applyChanges()) {

            for (Entity entity : flashLights.getAddedEntities()) {
                addFlashLight(entity);
            }

            for (Entity entity : flashLights.getChangedEntities()) {
                updateFlashLight(entity);
            }

            for (Entity entity : flashLights.getRemovedEntities()) {
                removeFlashLight(entity);
            }

        }
    }

    @Override
    public void render(RenderManager rm) {
        // update visual spot lights
        for (Entity entityId : flashLights) {
            updateLight(entityId.getId());
        }
    }

    private void addFlashLight(Entity entity) {
        // ToDo: Adjust spot light parameters for this game.
        FlashLight light = entity.get(FlashLight.class);
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
        spot.setEnabled(light.isEnabled());
        rootNode.addLight(spot);
        lights.put(entity.getId(), spot);
    }

    private void updateFlashLight(Entity entity) {
        FlashLight light = entity.get(FlashLight.class);
        SpotLight spot = lights.get(entity.getId());
        spot.setEnabled(light.isEnabled());
    }

    private void removeFlashLight(Entity entity) {
        SpotLight light = lights.remove(entity.getId());
        rootNode.removeLight(light);
    }

    /**
     * This methods updates the visual spot light.
     * @param entity the flash light id
     */
    private void updateLight(EntityId entity) {
        Spatial flashLightModel = modelViewAppState.getSpatial(entity);
        SpotLight light = lights.get(entity);
        if (flashLightModel != null && light != null) {
            light.setPosition(flashLightModel.getWorldTranslation());
            light.setDirection(flashLightModel.getWorldRotation().getRotationColumn(2, light.getDirection()));
        }
    }

    @Override
    public void cleanup() {
        for (Light light : lights.values()) {
            rootNode.removeLight(light);
        }

        this.lights.clear();
        this.lights = null;

        this.flashLights.release();
        this.flashLights.clear();
        this.flashLights = null;

        super.cleanup();
    }
}
