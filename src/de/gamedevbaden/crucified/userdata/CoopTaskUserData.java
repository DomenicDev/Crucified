package de.gamedevbaden.crucified.userdata;

import com.jme3.export.*;
import de.gamedevbaden.crucified.enums.CoopTaskType;

import java.io.IOException;

public class CoopTaskUserData implements Savable {

    private CoopTaskType type;

    public CoopTaskType getType() {
        return type;
    }

    public void setType(CoopTaskType type) {
        this.type = type;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(type, "type", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        this.type = in.readEnum("type", CoopTaskType.class, null);
    }
}
