package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.listeners.DoorStateListener;
import de.gamedevbaden.crucified.es.components.OpenedClosedState;

import java.util.ArrayList;

/**
 * This app state allows to change the {@link OpenedClosedState} component.
 * This can be used to open or close doors for example.
 *
 * @author Domenic
 */
public class DoorAppState extends AbstractAppState {

    private EntitySet doors;
    private ArrayList<DoorStateListener> listeners = new ArrayList<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.doors = entityData.getEntities(OpenedClosedState.class);
        super.initialize(stateManager, app);
    }

    public void addListener(DoorStateListener l) {
        listeners.add(l);
    }

    public void changeState(EntityId entityId) {
        doors.applyChanges();
        if (doors.containsId(entityId)) {
            OpenedClosedState state = doors.getEntity(entityId).get(OpenedClosedState.class);
            boolean open = !state.isOpened();
            doors.getEntity(entityId).set(new OpenedClosedState(open));
            callListeners(entityId, open);
        }
    }

    public void setNewState(EntityId entityId, boolean open) {
        doors.applyChanges();
        if (doors.containsId(entityId)) {
            OpenedClosedState state = doors.getEntity(entityId).get(OpenedClosedState.class);
            if (state.isOpened() != open) {
                // we apply the new state only if the value has changed
                doors.getEntity(entityId).set(new OpenedClosedState(open));
                callListeners(entityId, open);
            }
        }
    }

    private void callListeners(EntityId entityId, boolean open) {
        for (DoorStateListener l : listeners) {
            l.onStateChanged(entityId, open);
        }
    }
}
