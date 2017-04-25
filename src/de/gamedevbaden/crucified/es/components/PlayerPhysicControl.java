package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 12.04.2017.
 */
public class PlayerPhysicControl implements EntityComponent {

    private Vector3f walkDirection;
    private Vector3f viewDirection;

    public PlayerPhysicControl() {
    }

    public PlayerPhysicControl(Vector3f walkDirection, Vector3f viewDirection) {
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
