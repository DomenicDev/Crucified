package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;

/**
 * Created by Domenic on 13.04.2017.
 */
public class PhysicsRigidBody implements EntityComponent {

    private float mass;
    private boolean kinematic;
    private CollisionShapeType collisionShape;

    public PhysicsRigidBody() {
    }

    public PhysicsRigidBody(float mass, boolean kinematic, CollisionShapeType collisionShape) {
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

    public CollisionShapeType getCollisionShape() {
        return collisionShape;
    }
}
