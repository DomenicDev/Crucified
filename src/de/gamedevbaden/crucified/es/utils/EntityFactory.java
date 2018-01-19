package de.gamedevbaden.crucified.es.utils;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.enums.SkeletonType;
import de.gamedevbaden.crucified.enums.Sound;
import de.gamedevbaden.crucified.enums.Type;
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

    public static EntityId createPlayer(EntityData entityData) {
        EntityId player = entityData.createEntity();
        entityData.setComponents(player,
                new Model(ModelType.Player),
                new Transform(new Vector3f(0, 5, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new SkeletonComponent(SkeletonType.Human),
                new FootstepEmitter(),
                new PlayerControlled(),
                new CharacterMovementState(),
                new Container());

        return player;
    }

    public static EntityId createDemon(EntityData entityData) {
        EntityId monster = entityData.createEntity();
        entityData.setComponents(monster,
                new Model(ModelType.Demon),
                new Transform(new Vector3f(0,10,0), new Quaternion(), Vector3f.UNIT_XYZ.clone()),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new SkeletonComponent(SkeletonType.Demon),
                new FootstepEmitter(),
                new PlayerControlled(),
                new CharacterMovementState()
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
