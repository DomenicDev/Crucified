package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.es.components.StoredIn;

/**
 * This app state basically just culls entities which are stored somewhere (e.g. the player's inventory).
 * Created by Domenic on 19.05.2017.
 */
public class VisualStoringAppState extends AbstractAppState {

    private EntitySet storedEntities;
    private ModelViewAppState modelViewAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.storedEntities = entityData.getEntities(StoredIn.class);
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (storedEntities.applyChanges()) {

            for (Entity entity : storedEntities.getAddedEntities()) {
                setCullHintForSpatial(entity.getId(), CullHint.Always); // cull spatial
            }

            for (Entity entity : storedEntities.getChangedEntities()) {
                setCullHintForSpatial(entity.getId(), CullHint.Always); // cull spatial
            }

            for (Entity entity : storedEntities.getRemovedEntities()) {
                setCullHintForSpatial(entity.getId(), CullHint.Inherit); // make spatial visible again
            }

        }

    }

    private void setCullHintForSpatial(EntityId entityId, CullHint cullHint) {
        Spatial spatial = modelViewAppState.getSpatial(entityId);
        if (spatial != null) {
            spatial.setCullHint(cullHint);
        }
    }

    @Override
    public void cleanup() {
        this.storedEntities.release();
        this.storedEntities.clear();
        this.storedEntities = null;
        super.cleanup();
    }
}
