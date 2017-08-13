package de.gamedevbaden.crucified.appstates.listeners;

import com.simsilica.es.EntityId;

/**
 * {@link EquipmentListener} provides two methods in order to listen for equipment events.
 * <p>
 * Created by Domenic on 15.06.2017.
 */
public interface EquipmentListener {

    /**
     * Called when an item has been equipped.
     *
     * @param equippedItem the equipped entity
     */
    void onItemEquipped(EntityId equippedItem);

    /**
     * Called when an equipped has been un-equipped.
     *
     * @param unequippedItem the removed entity
     */
    void onItemUnequipped(EntityId unequippedItem);

}
