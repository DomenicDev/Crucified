package de.gamedevbaden.crucified.enums;

/**
 * Collection of all available footstep sounds.
 * The right one is chosen depending on the surface the player walks on.
 * <p>
 * Created by Domenic on 06.06.2017.
 */
public enum FootstepSound {

    Sand("sand_footstep.WAV"),
    Stone("stone_footstep.WAV"),
    Wood("wood_footstep.WAV");

    private String soundPath;

    FootstepSound(String soundPath) {
        this.soundPath = "Sounds/Footsteps/" + soundPath;
    }

    public String getSoundPath() {
        return soundPath;
    }
}
