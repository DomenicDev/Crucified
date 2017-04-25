package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.PhysicsCharacterViewDirection;
import de.gamedevbaden.crucified.es.components.PhysicsCharacterControl;
import de.gamedevbaden.crucified.es.components.PhysicsCharacterWalkDirection;
import de.gamedevbaden.crucified.physics.CustomCharacterControl;

/**
 * This app state applies all character related behaviour like walk direction and view direction to the actual
 * physical control. The state doesn't care where the values came from.
 *
 * Created by Domenic on 14.04.2017.
 */
public class CharacterControlAppState extends AbstractAppState {

    private EntitySet charactersWithViewDirection;
    private EntitySet charactersWithWalkDirection;

    private PhysicAppState physicsAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.physicsAppState = stateManager.getState(PhysicAppState.class);

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.charactersWithViewDirection = entityData.getEntities(PhysicsCharacterControl.class, PhysicsCharacterViewDirection.class);
        this.charactersWithWalkDirection = entityData.getEntities(PhysicsCharacterControl.class, PhysicsCharacterWalkDirection.class);

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (charactersWithWalkDirection.applyChanges()) {

            for (Entity entity : charactersWithWalkDirection.getAddedEntities()) {
                applyMoveDirection(entity);
            }
            for (Entity entity : charactersWithWalkDirection.getChangedEntities()) {
                applyMoveDirection(entity);
            }

        }

        if (charactersWithViewDirection.applyChanges()) {

            for (Entity entity : charactersWithViewDirection.getAddedEntities()) {
                applyViewDirection(entity);
            }
            for (Entity entity : charactersWithViewDirection.getChangedEntities()) {
                applyViewDirection(entity);
            }

        }

    }

    private void applyMoveDirection(Entity entity) {
        CustomCharacterControl characterControl = physicsAppState.getCharacterControl(entity.getId());
        if (characterControl != null) {
            characterControl.setWalkDirection(entity.get(PhysicsCharacterWalkDirection.class).getWalkDirection());
        }
    }

    private void applyViewDirection(Entity entity) {
        CustomCharacterControl characterControl = physicsAppState.getCharacterControl(entity.getId());
        if (characterControl != null) {
            characterControl.setViewDirection(entity.get(PhysicsCharacterViewDirection.class).getViewDirection());
        }
    }

    @Override
    public void cleanup() {
        this.charactersWithViewDirection.clear();
        this.charactersWithViewDirection = null;

        this.charactersWithWalkDirection.clear();
        this.charactersWithWalkDirection = null;

        super.cleanup();
    }
}
