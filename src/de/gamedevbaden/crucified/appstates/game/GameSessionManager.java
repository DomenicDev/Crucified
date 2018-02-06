package de.gamedevbaden.crucified.appstates.game;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.ActionType;
import de.gamedevbaden.crucified.game.GameEventListener;
import de.gamedevbaden.crucified.game.GameSession;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Holds and creates GameSessions and informs all added listeners about in game events.
 *
 * Created by Domenic on 01.05.2017.
 */
public class GameSessionManager extends AbstractAppState {

    private ArrayList<GameEventListener> listeners = new ArrayList<>();
    private HashMap<EntityId, GameSession> sessionHashMap = new HashMap<>();

    public GameSessionManager() {
    }

    /**
     * Creates a GameSession for and with the specified player entity.
     *
     * @param playerId the entity id of the player
     * @return a GameSession object.
     */
    public GameSession createSession(EntityId playerId) {
        if (playerId != null) {
            GameSessionImplementation implementation = new GameSessionImplementation(playerId);
            sessionHashMap.put(playerId, implementation);
            return implementation;
        }
        return null;
    }

    /**
     * Returns the GameSession object for the specified entity id or null if the session doesn't exist for that id
     * @param entityId the (player) entity id you want the GameSession from
     * @return the GameSession object or null if it doesn't exist.
     */
    public GameSession getGameSession(EntityId entityId) {
        return sessionHashMap.get(entityId);
    }

    /**
     * Adds the specified listener. This listener will be informed about in game events
     * @param listener the listener to add
     */
    public void addGameEventListener(GameEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void cleanup() {
        this.sessionHashMap.clear();
        this.sessionHashMap = null;
        this.listeners.clear();
        this.listeners = null;
        super.cleanup();
    }

    private class GameSessionImplementation implements GameSession {

        private EntityId playerId;

        GameSessionImplementation(EntityId playerId) {
            this.playerId = playerId;
        }

        @Override
        public EntityId getPlayer() {
            return playerId;
        }

        @Override
        public void pickUpItem(EntityId itemToPickup) {
            for (GameEventListener listener : listeners) {
                listener.onItemPickup(playerId, itemToPickup);
            }
        }

        @Override
        public void equipItem(EntityId itemToEquip) {
            for (GameEventListener listener : listeners) {
                listener.onItemEquipped(playerId, itemToEquip);
            }
        }

        @Override
        public void unequipItem(EntityId itemToRemove, EntityId containerId) {
            for (GameEventListener listener : listeners) {
                listener.onItemUnequipped(playerId, itemToRemove, containerId);
            }
        }

        @Override
        public void dropItem(EntityId itemToDrop) {
            for (GameEventListener listener : listeners) {
                listener.onItemDrop(playerId, itemToDrop);
            }
        }

        @Override
        public void applyInput(String mappingName, boolean isPressed) {
            for (GameEventListener listener : listeners) {
                listener.onInputChange(playerId, mappingName, isPressed);
            }
        }


        @Override
        public void applyViewDirection(Vector3f viewDirection) {
            for (GameEventListener listener : listeners) {
                listener.onViewDirectionChange(getPlayer(), viewDirection);
            }
        }

        @Override
        public void interactWithEntity(EntityId interactedEntity) {
            for (GameEventListener listener : listeners) {
                listener.onInteraction(playerId, interactedEntity);
            }
        }

        @Override
        public void toggleFlashLight(EntityId flashLightId) {
            for (GameEventListener listener : listeners) {
                listener.onFlashLightToggled(playerId, flashLightId);
            }
        }

        @Override
        public void putArtifactIntoContainer(EntityId containerId, EntityId artifactId) {
            for (GameEventListener listener : listeners) {
                listener.onPutArtifactIntoContainer(containerId, artifactId);
            }
        }

        @Override
        public void putItemToCraft(EntityId itemToCraft, EntityId ingredient) {
            for (GameEventListener listener : listeners) {
                listener.onItemPutForCraft(itemToCraft, ingredient);
            }
        }

        @Override
        public void performAction(ActionType actionType) {
            for (GameEventListener listener : listeners) {
                listener.onPerformAction(getPlayer(), actionType);
            }
        }

    }
}
