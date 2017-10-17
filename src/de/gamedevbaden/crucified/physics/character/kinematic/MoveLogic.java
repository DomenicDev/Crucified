package de.gamedevbaden.crucified.physics.character.kinematic;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.WarpVelocity;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacterMovement;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.simsilica.es.EntityData;

/**
 * Handles the movement in x-z-direction
 */
final class MoveLogic extends BaseSimpleEntityLogic {

    private EntityData entityData;

    public void initLogic(EntityData entityData){
        this.entityData = entityData;
    }

    /**
     * Register all needed components in one call with dependsOn();
     */
    @Override
    public void registerComponents() {
        dependsOn(KinematicCharacter.class, PhysicsCharacterObject.class, PhysicsCharacterMovement.class);
    }

    /**
     * This method is called every time the logic should be executed.
     */
    @Override
    public void run() {
        Vector2f dir = get(PhysicsCharacterMovement.class).getDirection();
        Vector3f linearVelocity = new Vector3f(dir.x, 0, dir.y);
        entityData.setComponent(get(PhysicsCharacterObject.class).getObject(), new WarpVelocity(linearVelocity, new Vector3f()));
    }
}
