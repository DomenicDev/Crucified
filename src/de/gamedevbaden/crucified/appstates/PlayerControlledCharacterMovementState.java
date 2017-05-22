package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.es.components.PlayerControlled;
import de.gamedevbaden.crucified.utils.InputChangeListener;
import de.gamedevbaden.crucified.utils.PlayerInputCollector;

import java.util.HashMap;

/**
 * This AppState applies the player input to the {@link CharacterMovementState} component.
 * Created by Domenic on 01.05.2017.
 */
public class PlayerControlledCharacterMovementState extends AbstractAppState {

    private EntityData entityData;
    private EntitySet playerControlledCharacters;
    private PlayerInputControlAppState inputControlAppState;
    private HashMap<EntityId, InputChangeListener> listeners;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.listeners = new HashMap<>();
        this.inputControlAppState = stateManager.getState(PlayerInputControlAppState.class);

        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.playerControlledCharacters = entityData.getEntities(PlayerControlled.class, CharacterMovementState.class);

        for (Entity entity : playerControlledCharacters) {
            addEntity(entity);
        }

        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {

        if (playerControlledCharacters.applyChanges()) {

            for (Entity entity : playerControlledCharacters.getAddedEntities()) {
                addEntity(entity);
            }

            for (Entity entity : playerControlledCharacters.getRemovedEntities()) {
                removeEntity(entity);
            }

        }

    }

    private void addEntity(Entity entity) {

        InputChangeListener listener = (entityId, changedInput, playerInputCollector) -> this.entityData.setComponent(entityId, new CharacterMovementState(calculateMovementState(playerInputCollector)));
        inputControlAppState.addInputChangeListener(entity.getId(), listener);
        listeners.put(entity.getId(), listener);
    }

    private void removeEntity(Entity entity) {
        inputControlAppState.removeInputListener(entity.getId(), listeners.get(entity.getId()));
    }

    private int calculateMovementState(PlayerInputCollector collector) {
        if (collector.isRunning()) {
            if (collector.isForward()) {
                if (collector.isRight()) {
                    return CharacterMovementState.RUNNING_FORWARD_RIGHT;
                } else if (collector.isLeft()) {
                    return CharacterMovementState.RUNNING_FORWARD_LEFT;
                } else {
                    return CharacterMovementState.RUNNING_FORWARD;
                }
            } else if (collector.isBackward()) {
                if (collector.isLeft()) {
                    return CharacterMovementState.RUNNING_BACK_LEFT;
                } else if (collector.isRight()) {
                    return CharacterMovementState.RUNNING_BACK_RIGHT;
                } else {
                    return CharacterMovementState.RUNNING_BACK;
                }
            }
        } else { // not running
            if (collector.isForward()) {
                if (collector.isRight()) {
                    return CharacterMovementState.MOVING_FORWARD_RIGHT;
                } else if (collector.isLeft()) {
                    return CharacterMovementState.MOVING_FORWARD_LEFT;
                } else {
                    return CharacterMovementState.MOVING_FORWARD;
                }
            } else if (collector.isBackward()) {
                if (collector.isLeft()) {
                    return CharacterMovementState.MOVING_BACK_LEFT;
                } else if (collector.isRight()) {
                    return CharacterMovementState.MOVING_BACK_RIGHT;
                } else {
                    return CharacterMovementState.MOVING_BACK;
                }
            }
        }
        if (collector.isLeft()) { // side walking (no running)
            return CharacterMovementState.MOVING_LEFT;
        } else if (collector.isRight()) {
            return CharacterMovementState.MOVING_RIGHT;
        }

        // if nothing fits, the character stands still
        return CharacterMovementState.IDLE;
    }

    @Override
    public void cleanup() {
        this.playerControlledCharacters.release();
        this.playerControlledCharacters.clear();
        this.playerControlledCharacters = null;

        this.listeners.clear();
        this.listeners = null;

        this.inputControlAppState = null;
        this.entityData = null;

        super.cleanup();
    }
}
