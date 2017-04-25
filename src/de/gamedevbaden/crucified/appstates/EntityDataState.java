package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;

/**
 * This app state just holds and creates the {@link EntityData} object.
 * Created by Domenic on 11.04.2017.
 */
public class EntityDataState extends AbstractAppState {

    private EntityData entityData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = new DefaultEntityData();
        super.initialize(stateManager, app);
    }


    public EntityData getEntityData() {
        return entityData;
    }

    @Override
    public void cleanup() {
        entityData.close();
        entityData = null;
        super.cleanup();
    }
}
