package de.gamedevbaden.crucified.game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.*;

/**
 * The {@link GameEventListener} listens for game related events and informs the relevant app states about
 * the event so they can handle those events. This class is sort of the interface between the game events
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

    private DefaultGameSessionImplementation gameSession;

    public GameEventHandler(DefaultGameSessionImplementation gameSession) {
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
        super.initialize(stateManager, app);
    }


    @Override
    public void onItemPickup(EntityId actor, EntityId itemToPickup) {
        itemStoreAppState.pickUpItem(actor, itemToPickup);
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
        interactionAppState.interactWith(interactedEntity);
    }
}
