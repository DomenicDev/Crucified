package de.gamedevbaden.crucified.userdata.triggers;

import com.jme3.export.*;

import java.io.IOException;

/**
 * Tags the spatial as trigger which is triggered
 * when the player interacts with the specified entity.
 * In here we use a name for the entity which is later "translated"
 * in the real entity object.
 * Created by Domenic on 23.06.2017.
 */
public class OnInteractionTriggerUserData implements Savable {

    private String virtualName;

    public OnInteractionTriggerUserData() {
    }

    public String getVirtualName() {
        return virtualName;
    }

    public void setVirtualName(String virtualName) {
        this.virtualName = virtualName;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(virtualName, "name", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        this.virtualName = in.readString("name", null);
    }
}
