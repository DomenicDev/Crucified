package de.gamedevbaden.crucified.es.triggersystem;

import com.simsilica.es.EntityId;

/**
 * Created by Domenic on 13.05.2017.
 */
public class OpenCloseEvent implements EventType {

    private EntityId entityId;

    /**
     * @param entityId The entity whose OpenClose component shall be changed
     */
    public OpenCloseEvent(EntityId entityId) {
        this.entityId = entityId;
    }

    public EntityId getEntityId() {
        return entityId;
    }
}
