package de.gamedevbaden.crucified.userdata;

import com.jme3.export.*;

import java.io.IOException;

/**
 * This user data is added to nodes which contain spatial entities
 * of a certain type. With this user data the paging system will get more
 * information about how to handle this node or rather it's children.
 */
public class PagingOptionsUserData implements Savable {

    private boolean useBatching;
    private String collisionObjectName;

    public boolean isUseBatching() {
        return useBatching;
    }

    public void setUseBatching(boolean useBatching) {
        this.useBatching = useBatching;
    }

    public String getCollisionObjectName() {
        return collisionObjectName;
    }

    public void setCollisionObjectName(String collisionObjectName) {
        this.collisionObjectName = collisionObjectName;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(useBatching, "useBatching", true);
        out.write(collisionObjectName, "collisionObjectName", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        useBatching = in.readBoolean("useBatching", true);
        collisionObjectName = in.readString("collisionObjectName", null);
    }
}
