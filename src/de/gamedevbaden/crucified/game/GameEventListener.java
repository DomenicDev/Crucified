package de.gamedevbaden.crucified.game;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;

/**
 * Created by Domenic on 03.05.2017.
 */
public interface GameEventListener {

    void onItemPickup(EntityId actor, EntityId itemToPickup);

    void onInputChange(EntityId entityId, String mappingName, boolean isPressed);

    void onViewDirectionChange(EntityId entityId, Vector3f newViewDirection);

    void onInteraction(EntityId playerId, EntityId interactedEntity);

}
