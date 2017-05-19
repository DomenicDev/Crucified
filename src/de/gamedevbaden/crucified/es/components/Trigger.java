package de.gamedevbaden.crucified.es.components;

import com.jme3.bounding.BoundingVolume;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.es.triggersystem.TriggerType;

/**
 * Created by Domenic on 09.05.2017.
 */
public class Trigger implements EntityComponent {

    private EntityId eventGroupId;
    private TriggerType triggerType;
    private BoundingVolume triggerVolume;

    public Trigger() {
    }

    public Trigger(EntityId eventGroupId, TriggerType triggerType, BoundingVolume triggerVolume) {
        this.eventGroupId = eventGroupId;
        this.triggerType = triggerType;
        this.triggerVolume = triggerVolume;
    }

    public EntityId getEventGroupId() {
        return eventGroupId;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public BoundingVolume getTriggerVolume() {
        return triggerVolume;
    }
}
