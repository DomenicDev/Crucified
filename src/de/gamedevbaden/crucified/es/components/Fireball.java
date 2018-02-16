package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

@Serializable
public class Fireball implements EntityComponent {

    private Vector3f direction;

    public Fireball() {
    }

    public Fireball(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getDirection() {
        return direction;
    }
}
