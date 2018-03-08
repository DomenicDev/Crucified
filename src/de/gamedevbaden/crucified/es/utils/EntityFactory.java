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

    public static EntityId createPlayer(EntityData entityData, EntityId player, Vector3f startPos) {
        entityData.setComponents(player,
                new SkeletonComponent(SkeletonType.HUMAN),
                new Model(ModelType.Player),
                new Transform(startPos.clone(), new Quaternion(), new Vector3f(1, 1, 1)),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new AliveComponent(100),
                new FootstepEmitter(),
                new PlayerControlled(),
                new ExplosionImpactComponent(),
                new CharacterMovementState(),
                new CanPickupArtifactCompont(),
                new Container(),
                new ActionGroupComponent() // empty
        );

        // create flashlight for player and attach it to the player
        EntityId flashlight = createFlashlight(entityData);
        entityData.setComponents(flashlight,
                new StoredIn(player),
                new EquippedBy(player)
        );

        return player;
    }

    public static EntityId createPlayer(EntityData entityData) {
        EntityId player = entityData.createEntity();
        return createPlayer(entityData, player, new Vector3f());
    }

    public static EntityId createFlashlight(EntityData entityData) {
        EntityId flashlight = entityData.createEntity();
        entityData.setComponents(flashlight,
                new Equipable(),
                new Transform(Vector3f.ZERO.clone()),
                new FlashLight(false),
                new ItemComponent(ItemType.Flashlight),
                new Model(ModelType.Headlamp)
        );
        return flashlight;
    }

    public static EntityId createDemon(EntityData entityData, EntityId monster, Vector3f startPos) {
        entityData.setComponents(monster,
                new SkeletonComponent(SkeletonType.DEMON),
                new Model(ModelType.Demon),
                new Transform(startPos.clone(), new Quaternion(), Vector3f.UNIT_XYZ.clone()),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new FootstepEmitter(),
                new PlayerControlled(),
                new CantSeeArtifactComponent(),
                new AliveComponent(100),
                new CharacterMovementState(),
                new ActionGroupComponent(ActionType.Scream, ActionType.ShootFireball)
        );
        return monster;
    }

    public static EntityId createDemon(EntityData entityData) {
        EntityId monster = entityData.createEntity();
        return createDemon(entityData, monster, new Vector3f());
    }

    public static void createArtifact(EntityData entityData, Vector3f artifactPos) {
        EntityId artifact = entityData.createEntity();
        entityData.setComponents(artifact,
                new Transform(artifactPos),
                new Pickable(),
                new ArtifactComponent(),
                new ItemComponent(ItemType.Artifact),
                new Model(ModelType.Artifact)
        );
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
