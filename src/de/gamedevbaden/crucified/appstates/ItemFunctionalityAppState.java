package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.FlashLight;

/**
 * This state handles the functionality of items the player can interact with.
 * For example: Turning a FlashLight on or off
 * <p>
 * Created by Domenic on 09.06.2017.
 */
public class ItemFunctionalityAppState extends AbstractAppState {

    private EntityData entityData;
    private EntitySet flashLights;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.flashLights = entityData.getEntities(FlashLight.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        flashLights.applyChanges();
    }

    public void toggleFlashLight(EntityId flashLight) {
        if (flashLights.containsId(flashLight)) {
            FlashLight lightComponent = flashLights.getEntity(flashLight).get(FlashLight.class);
            boolean enabled = lightComponent.isEnabled();
            enabled = !enabled; // we switch on / off state
            entityData.setComponent(flashLight, new FlashLight(enabled));
        }
    }

    @Override
    public void cleanup() {
        this.flashLights.release();
        this.flashLights.clear();
        this.flashLights = null;
        super.cleanup();
    }
}
