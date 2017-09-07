package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.es.components.FireState;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;
import java.util.Map;

public class FireLightAppState extends AbstractAppState {

    private Map<EntityId, PointLight> lightMap = new HashMap<>();
    private EntitySet fireEntities;
    private Node gameNode;
    private LightingDistanceAppState lightingDistanceAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.lightingDistanceAppState = stateManager.getState(LightingDistanceAppState.class);
        this.gameNode = stateManager.getState(GameCommanderAppState.class).getMainWorldNode();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.fireEntities = entityData.getEntities(FireState.class, Transform.class);
        for (Entity entity : fireEntities) {
            addFireLight(entity);
        }
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (fireEntities.applyChanges()) {

            for (Entity entity : fireEntities.getAddedEntities()) {
                addFireLight(entity);
            }

            for (Entity entity : fireEntities.getChangedEntities()) {
                updateFireLight(entity);
            }

            for (Entity entity : fireEntities.getRemovedEntities()) {
                removeFireLight(entity);
            }

        }

    }

    private void addFireLight(Entity entity) {
        // create actual light
        PointLight fireLight = new PointLight();
        fireLight.setColor(ColorRGBA.Orange);
        fireLight.setRadius(15f);
        fireLight.setPosition(entity.get(Transform.class).getTranslation().add(0, 0.3f, 0));
        fireLight.setEnabled(entity.get(FireState.class).isOn());

        // add light to scene
        this.gameNode.addLight(fireLight);

        // we want this light to be controlled, so we add to the LightningDistanceAppState
        this.lightingDistanceAppState.addPointLight(fireLight, 80);

        // add light to global list
        this.lightMap.put(entity.getId(), fireLight);
    }

    private void updateFireLight(Entity entity) {
        PointLight fireLight = this.lightMap.get(entity.getId());
        FireState state = entity.get(FireState.class);
        fireLight.setEnabled(state.isOn());
        // we also need to inform the LightingDistanceAppState about the change
        this.lightingDistanceAppState.updateEnabledState(fireLight, state.isOn());
    }

    private void removeFireLight(Entity entity) {
        PointLight fireLight = this.lightMap.remove(entity.getId());
        this.gameNode.removeLight(fireLight);
        // we also inform the LightingDistanceAppState about the remove
        this.lightingDistanceAppState.unwatchLight(fireLight);
    }

    @Override
    public void cleanup() {
        for (Entity entity : fireEntities) {
            removeFireLight(entity);
        }
        this.fireEntities.release();
        this.fireEntities.clear();
        this.fireEntities = null;
        this.lightMap.clear();
        super.cleanup();
    }
}
