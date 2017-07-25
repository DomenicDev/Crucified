package de.gamedevbaden.crucified.userdata;

import com.jme3.export.*;
import de.gamedevbaden.crucified.enums.Type;

import java.io.IOException;

/**
 * Created by Domenic on 25.04.2017.
 */
public class EntityType implements Savable {

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(type, "type", Type.DefaultModel);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        type = in.readEnum("type", Type.class, Type.DefaultModel);
    }
}
