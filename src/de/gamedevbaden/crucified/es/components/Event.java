package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.es.triggersystem.EventType;

/**
 * Created by Domenic on 10.05.2017.
 */
public class Event implements EntityComponent {

    private EntityId eventGroupId;
    private EventType eventType;

    public Event() {
    }

    public Event(EntityId eventGroupId, EventType eventType) {
        this.eventGroupId = eventGroupId;
        this.eventType = eventType;
    }

    public EntityId getEventGroupId() {
        return eventGroupId;
    }

    public EventType getEventType() {
        return eventType;
    }
}
