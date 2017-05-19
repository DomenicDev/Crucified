package de.gamedevbaden.crucified.es.triggersystem;

import de.gamedevbaden.crucified.enums.Sound;

/**
 * Created by Domenic on 10.05.2017.
 */
public class PlaySoundEventType implements EventType {

    private Sound sound;
    private boolean positional;

    public PlaySoundEventType() {
    }

    public PlaySoundEventType(Sound sound, boolean positional) {
        this.sound = sound;
        this.positional = positional;
    }

    public Sound getSound() {
        return sound;
    }

    public boolean isPositional() {
        return positional;
    }
}
