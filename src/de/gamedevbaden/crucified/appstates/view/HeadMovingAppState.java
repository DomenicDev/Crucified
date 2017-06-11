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
import de.gamedevbaden.crucified.controls.HeadRotatingControl;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.PhysicsCharacterControl;

import java.util.HashMap;

/**
 * This state watches players with a PhysicsCharacter component.
 * It then creates another control for each player so that the
 * players head always is facing the view direction.
 * <p>
 * For more info:
 *
 * @see de.gamedevbaden.crucified.controls.HeadRotatingControl
 * <p>
 * Created by Domenic on 09.06.2017.
 */
public class HeadMovingAppState extends AbstractAppState {

    private EntitySet players;
    private HashMap<EntityId, HeadRotatingControl> controls = new HashMap<>();
    private ModelViewAppState modelViewAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.players = entityData.getEntities(new FieldFilter<>(Model.class, "path", ModelType.Player), Model.class, PhysicsCharacterControl.class);
        for (Entity entity : players) {
            addHeadControl(entity);
        }
        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {

        if (players.applyChanges()) {

            for (Entity entity : players.getAddedEntities()) {
                addHeadControl(entity);
            }

            for (Entity entity : players.getChangedEntities()) {
                updateHeadControl(entity);
            }

            for (Entity entity : players.getRemovedEntities()) {
                removeHeadControl(entity);
            }

        }

    }

    private void addHeadControl(Entity entity) {
        PhysicsCharacterControl character = entity.get(PhysicsCharacterControl.class);
        // get player model
        Spatial playerModel = modelViewAppState.getSpatial(entity.getId());
        // create and add control
        HeadRotatingControl rotatingControl = new HeadRotatingControl();
        playerModel.addControl(rotatingControl);
        // set view direction
        rotatingControl.setViewDirection(character.getViewDirection());

        controls.put(entity.getId(), rotatingControl);
    }

    private void updateHeadControl(Entity entity) {
        PhysicsCharacterControl character = entity.get(PhysicsCharacterControl.class);
        HeadRotatingControl control = controls.get(entity.getId());
        control.setViewDirection(character.getViewDirection());
    }

    private void removeHeadControl(Entity entity) {
        HeadRotatingControl control = controls.remove(entity.getId());
        control.getSpatial().removeControl(control);
    }

    @Override
    public void cleanup() {
        for (Entity entity : players) {
            removeHeadControl(entity);
        }

        this.players.release();
        this.players.clear();
        this.players = null;

        this.controls.clear();
        this.controls = null;

        super.cleanup();
    }
}
