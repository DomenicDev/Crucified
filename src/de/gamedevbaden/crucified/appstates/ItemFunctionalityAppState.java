package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.listeners.EquipmentListener;
import de.gamedevbaden.crucified.appstates.listeners.ItemStorageListener;
import de.gamedevbaden.crucified.es.components.FlashLight;

/**
 * This state handles the functionality of items the player can interact with.
 * For example: Turning a FlashLight on or off
 * <p>
 * Created by Domenic on 09.06.2017.
 */
public class ItemFunctionalityAppState extends AbstractAppState implements ItemStorageListener, EquipmentListener {

    private EntityData entityData;
    private EntitySet flashLights;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.flashLights = entityData.getEntities(FlashLight.class);

        // add listeners
        stateManager.getState(ItemStoreAppState.class).addListener(this);
        stateManager.getState(EquipmentAppState.class).addListener(this);

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

    private void setNewStateForFlashLight(EntityId flashLightId, boolean enabled) {
        this.flashLights.applyChanges();
        if (enabled != flashLights.getEntity(flashLightId).get(FlashLight.class).isEnabled()) {
            entityData.setComponent(flashLightId, new FlashLight(enabled));
        }
    }

    @Override
    public void onItemStored(EntityId storedItem) {
        // if a flashlight has been stored then
        // we want it to be turned off
        if (flashLights.containsId(storedItem)) {
            setNewStateForFlashLight(storedItem, false);
        }
    }

    @Override
    public void onItemDropped(EntityId droppedItem) {
        // when a flashlight is dropped we want it to be turned off
        if (flashLights.containsId(droppedItem)) {
            setNewStateForFlashLight(droppedItem, false);
        }
    }

    @Override
    public void onItemEquipped(EntityId equippedItem) {
        if (flashLights.containsId(equippedItem)) {
            setNewStateForFlashLight(equippedItem, true);
        }
    }

    @Override
    public void onItemUnequipped(EntityId unequippedItem) {
        if (flashLights.containsId(unequippedItem)) {
            setNewStateForFlashLight(unequippedItem, false);
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
