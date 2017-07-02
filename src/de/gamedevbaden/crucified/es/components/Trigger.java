package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.es.triggersystem.TriggerType;

/**
 * Triggers the events of the specified event group if triggered.
 * Created by Domenic on 09.05.2017.
 */
public class Trigger implements EntityComponent {

    private EntityId eventGroupId;
    private TriggerType triggerType;

    public Trigger() {
    }

    public Trigger(EntityId eventGroupId, TriggerType triggerType) {
        this.eventGroupId = eventGroupId;
        this.triggerType = triggerType;
    }

    public EntityId getEventGroupId() {
        return eventGroupId;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

}
