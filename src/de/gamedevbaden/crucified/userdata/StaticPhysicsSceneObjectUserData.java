package de.gamedevbaden.crucified.userdata;

import com.jme3.export.*;

import java.io.IOException;

/**
 * This user data is added to all models which are static and have physics.
 * Examples are trees, palms, rocks, fences, houses, etc.
 * Those scene objects are not treated as entities anymore but are still added to physics space.
 * This will make things like batching a lot easier.
 */
public class StaticPhysicsSceneObjectUserData implements Savable {

    private boolean kinematic;
    private PhysicsShapeType shapeType;

    public boolean isKinematic() {
        return kinematic;
    }

    public void setKinematic(boolean kinematic) {
        this.kinematic = kinematic;
    }

    public PhysicsShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(PhysicsShapeType shapeType) {
        this.shapeType = shapeType;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(kinematic, "kinematic", false);
        out.write(shapeType, "shapeType", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        this.kinematic = in.readBoolean("kinematic", false);
        this.shapeType = in.readEnum("shapeType", PhysicsShapeType.class, null);
    }

    public enum PhysicsShapeType {
        BoxShape,
        MeshShape,
        TerrainShape
    }
}
