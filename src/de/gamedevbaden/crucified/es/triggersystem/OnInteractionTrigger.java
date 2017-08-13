package de.gamedevbaden.crucified.es.triggersystem;

import com.simsilica.es.EntityId;

/**
 * This trigger is fired when a player interacts with the specified entity.
 * <p>
 * Created by Domenic on 23.06.2017.
 */
public class OnInteractionTrigger implements TriggerType {

    private EntityId interactedEntity;

    public OnInteractionTrigger(EntityId interactedEntity) {
        this.interactedEntity = interactedEntity;
    }

    public EntityId getInteractedEntity() {
        return interactedEntity;
    }
}
