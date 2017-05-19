package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.Pickable;
import de.gamedevbaden.crucified.es.components.StoredIn;

/**
 * Created by Domenic on 19.05.2017.
 */
public class ItemStoreAppState extends AbstractAppState {

    private EntitySet pickables;
    private EntityData entityData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.pickables = entityData.getEntities(Pickable.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        pickables.applyChanges();
    }

    public void pickUpItem(EntityId actor, EntityId itemToPickup) {

        if (actor != null && itemToPickup != null && pickables.containsId(itemToPickup)) {

            this.entityData.setComponent(itemToPickup, new StoredIn(actor));
            this.entityData.removeComponent(itemToPickup, Pickable.class);

            System.out.println("item picked up");

        }

    }

    public void dropItem(EntityId itemToDrop) {

    }

}
