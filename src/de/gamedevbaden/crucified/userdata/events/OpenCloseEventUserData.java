package de.gamedevbaden.crucified.userdata.events;

import com.jme3.export.*;

import java.io.IOException;

/**
 * Created by Domenic on 13.05.2017.
 */
public class OpenCloseEventUserData implements Savable {

    private String spatialName;

    public String getSpatialName() {
        return spatialName;
    }

    public void setSpatialName(String spatialName) {
        this.spatialName = spatialName;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(spatialName, "name", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        spatialName = in.readString("name", null);
    }
}
