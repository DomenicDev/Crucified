package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * A physics character control is handled by the physics system.
 * Created by Domenic on 13.04.2017.
 */
@Serializable
public class PhysicsCharacterControl implements EntityComponent {

    private Vector3f walkDirection;
    private Vector3f viewDirection;

    public PhysicsCharacterControl() {
    }

    public PhysicsCharacterControl(Vector3f walkDirection, Vector3f viewDirection) {
        this.walkDirection = walkDirection;
        this.viewDirection = viewDirection;
    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }
}
