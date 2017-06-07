package de.gamedevbaden.crucified.appstates.sound;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.view.ModelViewAppState;
import de.gamedevbaden.crucified.controls.FootstepSoundControl;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.es.components.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds <code>{@link FootstepSoundControl} to players</code>.
 * Created by Domenic on 06.06.2017.
 */
public class FootstepSoundAppState extends AbstractAppState {

    private EntitySet players;
    private AssetManager assetManager;
    private Map<EntityId, FootstepSoundControl> controls;
    private Node gameWorld;

    private ModelViewAppState modelViewAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        this.gameWorld = ((SimpleApplication) app).getRootNode();
        this.controls = new HashMap<>();
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.players = entityData.getEntities(new FieldFilter<>(Model.class, "path", ModelType.Player), Model.class, CharacterMovementState.class);

        for (Entity entity : players) {
            addFootstepControl(entity);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (players.applyChanges()) {

            for (Entity entity : players.getAddedEntities()) {
                addFootstepControl(entity);
            }

            for (Entity entity : players.getChangedEntities()) {
                updateFootstepControl(entity);
            }

            for (Entity entity : players.getRemovedEntities()) {
                removeFootstepControl(entity);
            }

        }

    }

    private void addFootstepControl(Entity entity) {
        Spatial player = this.modelViewAppState.getSpatial(entity.getId());
        FootstepSoundControl control = new FootstepSoundControl(gameWorld, assetManager);
        control.setMovementState(entity.get(CharacterMovementState.class).getMovementState());
        player.addControl(control);
        this.controls.put(entity.getId(), control);
    }

    private void updateFootstepControl(Entity entity) {
        FootstepSoundControl control = controls.get(entity.getId());
        control.setMovementState(entity.get(CharacterMovementState.class).getMovementState());
    }

    private void removeFootstepControl(Entity entity) {
        FootstepSoundControl control = controls.remove(entity.getId());
        control.getSpatial().removeControl(control);
    }

    @Override
    public void cleanup() {
        for (Entity entity : players) {
            removeFootstepControl(entity);
        }
        this.players.release();
        this.players.clear();
        this.players = null;

        this.controls.clear();
        this.controls = null;

        super.cleanup();
    }
}
