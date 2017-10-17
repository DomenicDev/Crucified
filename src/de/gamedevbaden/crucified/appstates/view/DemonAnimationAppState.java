package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.enums.SkeletonType;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.SkeletonComponent;

public class DemonAnimationAppState extends AbstractAppState {

    private EntitySet monsters;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.monsters = entityData.getEntities(new FieldFilter<>(SkeletonComponent.class, "skeletonType", SkeletonType.Demon), SkeletonComponent.class, Model.class, CharacterMovementState.class);

        for (Entity entity : monsters) {
            addControl(entity);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (monsters.applyChanges()) {

            for (Entity entity : monsters.getAddedEntities()) {
                addControl(entity);
            }

            for (Entity entity : monsters.getChangedEntities()) {
                updateControl(entity);
            }

            for (Entity entity : monsters.getRemovedEntities()) {
                removeControl(entity);
            }

        }

    }

    private void addControl(Entity entity) {

    }

    private void updateControl(Entity entity) {

    }

    private void removeControl(Entity entity) {

    }
}
