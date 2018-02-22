package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.listeners.InputChangeListener;
import de.gamedevbaden.crucified.enums.InputCommand;
import de.gamedevbaden.crucified.es.components.PlayerControlled;
import de.gamedevbaden.crucified.utils.PlayerInputCollector;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class holds the current inputs for each player controlled entity.
 * <p>
 * Created by Domenic on 01.05.2017.
 */
public class PlayerInputControlAppState extends AbstractAppState {

    private EntitySet playerControlledEntities;
    private HashMap<EntityId, PlayerInputCollector> inputCollectorHashMap = new HashMap<>();
    private HashMap<EntityId, ArrayList<InputChangeListener>> inputChangeListenerHashMap = new HashMap<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.playerControlledEntities = entityData.getEntities(PlayerControlled.class);

        for (Entity entity : playerControlledEntities) {
            addInputCollector(entity);
        }

        super.initialize(stateManager, app);
    }


    public void addInputChangeListener(EntityId entityId, InputChangeListener listener) {
        if (!inputChangeListenerHashMap.containsKey(entityId)) {
            inputChangeListenerHashMap.put(entityId, new ArrayList<>());
        }
        inputChangeListenerHashMap.get(entityId).add(listener);
    }

    public void removeInputListener(EntityId entityId, InputChangeListener listener) {
        if (inputChangeListenerHashMap.containsKey(entityId)) {
            ArrayList<InputChangeListener> list = inputChangeListenerHashMap.get(entityId);
            if (list.contains(listener)) {
                list.remove(listener);
            }
        }
    }

    public void applyInputChange(EntityId entityId, String mappingName, boolean isPressed) {
        if (!inputCollectorHashMap.containsKey(entityId)) {
            PlayerInputCollector collector = new PlayerInputCollector();
            inputCollectorHashMap.put(entityId, collector);
        }

        InputCommand input = InputCommand.valueOf(mappingName);
        input.setPressed(isPressed);

        PlayerInputCollector collector = inputCollectorHashMap.get(entityId);
        if (input == InputCommand.Forward) {
            collector.setForward(input.isPressed());
        } else if (input == InputCommand.Backward) {
            collector.setBackward(input.isPressed());
        } else if (input == InputCommand.Left) {
            collector.setLeft(input.isPressed());
        } else if (input == InputCommand.Right) {
            collector.setRight(input.isPressed());
        } else if (input == InputCommand.Shift) {
            collector.setRunning(input.isPressed());
        }


        // call listener
        for (InputChangeListener listener : inputChangeListenerHashMap.get(entityId)) {
            if (listener != null) {
                listener.onInputChange(entityId, input, collector);
            }
        }
    }

    @Override
    public void update(float tpf) {

        if (playerControlledEntities.applyChanges()) {

            for (Entity entity : playerControlledEntities.getAddedEntities()) {
                addInputCollector(entity);
            }

            for (Entity entity : playerControlledEntities.getRemovedEntities()) {
                inputCollectorHashMap.remove(entity.getId());
            }

        }

    }

    private void addInputCollector(Entity entity) {
        if (!inputCollectorHashMap.containsKey(entity.getId())) {
            inputCollectorHashMap.put(entity.getId(), new PlayerInputCollector());
        }
    }



    public PlayerInputCollector getPlayerInput(EntityId entityId) {
        return inputCollectorHashMap.get(entityId);
    }

    @Override
    public void cleanup() {
        this.playerControlledEntities.release();
        this.playerControlledEntities.clear();
        this.playerControlledEntities = null;

        this.inputCollectorHashMap.clear();
        this.inputChangeListenerHashMap.clear();

        super.cleanup();
    }
}
