package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * An event group is the interface between events and their triggers.
 * It is possible to add several triggers and even events to one event group.
 * Created by Domenic on 10.05.2017.
 */
public class EventGroup implements EntityComponent {

    private int remainingTriggerCount;

    public EventGroup() {
    }

    public EventGroup(int remainingTriggerCount) {
        this.remainingTriggerCount = remainingTriggerCount;
    }

    public int getRemainingTriggerCount() {
        return remainingTriggerCount;
    }
}
