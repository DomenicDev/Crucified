package de.gamedevbaden.crucified.es.utils;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.enums.Sound;
import de.gamedevbaden.crucified.es.components.*;

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
                new Transform(new Vector3f(0, 2, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new PlayerControlled(),
                new CharacterMovementState(),
                new Container());

        return player;
    }


}
