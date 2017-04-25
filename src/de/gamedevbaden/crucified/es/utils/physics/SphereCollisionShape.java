package de.gamedevbaden.crucified.es.utils.physics;

/**
 * Created by Domenic on 14.04.2017.
 */
public class SphereCollisionShape extends CollisionShapeType {

    private float radius;

    public SphereCollisionShape(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}
