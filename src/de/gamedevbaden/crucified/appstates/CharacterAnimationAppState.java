package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.controls.CharacterAnimControl;
import de.gamedevbaden.crucified.enums.ObjectCategory;
import de.gamedevbaden.crucified.es.components.CharacterEquipmentState;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.es.components.Model;

import java.util.HashMap;

/**
 * A system which handles the player animations.
 * Created by Domenic on 29.04.2017.
 */
public class CharacterAnimationAppState extends AbstractAppState {

    private EntitySet characters;
    private HashMap<EntityId, CharacterAnimControl> animControls;
    private ModelViewAppState modelAppState;


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.characters = entityData.getEntities(new FieldFilter<>(Model.class, "category", ObjectCategory.Player), Model.class, CharacterMovementState.class, CharacterEquipmentState.class);
        this.animControls = new HashMap<>();
        this.modelAppState = stateManager.getState(ModelViewAppState.class);
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
            CharacterAnimControl animControl = new CharacterAnimControl();
            animControl.setPlayerEntity(entity);
            playerModel.addControl(animControl);
            animControls.put(entity.getId(), animControl);
        }

    }

    private void updateAnimControl(Entity entity) {
        CharacterAnimControl animControl = animControls.get(entity.getId());
        if (animControl != null) {
            animControl.setPlayerEntity(entity);
        }
    }

    private void removeAnimControl(Entity entity) {
        CharacterAnimControl animControl = animControls.get(entity.getId());
        if (animControl != null) {
            Spatial model = animControl.getSpatial();
            if (model != null) {
                model.removeControl(CharacterAnimControl.class);
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
        super.cleanup();
    }
}
