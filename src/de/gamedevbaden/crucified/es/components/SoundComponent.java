package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.Sound;

/**
 * This component is used for certain sounds.
 * Created by Domenic on 09.05.2017.
 */
@Serializable
public class SoundComponent implements EntityComponent {

    private Sound sound;
    private boolean positional; // if true the specific system will look for a Transform component
    private boolean looping;

    public SoundComponent() {
    }

    public SoundComponent(Sound sound, boolean looping, boolean positional) {
        this.sound = sound;
        this.looping = looping;
        this.positional = positional;
    }

    public Sound getSound() {
        return sound;
    }

    public boolean isLooping() {
        return looping;
    }

    public boolean isPositional() {
        return positional;
    }
}
