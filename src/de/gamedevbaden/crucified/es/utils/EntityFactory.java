package de.gamedevbaden.crucified.es.utils;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.Sound;
import de.gamedevbaden.crucified.es.components.Decay;
import de.gamedevbaden.crucified.es.components.SoundComponent;
import de.gamedevbaden.crucified.es.components.Transform;

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


}
