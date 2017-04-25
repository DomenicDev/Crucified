package de.gamedevbaden.crucified.es.utils.physics;

import com.jme3.math.Vector3f;

/**
 * Created by Domenic on 14.04.2017.
 */
public class BoxCollisionShape extends CollisionShapeType {

    // box attributes
   private Vector3f extent;

    public BoxCollisionShape(float width, float height, float depth) {
        this.extent = new Vector3f(width, height, depth);
    }

    public BoxCollisionShape(Vector3f extent) {
        this.extent = extent;
    }

    public Vector3f getExtent() {
        return extent;
    }
}
