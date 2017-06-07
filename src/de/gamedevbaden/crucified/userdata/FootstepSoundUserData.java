package de.gamedevbaden.crucified.userdata;

import com.jme3.export.*;
import de.gamedevbaden.crucified.enums.FootstepSound;

import java.io.IOException;

/**
 * This class containing a footstep sound can be added as user data.
 * It is later used to play the correct footstep sound depending on the
 * surface the player is walking on.
 * <p>
 * Created by Domenic on 06.06.2017.
 */
public class FootstepSoundUserData implements Savable {

    private FootstepSound footstepSound;

    public FootstepSoundUserData() {
    }

    public FootstepSound getFootstepSound() {
        return footstepSound;
    }

    public void setFootstepSound(FootstepSound footstepSound) {
        this.footstepSound = footstepSound;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(footstepSound, "sound", FootstepSound.Sand);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        footstepSound = in.readEnum("sound", FootstepSound.class, FootstepSound.Sand);
    }
}
