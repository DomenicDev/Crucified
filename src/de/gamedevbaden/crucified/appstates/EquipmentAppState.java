package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.enums.EquipmentLocation;
import de.gamedevbaden.crucified.es.components.Container;
import de.gamedevbaden.crucified.es.components.Equipable;
import de.gamedevbaden.crucified.es.components.EquippedBy;
import de.gamedevbaden.crucified.es.components.StoredIn;

/**
 * Created by Domenic on 20.05.2017.
 */
public class EquipmentAppState extends AbstractAppState {

    private EntitySet equipables;
    private EntitySet equippedEntities;
    private EntitySet containers;
    private EntityData entityData;

    private ItemStoreAppState itemStoreAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.equipables = entityData.getEntities(Equipable.class);
        this.equippedEntities = entityData.getEntities(EquippedBy.class);
        this.containers = entityData.getEntities(Container.class);
        this.itemStoreAppState = stateManager.getState(ItemStoreAppState.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        containers.applyChanges();
        equipables.applyChanges();
        equippedEntities.applyChanges();
    }

    public void equipItem(EntityId equipper, EntityId itemToEquip) {
        if (equipper == null || itemToEquip == null || !equipables.containsId(itemToEquip)) {
            return;
        }

        if (!canBeEquipped(equipper, itemToEquip)) {
            return;
        }

        // equip item
        entityData.setComponent(itemToEquip, new EquippedBy(equipper));

        // if we equip an item which is stored in a container (e.g. the players inventory)
        // we need to remove the StoredIn component
        if (entityData.getComponent(itemToEquip, StoredIn.class) != null) {
            entityData.removeComponent(itemToEquip, StoredIn.class);
        }
    }

    public void unequipItem(EntityId itemToUnequip) {
        if (equippedEntities.containsId(itemToUnequip)) {
            entityData.removeComponent(itemToUnequip, EquippedBy.class);
        }
    }

    /**
     * Checks whether item can be equipped or not.
     * Will return false if there is already an item attached at this location.
     *
     * @param target      the entity itemToEquip shall be equipped by
     * @param itemToEquip the item which shall be equipped
     * @return true if item can be equipped
     */
    private boolean canBeEquipped(EntityId target, EntityId itemToEquip) {
        // get the location where this item shall be equipped to
        EquipmentLocation loc = equipables.getEntity(itemToEquip).get(Equipable.class).getEquipmentLocation();

        // search all items which have been equipped by the target entity
        // and look if there is already an item attached at the desired
        // equipment location
        for (Entity entity : equippedEntities) {
            if (entity.get(EquippedBy.class).getPlayer().equals(target)) {
                EquipmentLocation loc2 = equipables.getEntity(entity.getId()).get(Equipable.class).getEquipmentLocation();
                if (loc == loc2) {
                    return false; // there already is an item attached at this location
                }
            }
        }

        return true; // no conflicts, so item can be equipped
    }

    /**
     * Use this method to unequip an item and add it to the supplied container.
     *
     * @param containerId
     * @param itemToUnequip
     */
    public void unequipItem(EntityId containerId, EntityId itemToUnequip) {
        if (containers.containsId(containerId) && equippedEntities.containsId(itemToUnequip)) {

            entityData.removeComponent(itemToUnequip, EquippedBy.class);
            //       entityData.setComponent(itemToUnequip, new StoredIn(containerId));

            itemStoreAppState.storeItem(containerId, itemToUnequip);
        }
    }

    @Override
    public void cleanup() {
        this.equipables.release();
        this.equipables.clear();
        this.equipables = null;

        this.equippedEntities.release();
        this.equippedEntities.clear();
        this.equippedEntities = null;

        this.containers.release();
        this.containers.clear();
        this.containers = null;
        super.cleanup();
    }
}
