package de.gamedevbaden.crucified.appstates.game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.appstates.action.ActionSystemAppState;
import de.gamedevbaden.crucified.appstates.gamelogic.ArtifactContainerAppState;
import de.gamedevbaden.crucified.enums.ActionType;
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
    private InteractionAppState interactionAppState;
    private ItemStoreAppState itemStoreAppState;
    private EquipmentAppState equipmentAppState;
    private ItemFunctionalityAppState itemFunctionalityAppState;
    private CraftingAppState craftingAppState;
    private ArtifactContainerAppState artifactContainerAppState;
    private ActionSystemAppState actionSystemAppState;

    public GameEventHandler(GameSessionManager gameSession) {
        gameSession.addGameEventListener(this);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.playerInputControlAppState = stateManager.getState(PlayerInputControlAppState.class);
        this.physicsPlayerMovementAppState = stateManager.getState(PhysicsPlayerMovementAppState.class);
        this.interactionAppState = stateManager.getState(InteractionAppState.class);
        this.itemStoreAppState = stateManager.getState(ItemStoreAppState.class);
        this.equipmentAppState = stateManager.getState(EquipmentAppState.class);
        this.itemFunctionalityAppState = stateManager.getState(ItemFunctionalityAppState.class);
        this.craftingAppState = stateManager.getState(CraftingAppState.class);
        this.artifactContainerAppState = stateManager.getState(ArtifactContainerAppState.class);
        this.actionSystemAppState = stateManager.getState(ActionSystemAppState.class);
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

    @Override
    public void onFlashLightToggled(EntityId playerId, EntityId flashLightId) {
        itemFunctionalityAppState.toggleFlashLight(flashLightId);
    }

    @Override
    public void onItemPutForCraft(EntityId itemToCraft, EntityId ingredient) {
        craftingAppState.putItemForCrafting(itemToCraft, ingredient);
    }

    @Override
    public void onPutArtifactIntoContainer(EntityId containerId, EntityId artifactId) {
        artifactContainerAppState.throwArtifactIntoContainer(containerId, artifactId);
    }

    @Override
    public void onPerformAction(EntityId performerId, ActionType actionType) {
        actionSystemAppState.performAction(performerId, actionType);
    }
}
