package de.gamedevbaden.crucified.es.triggersystem;

import com.jme3.bounding.BoundingVolume;

/**
 * With this trigger type the trigger event is triggered when a player enters this bounding volume.
 * <p>
 * Created by Domenic on 23.06.2017.
 */
public class OnEnterTrigger implements TriggerType {

    private BoundingVolume volume;

    public OnEnterTrigger(BoundingVolume volume) {
        this.volume = volume;
    }

    public BoundingVolume getVolume() {
        return volume;
    }
}
