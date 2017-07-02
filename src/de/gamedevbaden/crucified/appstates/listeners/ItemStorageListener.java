package de.gamedevbaden.crucified.appstates.listeners;

import com.simsilica.es.EntityId;

/**
 * {@link ItemStorageListener} provides two methods in order to listen for store events.
 * Created by Domenic on 15.06.2017.
 */
public interface ItemStorageListener {

    /**
     * Called when an item has been stored.
     * @param storedItem the stored item entity
     */
    void onItemStored(EntityId storedItem);

    /**
     * Called when an item has been dropped.
     * @param droppedItem the dropped item.
     */
    void onItemDropped(EntityId droppedItem);

}