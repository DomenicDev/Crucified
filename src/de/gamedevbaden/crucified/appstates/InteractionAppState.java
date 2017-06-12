package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.enums.InteractionType;
import de.gamedevbaden.crucified.enums.Sound;
import de.gamedevbaden.crucified.es.components.InteractionComponent;
import de.gamedevbaden.crucified.es.components.ReadableScript;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameCommander;

/**
 * This app state defines what shall happen if players interact with various types of game objects.
 *
 * Created by Domenic on 14.05.2017.
 */
public class InteractionAppState extends AbstractAppState {

    private EntitySet interactables;
    private EntityData entityData;
    private GameCommanderHolder commanderHolder;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.commanderHolder = stateManager.getState(GameCommanderHolder.class);
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.interactables = entityData.getEntities(InteractionComponent.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        interactables.applyChanges();
    }

    public void interactWith(EntityId playerId, EntityId interactableEntityId) {
        if (interactables.containsId(interactableEntityId)) {
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
                        System.out.println(commander);
                        if (commander != null) {
                            commander.readNote(readableScript.getScript());
                        }
                    }
                    break;

                default:
                    break;

            }
        }
    }
}
