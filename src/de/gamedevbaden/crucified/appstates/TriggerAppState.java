package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.triggersystem.EventType;
import de.gamedevbaden.crucified.es.triggersystem.OpenCloseEvent;
import de.gamedevbaden.crucified.es.triggersystem.PlaySoundEventType;
import de.gamedevbaden.crucified.es.triggersystem.TriggerType;

/**
 * Created by Domenic on 10.05.2017.
 */
public class TriggerAppState extends AbstractAppState {

    private EntityData entityData;

    private EntitySet triggers;
    private EntitySet eventGroups;
    private EntitySet events;
    private EntitySet actors;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.triggers = entityData.getEntities(Trigger.class, Transform.class);
        this.eventGroups = entityData.getEntities(EventGroup.class);
        this.events = entityData.getEntities(Event.class, Transform.class);
        this.actors = entityData.getEntities(PlayerControlled.class, Transform.class); // players
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        eventGroups.applyChanges();
        events.applyChanges();

        if (triggers.applyChanges()) {
            updateSet(triggers);
        }

        if (actors.applyChanges()) {
            // check if they moved into a bounding volume
            for (Entity actor : actors.getChangedEntities()) {
                for (Entity trigger : triggers) {
                    if (isInsideVolume(actor.getId(), trigger.getId()) && trigger.get(Trigger.class).getTriggerType().equals(TriggerType.EnterabeTrigger)) {
                        trigger(trigger.getId());
                    }
                }
            }
        }
    }

    private void updateSet(EntitySet set) {
        for (Entity entity : set.getAddedEntities()) {
            refreshBoundingVolume(entity);
        }

        for (Entity entity : set.getChangedEntities()) {
            refreshBoundingVolume(entity);
        }
    }

    /**
     * This methods is called when the trigger entity moves.
     * We then update the center of the bounding volume
     *
     * @param entity
     */
    private void refreshBoundingVolume(Entity entity) {
        Trigger trigger = entity.get(Trigger.class);
        Transform transform = entity.get(Transform.class);
        trigger.getTriggerVolume().setCenter(transform.getTranslation());
    }

    public void onInteraction(EntityId actor, EntityId trigger) {
        if (isInsideVolume(actor, trigger)) {
            trigger(trigger);
        }
    }

    private boolean isInsideVolume(EntityId actor, EntityId trigger) {
        if (triggers.containsId(trigger) && actors.containsId(actor)) {
            Vector3f actorPosition = actors.getEntity(actor).get(Transform.class).getTranslation();
            BoundingVolume volume = triggers.getEntity(trigger).get(Trigger.class).getTriggerVolume();
            return volume.contains(actorPosition);
        }
        return false;
    }


    /**
     * Call this when the trigger entity is triggered.
     *
     * @param triggerId the entity id to trigger
     */
    private void trigger(EntityId triggerId) {
        EntityId groupEventId = triggers.getEntity(triggerId).get(Trigger.class).getEventGroupId();

        for (Entity eventEntity : events) {
            Event event = eventEntity.get(Event.class);
            if (event.getEventGroupId() == groupEventId) {
                triggerEvent(eventEntity.getId());
            }
        }
        if (eventGroups.containsId(groupEventId)) {
            EventGroup eventGroup = eventGroups.getEntity(groupEventId).get(EventGroup.class);
            if (eventGroup.getRemainingTriggerCount() == 1) {
                // remove, can't be triggered anymore
                entityData.removeEntity(groupEventId);
                removeTriggersAndEventsByGroupId(groupEventId);
            } else if (eventGroup.getRemainingTriggerCount() > 1) {
                entityData.setComponent(groupEventId, new EventGroup(eventGroup.getRemainingTriggerCount() - 1));
            }
        }

    }

    private void triggerEvent(EntityId eventId) {
        Entity eventEntity = this.events.getEntity(eventId);
        Event event = eventEntity.get(Event.class);
        EventType eventType = event.getEventType();
        Transform transform = eventEntity.get(Transform.class);

        if (eventType instanceof PlaySoundEventType) {

            PlaySoundEventType soundEvent = (PlaySoundEventType) eventType;

            EntityId sound = this.entityData.createEntity();
            entityData.setComponents(sound,
                    new SoundComponent(soundEvent.getSound(), false, soundEvent.isPositional()),
                    new Decay((long) (soundEvent.getSound().getDuration() * 1000)));

            // if this is a positional audio we need to add a Transform component
            if (soundEvent.isPositional()) {
                entityData.setComponent(sound, new Transform(transform.getTranslation(), transform.getRotation(), transform.getScale()));
            }

        } else if (eventType instanceof OpenCloseEvent) {
            EntityId targetId = ((OpenCloseEvent) eventType).getEntityId();
            Entity targetEntity = entityData.getEntity(targetId, OpenedClosedState.class);
            if (targetEntity != null && targetEntity.get(OpenedClosedState.class) != null) {
                OpenedClosedState state = targetEntity.get(OpenedClosedState.class);
                targetEntity.set(new OpenedClosedState(!state.isOpened()));
            }
        }
    }

    private void removeTriggersAndEventsByGroupId(EntityId groupEventId) {
        // remove triggers
        for (Entity triggerEntity : triggers) {
            Trigger trigger = triggerEntity.get(Trigger.class);
            if (trigger.getEventGroupId() == groupEventId) {
                entityData.removeEntity(triggerEntity.getId());
            }
        }
        // remove events
        for (Entity eventEntity : events) {
            Event event = eventEntity.get(Event.class);
            if (event.getEventGroupId() == groupEventId) {
                entityData.removeEntity(eventEntity.getId());
            }
        }
    }

    @Override
    public void cleanup() {
        this.triggers.release();
        this.triggers.clear();
        this.triggers = null;

        this.eventGroups.release();
        this.eventGroups.clear();
        this.eventGroups = null;

        this.events.release();
        this.events.clear();
        this.events = null;

        this.actors.release();
        this.actors.clear();
        this.actors = null;

        super.cleanup();
    }
}
