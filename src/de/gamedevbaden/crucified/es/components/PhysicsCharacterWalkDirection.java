package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 14.04.2017.
 */
public class PhysicsCharacterWalkDirection implements EntityComponent {

    private Vector3f walkDirection;

    public PhysicsCharacterWalkDirection() {
    }

    public PhysicsCharacterWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }
}
