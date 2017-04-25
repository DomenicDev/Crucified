package de.gamedevbaden.crucified.userdata;

import com.jme3.export.*;
import de.gamedevbaden.crucified.enums.Models;
import de.gamedevbaden.crucified.enums.Type;

import java.io.IOException;

/**
 * Created by Domenic on 25.04.2017.
 */
public class EntityType implements Savable {

    private Type type;
    private String test;
    private Models model;


    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setModel(Models model) {
        this.model = model;
    }

    public Models getModel() {
        return model;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(model, "model", null);
        out.write(type, "type", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        model = in.readEnum("model", Models.class, null);
        type = in.readEnum("type", Type.class, null);
    }
}
