package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.listeners.ItemStorageListener;
import de.gamedevbaden.crucified.es.components.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This state handles all kind of inventories.
 * It provides methods to store and remove items from container entities.
 *
 * Created by Domenic on 19.05.2017.
 */
public class ItemStoreAppState extends AbstractAppState {

    private EntitySet pickables;
    private EntitySet containers;
    private EntitySet storedEntities;
    private EntitySet itemTypeEntities;
    private EntityData entityData;

    private List<ItemStorageListener> listeners = new ArrayList<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.pickables = entityData.getEntities(Pickable.class);
        this.containers = entityData.getEntities(Container.class);
        this.storedEntities = entityData.getEntities(StoredIn.class);
        this.itemTypeEntities = entityData.getEntities(ItemComponent.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        pickables.applyChanges();
        containers.applyChanges();
        storedEntities.applyChanges();
        itemTypeEntities.applyChanges();
    }

    public boolean storeItem(EntityId container, EntityId itemToPickup) {
        if (container != null && itemToPickup != null && containers.containsId(container) && pickables.containsId(itemToPickup)) {
            // we now check if this item can even put into this container
            // we first check the type then the capacity
            Container c = containers.getEntity(container).get(Container.class);
            if (c.getTypeToStore() != null) {
                Entity e = itemTypeEntities.getEntity(itemToPickup);
                if (e == null || !e.get(ItemComponent.class).getItemType().equals(c.getTypeToStore())) {
                    return false;
                }
            }
            if (c.getCapacity() != -1) {
                int itemCounter = 0;
                for (Entity entity : storedEntities) {
                    if (entity.get(StoredIn.class).getContainer().equals(container)) {
                        itemCounter++;
                    }
                }
                if (itemCounter >= c.getCapacity()) {
                    // the container is full
                    // no more items can be stored
                    return false;
                }
            }

            this.entityData.setComponents(itemToPickup, new StoredIn(container) /*, new ChildOf(container)*/);

            // call listeners
            for (ItemStorageListener l : listeners) {
                l.onItemStored(itemToPickup);
            }
            return true;
        }
        return false;
    }

    public void dropItem(EntityId container, EntityId itemToDrop) {
        if (storedEntities.containsId(itemToDrop)) {

            // remove StoredIn component
            entityData.removeComponent(itemToDrop, StoredIn.class);

            // remove the ChildOf component because we added it when item was picked up
            //      entityData.removeComponent(itemToDrop, ChildOf.class);

            // test: drop at containers position
            if (entityData.getComponent(container, Transform.class) != null) {
                Transform t = entityData.getComponent(container, Transform.class);
                entityData.setComponent(itemToDrop, new Transform(t.getTranslation(), t.getRotation(), t.getScale()));
            }

            // call listeners
            for (ItemStorageListener l : listeners) {
                l.onItemDropped(itemToDrop);
            }
        }
    }

    public void addListener(ItemStorageListener listener) {
        this.listeners.add(listener);
    }


    @Override
    public void cleanup() {
        this.pickables.release();
        this.pickables.clear();
        this.pickables = null;

        this.containers.release();
        this.containers.clear();
        this.containers = null;

        this.storedEntities.release();
        this.storedEntities.clear();
        this.storedEntities = null;

        this.listeners.clear();
        this.listeners = null;
        super.cleanup();
    }
}
