package de.gamedevbaden.crucified.game;

import com.jme3.math.Vector3f;
import com.jme3.network.service.rmi.Asynchronous;
import com.simsilica.es.EntityId;

/**
 * Created by Domenic on 01.05.2017.
 */
public interface GameSession {

    EntityId getPlayer();

    boolean pickUpItem(EntityId itemToPickup);

    /**
     * Equips the given entity to the player.
     *
     * @param itemToEquip
     */
    void equipItem(EntityId itemToEquip);

    /**
     * Removes this equipped entity from the player.
     * If the item shall be stored somewhere you also can set the container id
     *
     * @param itemToRemove which item to remove
     * @param containerId  the container the item shall be stored in or null
     */
    void unequipItem(EntityId itemToRemove, EntityId containerId);

    void dropItem(EntityId itemToDrop);

    void applyInput(String mappingName, boolean isPressed);

    @Asynchronous(reliable = false)
    void applyViewDirection(Vector3f viewDirection);

    /**
     * Is called when the player triggers an InteractionTrigger, e.g.
     * opening a door, picking up an item
     *
     * @param interactedEntity the trigger entity id
     */
    void interactWithEntity(EntityId interactedEntity);

    void toggleFlashLight(EntityId flashLightId);

}
