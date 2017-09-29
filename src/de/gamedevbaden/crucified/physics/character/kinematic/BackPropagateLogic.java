package de.gamedevbaden.crucified.physics.character.kinematic;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.simsilica.es.EntityData;

/**
 * The physics side of the character logic. It propagates the position back to the entity system
 * and handles the height.
 */
final class BackPropagateLogic extends BaseSimpleEntityLogic{

    private EntityData entityData;

    public void initLogic(EntityData entityData){
        this.entityData = entityData;
    }

    /**
     * Register all needed components in one call with dependsOn();
     */
    @Override
    public void registerComponents() {
        dependsOn(ESCharacter.class, PhysicsPosition.class);
    }

    /**
     * This method is called every time the logic should be executed.
     */
    @Override
    public void run() {
        super.run();
        Vector3f pos = get(PhysicsPosition.class).getLocation();
        //TODO move stepheight to ESCharacter
        float stepHeight = entityData.getEntity(get(ESCharacter.class).getCharacter(), KinematicCharacter.class).get(KinematicCharacter.class).getStepHeight();
        pos.subtractLocal(0, stepHeight,0);
        entityData.setComponent(get(ESCharacter.class).getCharacter(), new PhysicsPosition(pos, Quaternion.DIRECTION_Z));
    }
}
