package de.gamedevbaden.crucified.game;

import com.jme3.math.Vector3f;
import com.jme3.network.service.rmi.Asynchronous;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.ActionType;

/**
 * {@link GameSession} provides the fundamental methods for player interaction in the game world.
 * When those methods are called other app states continue evaluating them.
 * In networked sessions those methods or called through a RMI service.
 * Created by Domenic on 01.05.2017.
 */
public interface GameSession {

    /**
     * Gets the player entity for the current game session
     * @return the player entity
     */
    EntityId getPlayer();

    /**
     * Call this to let the player pick up an item
     * @param itemToPickup the item the player wants to pick up
     */
    void pickUpItem(EntityId itemToPickup);

    /**
     * Equips the given entity to the player.
     * @param itemToEquip the item to equip
     */
    void equipItem(EntityId itemToEquip);

    /**
     * Removes this equipped entity from the player.
     * If the item shall be stored somewhere you also can set the container id
     * @param itemToRemove which item to remove
     * @param containerId  the container the item shall be stored in or null
     */
    void unequipItem(EntityId itemToRemove, EntityId containerId);

    /**
     * Drops the specified item from the players inventory
     * @param itemToDrop the item to drop
     */
    void dropItem(EntityId itemToDrop);

    void applyInput(String mappingName, boolean isPressed);

    /**
     * Applies the current view direction for the player entity
     * In networked sessions this method is used with RMI, but since this
     * is called very often it is unreliable (sent over UDP).
     * @param viewDirection the view direction of the player.
     */
    @Asynchronous(reliable = false)
    void applyViewDirection(Vector3f viewDirection);

    /**
     * Is called when the player triggers an InteractionTrigger, e.g.
     * opening a door
     *
     * @param interactedEntity the trigger entity id
     */
    void interactWithEntity(EntityId interactedEntity);

    /**
     * This method toggles the specified flashlight.
     * @param flashLightId the flashlight to toggle
     */
    void toggleFlashLight(EntityId flashLightId);

    /**
     * This method will put the specified artifact into the specified container.
     *
     * @param containerId the container id
     * @param artifactId  the artifact id
     */
    void putArtifactIntoContainer(EntityId containerId, EntityId artifactId);

    /**
     * Apply the specified ingredient item for crafting the specified item.
     * This might need to be called several times since several items are needed to craft
     * a certain entity
     * @param itemToCraft the entity to craft
     * @param ingredient the item (entity) which is needed for crafting
     */
    void putItemToCraft(EntityId itemToCraft, EntityId ingredient);

    /**
     * This will make the entity perform the specified action
     * unless the entity does not contain this type of action
     * in the {@link de.gamedevbaden.crucified.es.components.ActionGroupComponent}
     *
     * @param actionType the action to perform
     */
    void performAction(ActionType actionType);

}
