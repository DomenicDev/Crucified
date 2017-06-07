package de.gamedevbaden.crucified.appstates.game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.game.GameEventListener;

/**
 * The {@link GameEventListener} listens for game related events and informs the relevant app states about
 * the event so they can handle those events. This class is kind of the interface between the game events
 * and the game logic systems.
 *
 * Created by Domenic on 03.05.2017.
 */
public class GameEventHandler extends AbstractAppState implements GameEventListener {

    private PlayerInputControlAppState playerInputControlAppState;
    private PhysicsPlayerMovementAppState physicsPlayerMovementAppState;
    private TriggerAppState triggerAppState;
    private InteractionAppState interactionAppState;
    private ItemStoreAppState itemStoreAppState;
    private EquipmentAppState equipmentAppState;
    private GameSessionManager gameSession;

    public GameEventHandler(GameSessionManager gameSession) {
        this.gameSession = gameSession;
        this.gameSession.addGameEventListener(this);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.playerInputControlAppState = stateManager.getState(PlayerInputControlAppState.class);
        this.physicsPlayerMovementAppState = stateManager.getState(PhysicsPlayerMovementAppState.class);
        this.triggerAppState = stateManager.getState(TriggerAppState.class);
        this.interactionAppState = stateManager.getState(InteractionAppState.class);
        this.itemStoreAppState = stateManager.getState(ItemStoreAppState.class);
        this.equipmentAppState = stateManager.getState(EquipmentAppState.class);
        super.initialize(stateManager, app);
    }


    @Override
    public void onItemPickup(EntityId actor, EntityId itemToPickup) {
        itemStoreAppState.storeItem(actor, itemToPickup);
    }

    @Override
    public void onItemDrop(EntityId playerId, EntityId itemToDrop) {
        itemStoreAppState.dropItem(playerId, itemToDrop);
    }

    @Override
    public void onItemEquipped(EntityId player, EntityId itemToEquip) {
        equipmentAppState.equipItem(player, itemToEquip);
    }

    @Override
    public void onItemUnequipped(EntityId player, EntityId itemToRemove, EntityId containerId) {
        if (containerId == null) {
            equipmentAppState.unequipItem(itemToRemove);
        } else {
            equipmentAppState.unequipItem(containerId, itemToRemove);
        }
    }

    @Override
    public void onInputChange(EntityId entityId, String mappingName, boolean isPressed) {
        this.playerInputControlAppState.applyInputChange(entityId, mappingName, isPressed);
    }

    @Override
    public void onViewDirectionChange(EntityId entityId, Vector3f newViewDirection) {
        physicsPlayerMovementAppState.setViewDirection(entityId, newViewDirection);
    }

    @Override
    public void onInteraction(EntityId playerId, EntityId interactedEntity) {
        //   triggerAppState.onInteraction(playerId, interactedEntity);
        interactionAppState.interactWith(playerId, interactedEntity);
    }
}
