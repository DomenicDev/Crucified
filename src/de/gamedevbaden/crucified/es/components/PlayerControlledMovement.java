package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * This class only works for real players.
 *
 * Created by Domenic on 12.04.2017.
 */
public class PlayerControlledMovement implements EntityComponent {

    private Vector3f moveDirection;

    public PlayerControlledMovement() {}

    public PlayerControlledMovement(Vector3f moveDirection) {
        this.moveDirection = moveDirection;
    }

    public Vector3f getMoveDirection() {
        return moveDirection;
    }
}
