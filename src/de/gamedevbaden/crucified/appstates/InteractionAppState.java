package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.listeners.InteractionListener;
import de.gamedevbaden.crucified.enums.InteractionType;
import de.gamedevbaden.crucified.enums.Sound;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameCommander;

import java.util.ArrayList;

/**
 * This app state defines what shall happen if players interact with various types of game objects.
 *
 * Created by Domenic on 14.05.2017.
 */
public class InteractionAppState extends AbstractAppState {

    private EntitySet interactables;
    private EntityData entityData;
    private GameCommanderHolder commanderHolder;
    private DoorAppState doorAppState;

    private ArrayList<InteractionListener> listeners = new ArrayList<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.commanderHolder = stateManager.getState(GameCommanderHolder.class);
        this.doorAppState = stateManager.getState(DoorAppState.class);
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.interactables = entityData.getEntities(InteractionComponent.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        interactables.applyChanges();
    }

    /**
     * Let the specified player interact with the specified entity.
     * @param playerId the player which interacts
     * @param interactableEntityId the entity the player wants to interact with
     */
    public void interactWith(EntityId playerId, EntityId interactableEntityId) {
        if (interactables.containsId(interactableEntityId)) {

            if (!canInteract(interactableEntityId)) {
                return;
            }

            Entity entity = interactables.getEntity(interactableEntityId);
            InteractionComponent interactionComponent = entity.get(InteractionComponent.class);
            InteractionType type = interactionComponent.getType();

            if (type == null) {
                return;
            }

            switch (type) {

                case PlayTestSound:
                    EntityFactory.createSoundEffect(entityData, Sound.Miss, false, null);
                    break;

                case ReadText:
                    // look whether this entity has an script component
                    ReadableScript readableScript = entityData.getComponent(interactableEntityId, ReadableScript.class);
                    if (readableScript != null && readableScript.getScript() != null) {
                        GameCommander commander = commanderHolder.get(playerId);
                        if (commander != null) {
                            commander.readNote(readableScript.getScript());
                        }
                    }
                    break;

                case TurnOnCampfire:
                    FireState fireState = entityData.getComponent(interactableEntityId, FireState.class);
                    if (fireState != null) {
                        entityData.setComponent(interactableEntityId, new FireState(true));
                    }
                    break;

                case OpenDoor:
                    OpenedClosedState state = entityData.getComponent(interactableEntityId, OpenedClosedState.class);
                    if (state != null) {
                        doorAppState.changeState(interactableEntityId);
                    }
                    break;

                default:
                    break;

            }

            // call listeners
            for (InteractionListener l : listeners) {
                l.onInteract(interactableEntityId);
            }

            // if this was a "one-time-only" interaction we remove the interaction component
            if (interactionComponent.isOnlyOnce()) {
                entityData.removeComponent(interactableEntityId, InteractionComponent.class);
            }
        }
    }

    public void addListener(InteractionListener listener) {
        listeners.add(listener);
    }

    /**
     * Check whether a player can interact with the specified entity.
     * This method checks things like if the specified entity does still need to be crafted.
     *
     * @param itemToInteract the entity a player wants to interact with
     * @return true if interaction is possible otherwise false
     */
    private boolean canInteract(EntityId itemToInteract) {
        if (entityData.getComponent(itemToInteract, NeedToBeCrafted.class) != null) {
            // this entity does still need to be crafted, so no interaction possible
            return false;
        }

        return true; // we are able to interact
    }

    @Override
    public void cleanup() {
        this.interactables.release();
        this.interactables.clear();
        this.interactables = null;

        listeners.clear();
        super.cleanup();
    }
}
