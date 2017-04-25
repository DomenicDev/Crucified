package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;
import de.gamedevbaden.crucified.appstates.PlayerInputAppState.InputMapping;
//import de.gamedevbaden.crucified.es.components.Physics;
import de.gamedevbaden.crucified.es.components.PlayerControlledMovement;
import de.gamedevbaden.crucified.es.components.OwnPlayer;

/**
 * Moves the
 *
 * Created by Domenic on 12.04.2017.
 */
public class PhysicsPlayerMovementAppState extends AbstractAppState {

    // there should just be one player controlled entity
    private EntitySet player;
    private Camera cam;

    private final Vector3f lastWalkDirection = new Vector3f();
    private final Vector3f walkDirection = new Vector3f();
    private final Vector3f camDirection = new Vector3f();
    private final Vector3f camLeft = new Vector3f();


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.cam = app.getCamera();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
      //  this.player = entityData.getEntities(Filters.fieldEquals(Physics.class, "type", Physics.BETTER_CHARACTER_CONTROL), OwnPlayer.class, Physics.class, PlayerControlledMovement.class);

        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {
        player.applyChanges();

        // reset walk direction
        walkDirection.set(Vector3f.ZERO);

        camDirection.set(cam.getDirection());
        camDirection.y = 0;
        camDirection.normalizeLocal();

        camLeft.set(cam.getLeft());
        camLeft.y = 0;
        camLeft.normalizeLocal();

        if (InputMapping.Forward.isPressed()) {
            walkDirection.addLocal(camDirection).multLocal(2.5f);
        }
        if (InputMapping.Backward.isPressed()) {
            walkDirection.addLocal(camDirection.negateLocal());
        }
        if (InputMapping.Left.isPressed()) {
            walkDirection.addLocal(camLeft);
        }
        if (InputMapping.Right.isPressed()) {
            walkDirection.addLocal(camLeft.negateLocal());
        }
        if (InputMapping.Shift.isPressed()) {
            walkDirection.multLocal(2);
        }
        walkDirection.setY(0);
        // apply walk direction


        if (lastWalkDirection.equals(walkDirection)) {
            return;
        }

        for (Entity entity : player) {
            entity.set(new PlayerControlledMovement(walkDirection));
        }

        lastWalkDirection.set(walkDirection);

    }
}
