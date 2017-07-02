package de.gamedevbaden.crucified.appstates;

import com.jme3.app.state.AbstractAppState;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;

/**
 * This app state just stores or creates an {@link EntityData} object.
 * Created by Domenic on 11.04.2017.
 */
public class EntityDataState extends AbstractAppState {

    private EntityData entityData;

    public EntityDataState() {
        this.entityData = new DefaultEntityData();
    }

    public EntityDataState(EntityData entityData) {
        this.entityData = entityData;
    }

    public EntityData getEntityData() {
        return entityData;
    }

    @Override
    public void cleanup() {
        if (entityData == null) {
            return;
        }
        entityData.close();
        entityData = null;
        super.cleanup();
    }
}
