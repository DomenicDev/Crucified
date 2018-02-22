package de.gamedevbaden.crucified.es.utils;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.*;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;

/**
 * This class provides static methods to create entities.
 *
 * Created by Domenic on 11.04.2017.
 */
public class EntityFactory {


    public static EntityId createSoundEffect(EntityData entityData, Sound sound, boolean positional, Vector3f location) {

        EntityId soundEffect = entityData.createEntity();
        entityData.setComponents(soundEffect,
                new SoundComponent(sound, false, positional),
                new Decay((long) sound.getDurationInMillis()));

        if (positional) {
            entityData.setComponent(soundEffect, new Transform(location, new Quaternion(), new Vector3f(1, 1, 1)));
        }

        return soundEffect;

    }

    public static EntityId createPlayer(EntityData entityData, EntityId player) {
        entityData.setComponents(player,
                new SkeletonComponent(SkeletonType.HUMAN),
                new Model(ModelType.Player),
                new Transform(new Vector3f(0, 5, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new AliveComponent(100),
                new FootstepEmitter(),
                new PlayerControlled(),
                new ExplosionImpactComponent(),
                new CharacterMovementState(),
                new CanPickupArtifactCompont(),
                new Container(),
                new ActionGroupComponent(ActionType.Scream, ActionType.ShootFireball)
        );

        return player;
    }

    public static EntityId createPlayer(EntityData entityData) {
        EntityId player = entityData.createEntity();
        return createPlayer(entityData, player);
    }

    public static EntityId createDemon(EntityData entityData) {
        EntityId monster = entityData.createEntity();
        entityData.setComponents(monster,
                new SkeletonComponent(SkeletonType.DEMON),
                new Model(ModelType.Demon),
                new Transform(new Vector3f(0, 5, 0), new Quaternion(), Vector3f.UNIT_XYZ.clone()),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new FootstepEmitter(),
                new PlayerControlled(),
                new AliveComponent(100),
                new CharacterMovementState(),
                new ActionGroupComponent(ActionType.Scream, ActionType.ShootFireball)
        );
        return monster;
    }

    public static EntityId createEntityType(EntityData entityData, Type type) {
        EntityId entityId = entityData.createEntity();

        switch (type) {
            case Campfire:
                entityData.setComponents(entityId,
                        new Model(ModelType.Campfire),
                        new PhysicsRigidBody(0, false, CollisionShapeType.BOX_COLLISION_SHAPE));
                break;
            default:
        }
        return entityId;
    }


    /**
     * Returns the default model path for the specified type
     *
     * @param type the type you want the model of
     * @return the model path for that type
     */
    public static String getModelForType(Type type) {
        switch (type) {
            case Campfire:
                return ModelType.Campfire;
            case FlashLight:
                return ModelType.Headlamp;
            case ReadablePaper:
                return ModelType.Paper;
            default:
                return null;
        }
    }

}
