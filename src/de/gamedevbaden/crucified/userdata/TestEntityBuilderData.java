package de.gamedevbaden.crucified.userdata;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;

import java.io.IOException;

/**
 * Created by Domenic on 16.05.2017.
 */
public class TestEntityBuilderData implements Savable {

    private boolean attachment;
    private String attachedTo;

    private boolean physicsChar;
    private float mass;

    public boolean isAttachment() {
        return attachment;
    }

    public void setAttachment(boolean attachment) {
        this.attachment = attachment;
    }

    public String getAttachedTo() {
        return attachedTo;
    }

    public void setAttachedTo(String attachedTo) {
        this.attachedTo = attachedTo;
    }

    public boolean isPhysicsChar() {
        return physicsChar;
    }

    public void setPhysicsChar(boolean physicsChar) {
        this.physicsChar = physicsChar;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {

    }

    @Override
    public void read(JmeImporter im) throws IOException {

    }
}
