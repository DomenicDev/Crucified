package de.gamedevbaden.crucified.userdata.events;

import com.jme3.export.*;
import de.gamedevbaden.crucified.enums.Sound;

import java.io.IOException;

/**
 * Created by Domenic on 11.05.2017.
 */
public class SoundEvent implements Savable {

    private Sound sound;
    private boolean positional;

    public SoundEvent() {
    }

    public SoundEvent(Sound sound, boolean positional) {
        this.sound = sound;
        this.positional = positional;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public boolean isPositional() {
        return positional;
    }

    public void setPositional(boolean positional) {
        this.positional = positional;
    }


    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(sound, "sound", null);
        out.write(positional, "positional", false);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        sound = in.readEnum("sound", Sound.class, null);
        positional = in.readBoolean("positional", false);
    }


}
