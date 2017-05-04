package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.enums.InputMapping;
import de.gamedevbaden.crucified.es.components.PlayerControlled;
import de.gamedevbaden.crucified.utils.InputChangeListener;
import de.gamedevbaden.crucified.utils.PlayerInputCollector;

import java.util.HashMap;

/**
 * This class holds the current inputs for each player controlled entity.
 * <p>
 * Created by Domenic on 01.05.2017.
 */
public class PlayerInputControlAppState extends AbstractAppState {

    private EntitySet playerControlledEntities;
    private HashMap<EntityId, PlayerInputCollector> inputCollectorHashMap;
    private HashMap<EntityId, InputChangeListener> inputChangeListenerHashMap;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.playerControlledEntities = entityData.getEntities(PlayerControlled.class);

        this.inputCollectorHashMap = new HashMap<>();
        this.inputChangeListenerHashMap = new HashMap<>();

        super.initialize(stateManager, app);
    }


    public void addInputChangeListener(EntityId entityId, InputChangeListener listener) {
        inputChangeListenerHashMap.put(entityId, listener);
    }

    public void removeInputListener(EntityId entityId) {
        inputChangeListenerHashMap.remove(entityId);
    }

    public void applyInputChange(EntityId entityId, String mappingName, boolean isPressed) {
        if (!inputCollectorHashMap.containsKey(entityId)) {
            PlayerInputCollector collector = new PlayerInputCollector();
            inputCollectorHashMap.put(entityId, collector);
        }

        InputMapping input = InputMapping.valueOf(mappingName);
        input.setPressed(isPressed);

        PlayerInputCollector collector = inputCollectorHashMap.get(entityId);
        if (input == InputMapping.Forward) {
            collector.setForward(input.isPressed());
        } else if (input == InputMapping.Backward) {
            collector.setBackward(input.isPressed());
        } else if (input == InputMapping.Left) {
            collector.setLeft(input.isPressed());
        } else if (input == InputMapping.Right) {
            collector.setRight(input.isPressed());
        } else if (input == InputMapping.Shift) {
            collector.setRunning(input.isPressed());
        }


        // call listener
        InputChangeListener listener = inputChangeListenerHashMap.get(entityId);
        if (listener != null) {
            listener.onInputChange(entityId, input, collector);
        }
    }

    @Override
    public void update(float tpf) {

        if (playerControlledEntities.applyChanges()) {

            for (Entity entity : playerControlledEntities.getAddedEntities()) {
                if (!inputCollectorHashMap.containsKey(entity)) {
                    inputCollectorHashMap.put(entity.getId(), new PlayerInputCollector());
                }
            }

            for (Entity entity : playerControlledEntities.getRemovedEntities()) {
                inputCollectorHashMap.remove(entity.getId());
            }

        }

    }

    public PlayerInputCollector getPlayerInput(EntityId entityId) {
        return inputCollectorHashMap.get(entityId);
    }

    @Override
    public void cleanup() {
        this.playerControlledEntities.clear();
        this.playerControlledEntities = null;

        this.inputCollectorHashMap.clear();
        this.inputCollectorHashMap = null;

        this.inputChangeListenerHashMap.clear();
        this.inputChangeListenerHashMap = null;

        super.cleanup();
    }
}
