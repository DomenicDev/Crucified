package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.Decay;

/**
 * Watches entities with a Decay component and removes them when their time is up.
 * Created by Paul Speed
 */
public class DecayAppState extends AbstractAppState {

    private EntityData entityData;
    private EntitySet decayingEntities;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.decayingEntities = entityData.getEntities(Decay.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        decayingEntities.applyChanges();
        for (Entity entity : decayingEntities) {
            Decay decay = entity.get(Decay.class);
            if (decay.getPercent() >= 1.0f) {
                entityData.removeEntity(entity.getId());
            }
        }
    }

    @Override
    public void cleanup() {
        this.decayingEntities.release();
        this.decayingEntities.clear();
        this.decayingEntities = null;
        super.cleanup();
    }
}
