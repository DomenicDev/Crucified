package de.gamedevbaden.crucified.appstates.cooptasks;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.DoorAppState;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.es.components.CoopDoorTask;
import de.gamedevbaden.crucified.es.components.OpenedClosedState;
import de.gamedevbaden.crucified.es.components.PlayerControlled;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;

public class TestCoopDoorTask extends AbstractAppState {

    private HashMap<EntityId, BoundingData> boundingDataHashMap = new HashMap<>();
    private EntitySet coopDoorTasks;
    private EntitySet players;
    private EntitySet doors;

    private DoorAppState doorAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.coopDoorTasks = entityData.getEntities(CoopDoorTask.class);
        this.players = entityData.getEntities(Transform.class, PlayerControlled.class);
        this.doors = entityData.getEntities(OpenedClosedState.class);

        this.doorAppState = stateManager.getState(DoorAppState.class);

        for (Entity entity : coopDoorTasks) {
            addEntity(entity);
        }
        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {
        doors.applyChanges();

        if (coopDoorTasks.applyChanges()) {

            for (Entity entity : coopDoorTasks.getAddedEntities()) {
                addEntity(entity);
            }

            for (Entity entity : coopDoorTasks.getRemovedEntities()) {
                removeEntity(entity);
            }
        }

        if (players.applyChanges()) {

            // check every task
            for (Entity task : coopDoorTasks) {

                BoundingData boundingData = boundingDataHashMap.get(task.getId());
                CoopDoorTask coopDoorTask = task.get(CoopDoorTask.class);
                EntityId doorId = coopDoorTask.getDoorId();
                BoundingVolume box1 = coopDoorTask.getTriggerOne();
                BoundingVolume box2 = coopDoorTask.getTriggerTwo();

                for (Entity entity : players.getChangedEntities()) {

                    Vector3f playerLocation = entity.get(Transform.class).getTranslation();

                    if (boundingData.getPlayerInsideOne() == null && box1.contains(playerLocation) && !boundingData.isInsideOne()) {
                        // a player just has entered the first bounding box
                        boundingData.setInsideOne(true);
                        boundingData.setPlayerInsideOne(entity.getId());
                    } else if (boundingData.getPlayerInsideTwo() == null && box2.contains(playerLocation) && !boundingData.isInsideTwo()) {
                        boundingData.setInsideTwo(true);
                        boundingData.setPlayerInsideTwo(entity.getId());
                    } else if (boundingData.getPlayerInsideOne() == entity.getId() && !box1.contains(playerLocation) && boundingData.isInsideOne()) {
                        boundingData.setInsideOne(false);
                        boundingData.setPlayerInsideOne(null);
                    } else if (boundingData.getPlayerInsideTwo() == entity.getId() && !box2.contains(playerLocation) && boundingData.isInsideTwo()) {
                        boundingData.setInsideTwo(false);
                        boundingData.setPlayerInsideTwo(null);
                    }
                }
                setNewDoorState(doorId, boundingData);
            }
        }
    }

    private void setNewDoorState(EntityId doorId, BoundingData data) {
        if (!doors.containsId(doorId)) {
            return;
        }

        OpenedClosedState state = doors.getEntity(doorId).get(OpenedClosedState.class);

        // we check if a change is necessary
        if ((!data.isInsideOne() && !data.isInsideTwo()) && state.isOpened()) {
            // no one is standing on the triggers but the door is still open
            // so we set the open-close-state to false (closed)
            doorAppState.setNewState(doorId, false);
        } else if (!state.isOpened() && (data.isInsideOne() || data.isInsideTwo())) {
            // there is at least one guy standing on a trigger but the state is still set to false
            // so we set it true (open)
            doorAppState.setNewState(doorId, true);
        }
    }

    private void addEntity(Entity entity) {
        boundingDataHashMap.put(entity.getId(), new BoundingData());
    }

    private void removeEntity(Entity entity) {
        boundingDataHashMap.remove(entity.getId());
    }

    @Override
    public void cleanup() {
        this.coopDoorTasks.release();
        this.coopDoorTasks.clear();
        this.coopDoorTasks = null;

        this.players.release();
        this.players.clear();
        this.players = null;

        this.boundingDataHashMap.clear();
        super.cleanup();
    }


    private class BoundingData {

        boolean insideOne;
        boolean insideTwo;
        private EntityId playerInsideOne;
        private EntityId playerInsideTwo;

        EntityId getPlayerInsideOne() {
            return playerInsideOne;
        }

        void setPlayerInsideOne(EntityId playerInsideOne) {
            this.playerInsideOne = playerInsideOne;
        }

        EntityId getPlayerInsideTwo() {
            return playerInsideTwo;
        }

        void setPlayerInsideTwo(EntityId playerInsideTwo) {
            this.playerInsideTwo = playerInsideTwo;
        }

        boolean isInsideOne() {
            return insideOne;
        }

        void setInsideOne(boolean insideOne) {
            this.insideOne = insideOne;
        }

        boolean isInsideTwo() {
            return insideTwo;
        }

        void setInsideTwo(boolean insideTwo) {
            this.insideTwo = insideTwo;
        }
    }
}
