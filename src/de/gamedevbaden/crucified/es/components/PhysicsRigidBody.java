package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 13.04.2017.
 */
public class PhysicsRigidBody implements EntityComponent {

    private float mass;
    private boolean kinematic;
    private int collisionShapeType;



    public PhysicsRigidBody() {
    }

    public PhysicsRigidBody(float mass, boolean kinematic, int collisionType) {
        this.mass = mass;
        this.kinematic = kinematic;
        this.collisionShapeType = collisionType;
    }

    public float getMass() {
        return mass;
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public int getCollisionShapeType() {
        return collisionShapeType;
    }
}
