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
import de.gamedevbaden.crucified.controls.NewCharacterAnimControl;
import de.gamedevbaden.crucified.enums.SkeletonType;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.es.components.SkeletonComponent;

import java.util.HashMap;

/**
 * A system which handles the player animations.
 * Created by Domenic on 29.04.2017.
 */
public class CharacterAnimationAppState extends AbstractAppState {

    private EntitySet characters;
    private HashMap<EntityId, NewCharacterAnimControl> animControls = new HashMap<>();
    private ModelViewAppState modelAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.characters = entityData.getEntities(new FieldFilter<>(SkeletonComponent.class, "skeletonType", SkeletonType.HUMAN), SkeletonComponent.class, CharacterMovementState.class);
        this.modelAppState = stateManager.getState(ModelViewAppState.class);

        if (!characters.isEmpty()) {
            for (Entity entity : characters) {
                addCharacterAnimControl(entity);
            }
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (characters.applyChanges()) {

            for (Entity entity : characters.getAddedEntities()) {
                addCharacterAnimControl(entity);
            }

            for (Entity entity : characters.getChangedEntities()) {
                updateAnimControl(entity);
            }

            for (Entity entity : characters.getRemovedEntities()) {
                removeAnimControl(entity);
            }

        }
    }

    private void addCharacterAnimControl(Entity entity) {
        Spatial playerModel = modelAppState.getSpatial(entity.getId());
        if (playerModel != null) {
            NewCharacterAnimControl animControl = new NewCharacterAnimControl();
            playerModel.addControl(animControl);
            animControls.put(entity.getId(), animControl);
            updateAnimControl(entity);
        }

    }

    private void updateAnimControl(Entity entity) {
        NewCharacterAnimControl animControl = animControls.get(entity.getId());
        if (animControl != null) {
            CharacterMovementState movementState = entity.get(CharacterMovementState.class);
            animControl.setMovementState(movementState.getMovementState());
            // ToDo; Apply equipped model
        }
    }

    private void removeAnimControl(Entity entity) {
        NewCharacterAnimControl animControl = animControls.get(entity.getId());
        if (animControl != null) {
            Spatial model = animControl.getSpatial();
            if (model != null) {
                model.removeControl(NewCharacterAnimControl.class);
            }
        }
    }

    @Override
    public void cleanup() {
        for (Entity entity : characters) {
            removeAnimControl(entity);
        }
        this.characters.release();
        this.characters.clear();
        this.characters = null;
        this.animControls.clear();
        super.cleanup();
    }
}
