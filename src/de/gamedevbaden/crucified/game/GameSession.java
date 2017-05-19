package de.gamedevbaden.crucified.game;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;


/**
 * Created by Domenic on 01.05.2017.
 */
public interface GameSession {

    EntityId getPlayer();

    boolean pickUpItem(EntityId itemToPickup);

    void applyInput(String mappingName, boolean isPressed);

    void applyViewDirection(Vector3f viewDirection);

    /**
     * Is called when the player triggers an InteractionTrigger, e.g.
     * opening a door, picking up an item
     *
     * @param interactedEntity the trigger entity id
     */
    void interactWithEntity(EntityId interactedEntity);

}
