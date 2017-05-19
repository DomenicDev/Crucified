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
import de.gamedevbaden.crucified.es.utils.EntityFactory;

/**
 * Created by Domenic on 14.05.2017.
 */
public class InteractionAppState extends AbstractAppState {

    private EntitySet interactables;
    private EntityData entityData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.interactables = entityData.getEntities(InteractionComponent.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        interactables.applyChanges();
    }

    public void interactWith(EntityId interactableEntityId) {
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

                default:
                    break;

            }
        }
    }
}
