package de.gamedevbaden.crucified.physics.character.kinematic;

import com.jvpichowski.jme3.es.bullet.components.WarpPosition;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.simsilica.es.EntityData;

/**
 * Forwards warping to the physics object
 */
final class WarpLogic extends BaseSimpleEntityLogic {

    private EntityData entityData;

    public void initLogic(EntityData entityData){
        this.entityData = entityData;
    }

    /**
     * Register all needed components in one call with dependsOn();
     */
    @Override
    public void registerComponents() {
        dependsOn(KinematicCharacter.class, WarpPosition.class, PhysicsCharacterObject.class);
    }

    /**
     * This method is called if a new entity is created which fits to the needed components
     */
    @Override
    public void init() {
        super.init();
        WarpPosition pos = get(WarpPosition.class);
        float stepHeight = get(KinematicCharacter.class).getStepHeight();
        entityData.setComponents(get(PhysicsCharacterObject.class).getObject(),
                new WarpPosition(pos.getLocation().add(0, stepHeight,0), pos.getRotation()));
        entityData.removeComponent(getId(), WarpPosition.class);
        KinematicCharacterLogic.LOGGER.info("Warped character: "+getId());
    }
}
