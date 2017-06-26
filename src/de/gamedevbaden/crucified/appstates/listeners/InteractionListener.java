package de.gamedevbaden.crucified.appstates.listeners;

import com.simsilica.es.EntityId;

/**
 * This listener is used by the {@link de.gamedevbaden.crucified.appstates.InteractionAppState}
 * to inform other systems about an interaction.
 * Created by Domenic on 23.06.2017.
 */
public interface InteractionListener {

    /**
     * Is called when a player has interacted with an entity.
     *
     * @param interactedEntity the entity the player interacted with
     */
    void onInteract(EntityId interactedEntity);
}
