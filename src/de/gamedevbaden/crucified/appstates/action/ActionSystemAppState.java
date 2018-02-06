package de.gamedevbaden.crucified.appstates.action;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.enums.ActionType;
import de.gamedevbaden.crucified.es.components.ActionComponent;
import de.gamedevbaden.crucified.es.components.ActionGroupComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionSystemAppState extends AbstractAppState {

    private EntityData entityData;
    private EntitySet performers; // the entities which can perform actions
    private EntitySet onActionEntities; // the entities which currently perform an action

    private Map<EntityId, Float> delayMap = new HashMap<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.performers = entityData.getEntities(ActionGroupComponent.class);
        this.onActionEntities = entityData.getEntities(ActionComponent.class);
    }

    @Override
    public void update(float tpf) {
        updateSets();
        if (!delayMap.isEmpty()) {
            List<EntityId> toRemove = new ArrayList<>();
            for (Map.Entry<EntityId, Float> e : delayMap.entrySet()) {
                float remainingTime = e.getValue() - tpf;
                if (remainingTime <= 0) {
                    // we can remove the action component
                    // so a new action can now be performed
                    entityData.removeComponent(e.getKey(), ActionComponent.class);
                    toRemove.add(e.getKey());
                } else {
                    // the entity still needs to wait
                    delayMap.put(e.getKey(), remainingTime);
                }
            }
            for (EntityId entityId : toRemove) {
                delayMap.remove(entityId);
            }
        }
    }

    public void performAction(EntityId performerId, ActionType actionType) {
        updateSets();
        // we check if the player can even perform this action
        // and if so we apply the action to this entity
        if (canPerformAction(performerId, actionType)) {
            applyAction(performerId, actionType);
        }
    }

    private boolean canPerformAction(EntityId performerId, ActionType type) {
        if (!performers.containsId(performerId) || onActionEntities.containsId(performerId)) {
            return false;
        }
        ActionType[] actions = performers.getEntity(performerId).get(ActionGroupComponent.class).getActions();
        for (ActionType a : actions) {
            if (a == type) {
                return true;
            }
        }
        return false;
    }

    private void applyAction(EntityId performerId, ActionType actionType) {
        entityData.setComponent(performerId, new ActionComponent(actionType));
        this.delayMap.put(performerId, actionType.getDelay());
    }

    private void updateSets() {
        this.performers.applyChanges();
        this.onActionEntities.applyChanges();
    }

    @Override
    public void cleanup() {
        this.performers.release();
        this.performers.clear();
        this.performers = null;

        this.onActionEntities.release();
        this.onActionEntities.clear();
        this.onActionEntities = null;

        this.delayMap.clear();
        super.cleanup();
    }
}
