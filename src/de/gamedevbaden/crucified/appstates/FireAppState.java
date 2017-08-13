package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.FireState;

/**
 * This states provides the ability to change the {@link FireState} component of entities.
 */
public class FireAppState extends AbstractAppState {

    private EntitySet fireEntities;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.fireEntities = entityData.getEntities(FireState.class);
        super.initialize(stateManager, app);
    }

    /**
     * Set the new state of the specified entity.
     * If the entity does not has the required component or the value wouldn't change
     * nothing will happen.
     * @param entityId the entity with the {@link FireState} component
     * @param onFire the new state of the entity
     */
    public void setFireState(EntityId entityId, boolean onFire) {
        fireEntities.applyChanges();
        if (!fireEntities.containsId(entityId)) {
            return;
        }
        FireState state = fireEntities.getEntity(entityId).get(FireState.class);
        if (state.isOn() != onFire) { // only set state if value has changed
            fireEntities.getEntity(entityId).set(new FireState(onFire));
        }
    }

    @Override
    public void cleanup() {
        this.fireEntities.release();
        this.fireEntities.clear();
        this.fireEntities = null;
        super.cleanup();
    }
}
