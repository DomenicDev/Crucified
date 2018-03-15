package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.es.components.PhysicsCharacterControl;
import de.gamedevbaden.crucified.es.components.WalkComponent;

/**
 * This AppState turns the state of {@link CharacterMovementState} into a walk direction for physic control.
 * It is also possible to set the view direction of the character control in here.
 * Created by Domenic on 01.05.2017.
 */
public class PhysicsPlayerMovementAppState extends AbstractAppState {

    private EntitySet physicalPlayers;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        // get all physical player controlled entities
        this.physicalPlayers = entityData.getEntities(PhysicsCharacterControl.class, CharacterMovementState.class, WalkComponent.class);

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (physicalPlayers.applyChanges()) {

            for (Entity entity : physicalPlayers.getAddedEntities()) {
                updateEntity(entity);
            }

            for (Entity entity : physicalPlayers.getChangedEntities()) {
                updateEntity(entity);
            }
        }
    }

    public void setViewDirection(EntityId entityId, Vector3f viewDirection) {
        if (entityId == null || viewDirection == null) {
            return;
        }
        Entity entity = physicalPlayers.getEntity(entityId);
        if (entity != null) {
            PhysicsCharacterControl characterControl = entity.get(PhysicsCharacterControl.class);
            Vector3f walkDirection = characterControl.getWalkDirection(); // we take the old walk direction
            entity.set(new PhysicsCharacterControl(walkDirection, viewDirection));
        }
    }

    private void updateEntity(Entity entity) {
        Vector3f walkDirection = calculateWalkDirection(entity);
        Vector3f viewDirection = entity.get(PhysicsCharacterControl.class).getViewDirection();
        entity.set(new PhysicsCharacterControl(walkDirection, viewDirection));
    }


    private Vector3f calculateWalkDirection(Entity entity) {
        CharacterMovementState movementState = entity.get(CharacterMovementState.class);
        PhysicsCharacterControl characterControl = entity.get(PhysicsCharacterControl.class);

        Vector3f viewDirection = characterControl.getViewDirection().clone();
        viewDirection.setY(0);
        viewDirection.normalizeLocal();

        Vector3f leftDirection = viewDirection.cross(Vector3f.UNIT_Y).negate();
        leftDirection.setY(0);
        leftDirection.normalizeLocal();

        Vector3f walkDirection = new Vector3f();

        WalkComponent walk = entity.get(WalkComponent.class);

        float runningMultSpeed = walk.getRunSpeed();
        float walkingMultSpeed = walk.getWalkSpeed();

        switch (movementState.getMovementState()) {
            case CharacterMovementState.IDLE:
                return walkDirection; // (0,0,0)
            case CharacterMovementState.MOVING_FORWARD:
                return walkDirection.addLocal(viewDirection).multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_FORWARD_LEFT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection).normalizeLocal().multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_FORWARD_RIGHT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection.negateLocal()).normalizeLocal().multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_BACK:
                return walkDirection.addLocal(viewDirection.negateLocal()).multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_BACK_LEFT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection).normalizeLocal().multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_BACK_RIGHT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection.negateLocal()).multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_LEFT:
                return walkDirection.addLocal(leftDirection);
            case CharacterMovementState.MOVING_RIGHT:
                return walkDirection.addLocal(leftDirection.negateLocal());
            case CharacterMovementState.RUNNING_FORWARD:
                return walkDirection.addLocal(viewDirection).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_FORWARD_LEFT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_FORWARD_RIGHT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection.negateLocal()).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_BACK:
                return walkDirection.addLocal(viewDirection.negateLocal()).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_BACK_LEFT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_BACK_RIGHT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection.negateLocal()).multLocal(runningMultSpeed);
            default:
                return walkDirection;
        }
    }
}
