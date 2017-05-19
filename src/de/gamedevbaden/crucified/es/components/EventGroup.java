package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
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
