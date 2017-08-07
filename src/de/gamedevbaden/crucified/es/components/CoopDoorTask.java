package de.gamedevbaden.crucified.es.components;

import com.jme3.bounding.BoundingVolume;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

public class CoopDoorTask implements EntityComponent {

    private BoundingVolume triggerOne, triggerTwo;
    private EntityId doorId;

    public CoopDoorTask(BoundingVolume triggerOne, BoundingVolume triggerTwo, EntityId doorId) {
        this.triggerOne = triggerOne;
        this.triggerTwo = triggerTwo;
        this.doorId = doorId;
    }

    public BoundingVolume getTriggerOne() {
        return triggerOne;
    }

    public BoundingVolume getTriggerTwo() {
        return triggerTwo;
    }

    public EntityId getDoorId() {
        return doorId;
    }
}
