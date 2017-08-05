package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingVolume;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.listeners.InteractionListener;
import de.gamedevbaden.crucified.enums.Sound;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.triggersystem.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This state is responsible for all kind of trigger events.
 * Created by Domenic on 23.06.2017.
 */
public class NewTriggerAppState extends AbstractAppState implements InteractionListener {

    private EntityData entityData;
    private EntitySet eventGroups;
    private EntitySet events;
    private EntitySet triggers;
    private EntitySet actors;

    private Map<EntityId, EntityId> interactTriggers;
    private Map<EntityId, BoundingVolume> enterTriggers;
    private Map<EntityId, ArrayList<EntityId>> groupEvents;

    private DoorAppState doorAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        stateManager.getState(InteractionAppState.class).addListener(this);

        this.interactTriggers = new HashMap<>();
        this.enterTriggers = new HashMap<>();
        this.groupEvents = new HashMap<>();

        this.doorAppState = stateManager.getState(DoorAppState.class);

        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.eventGroups = entityData.getEntities(EventGroup.class);
        this.events = entityData.getEntities(Event.class);
        this.triggers = entityData.getEntities(Trigger.class);
        this.actors = entityData.getEntities(PlayerControlled.class, Transform.class);

        for (Entity entity : eventGroups) {
            addEventGroup(entity);
        }

        for (Entity entity : triggers) {
            addTrigger(entity);
        }

        for (Entity entity : events) {
            addEvent(entity);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (eventGroups.applyChanges()) {

            for (Entity entity : eventGroups.getAddedEntities()) {
                addEventGroup(entity);
            }

            for (Entity entity : eventGroups.getRemovedEntities()) {
                removeEventGroup(entity);
            }

        }

        if (events.applyChanges()) {

            for (Entity entity : events.getAddedEntities()) {
                addEvent(entity);
            }

        }

        if (triggers.applyChanges()) {

            for (Entity entity : triggers.getAddedEntities()) {
                addTrigger(entity);
            }

            for (Entity entity : triggers.getRemovedEntities()) {
                removeTrigger(entity);
            }

        }


        if (actors.applyChanges()) {

            for (Entity entity : actors.getChangedEntities()) {
                Transform transform = entity.get(Transform.class);
                // a player has moved, so we check if he
                // has walked into a bounding volume
                for (Map.Entry<EntityId, BoundingVolume> entry : enterTriggers.entrySet()) {
                    BoundingVolume volume = entry.getValue();
                    if (volume.contains(transform.getTranslation())) {
                        trigger(entry.getKey()); // player is inside --> trigger event
                    }
                }
            }

        }
    }

    private void addEventGroup(Entity entity) {
        groupEvents.put(entity.getId(), new ArrayList<>());
    }

    private void removeEventGroup(Entity entity) {
        ArrayList<EntityId> events = groupEvents.remove(entity.getId());
        events.clear();
    }

    private void addEvent(Entity entity) {
        EntityId eventGroupId = entity.get(Event.class).getEventGroupId();
        ArrayList<EntityId> events = groupEvents.get(eventGroupId);
        events.add(entity.getId());
    }

    private void addTrigger(Entity entity) {
        TriggerType type = entity.get(Trigger.class).getTriggerType();

        if (type instanceof OnEnterTrigger) {
            // get bounding volume and put it into the list
            OnEnterTrigger onEnterTrigger = (OnEnterTrigger) type;
            BoundingVolume volume = onEnterTrigger.getVolume();
            enterTriggers.put(entity.getId(), volume);
        } else if (type instanceof OnInteractionTrigger) {
            OnInteractionTrigger onInteractionTrigger = (OnInteractionTrigger) type;
            EntityId interactionEntity = onInteractionTrigger.getInteractedEntity();
            interactTriggers.put(entity.getId(), interactionEntity);
        }
    }

    private void removeTrigger(Entity entity) {
        if (enterTriggers.containsKey(entity.getId())) {
            enterTriggers.remove(entity.getId());
        }
    }

    private void trigger(EntityId triggerId) {
        // get group event id this trigger belongs to
        EntityId groupEventId = triggers.getEntity(triggerId).get(Trigger.class).getEventGroupId();

        // if group event id does not exist, do not continue
        if (!(eventGroups.containsId(groupEventId))) {
            return;
        }

        // trigger all events which belong to this group
        ArrayList<EntityId> events = groupEvents.get(groupEventId);
        for (EntityId eventId : events) {
            triggerEvent(eventId);
        }

        // update event group
        EventGroup eventGroup = eventGroups.getEntity(groupEventId).get(EventGroup.class);
        int remainingTriggerCount = eventGroup.getRemainingTriggerCount();

        if (remainingTriggerCount == 1) {
            // this was the last time this event could be triggered
            // we remove it now
            entityData.removeEntity(groupEventId);
            // also remove all events and triggers which belong to this group
            removeTriggersAndEventsByGroupId(groupEventId);
        } else if (eventGroup.getRemainingTriggerCount() > 1) {
            remainingTriggerCount--; // decrement trigger count
            entityData.setComponent(groupEventId, new EventGroup(remainingTriggerCount));
        }
    }

    private void triggerEvent(EntityId eventId) {
        if (!events.containsId(eventId)) {
            return;
        }

        Event event = events.getEntity(eventId).get(Event.class);
        EventType eventType = event.getEventType();

        if (eventType instanceof OpenCloseEvent) {
            EntityId targetId = ((OpenCloseEvent) eventType).getEntityId();
            doorAppState.changeState(targetId);
        } else if (eventType instanceof PlaySoundEventType) {
            EntityId sound = entityData.createEntity();
            entityData.setComponents(sound,
                    new SoundComponent(Sound.Miss, false, false),
                    new Decay((long) (Sound.Miss.getDurationInMillis() * 1000))
            );
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
    public void onInteract(EntityId interactedEntity) {
        for (Map.Entry<EntityId, EntityId> entry : interactTriggers.entrySet()) {
            EntityId triggerId = entry.getKey();
            EntityId e2 = entry.getValue();
            if (interactedEntity.equals(e2)) {
                trigger(triggerId);
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

        this.interactTriggers.clear();
        this.groupEvents.clear();
        this.enterTriggers.clear();
        super.cleanup();
    }


}
