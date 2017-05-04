package de.gamedevbaden.crucified.game;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ToDo: All !!!
 * Created by Domenic on 01.05.2017.
 */
public class DefaultGameSessionImplementation extends AbstractAppState {

    private ArrayList<GameEventListener> listeners = new ArrayList<>();
    private HashMap<EntityId, GameSession> sessionHashMap = new HashMap<>();

    public DefaultGameSessionImplementation() {
    }


    public GameSession addPlayer(EntityId playerId) {
        if (playerId != null) {
            GameSessionImplementation implementation = new GameSessionImplementation(playerId);
            sessionHashMap.put(playerId, implementation);
            return implementation;
        }
        return null;
    }

    public GameSession getGameSession(EntityId entityId) {
        return sessionHashMap.get(entityId);
    }

    public void addGameEventListener(GameEventListener listener) {
        this.listeners.add(listener);
    }


    private class GameSessionImplementation implements GameSession {

        private EntityId playerId;

        public GameSessionImplementation(EntityId playerId) {
            this.playerId = playerId;
        }

        @Override
        public EntityId getPlayer() {
            return playerId;
        }

        @Override
        public boolean pickUpItem(EntityId itemToPickup) {
            return false;
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

    }

}
