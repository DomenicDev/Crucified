package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 14.04.2017.
 */
public class PhysicsCharacterViewDirection implements EntityComponent {

    private Vector3f viewDirection;

    public PhysicsCharacterViewDirection() {
    }

    public PhysicsCharacterViewDirection(Vector3f viewDirection) {
        this.viewDirection = viewDirection;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }
}
