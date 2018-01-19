package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.controls.DemonAnimationControl;
import de.gamedevbaden.crucified.enums.SkeletonType;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.SkeletonComponent;

import java.util.HashMap;

public class DemonAnimationAppState extends AbstractAppState {

    private EntitySet monsters;
    private ModelViewAppState modelViewAppState;
    private HashMap<EntityId, DemonAnimationControl> controls = new HashMap<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.monsters = entityData.getEntities(new FieldFilter<>(SkeletonComponent.class, "skeletonType", SkeletonType.Demon), SkeletonComponent.class, Model.class, CharacterMovementState.class);

        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);

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
        DemonAnimationControl control = new DemonAnimationControl();
        Spatial demon = modelViewAppState.getSpatial(entity.getId());
        demon.addControl(control);
        control.setMovementState(entity.get(CharacterMovementState.class).getMovementState());
        controls.put(entity.getId(), control);
    }

    private void updateControl(Entity entity) {
        controls.get(entity.getId()).setMovementState(entity.get(CharacterMovementState.class).getMovementState());
    }

    private void removeControl(Entity entity) {
        DemonAnimationControl c = controls.remove(entity.getId());
        if (c.getSpatial() != null) {
            c.getSpatial().removeControl(c);
        }
    }

    @Override
    public void cleanup() {
        for (Entity entity : monsters) {
            removeControl(entity);
        }
        this.monsters.release();
        this.monsters.clear();
        this.monsters = null;
        super.cleanup();
    }
}
