package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * This component is used to add a physics rigid body for this entity.
 * For this it will take the specified parameters.
 * Created by Domenic on 13.04.2017.
 */
@Serializable
public class PhysicsRigidBody implements EntityComponent {

    private float mass;
    private boolean kinematic;
    private int collisionShape;

    public PhysicsRigidBody() {
    }

    public PhysicsRigidBody(float mass, boolean kinematic, int collisionShape) {
        this.mass = mass;
        this.kinematic = kinematic;
        this.collisionShape = collisionShape;
    }

    public float getMass() {
        return mass;
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public int getCollisionShapeType() {
        return collisionShape;
    }
}
