package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 13.04.2017.
 */
public class PhysicsCharacterControl implements EntityComponent {

    private float width;
    private float height;
    private float mass;

    public PhysicsCharacterControl() {
    }

    public PhysicsCharacterControl(float width, float height, float mass) {
        this.width = width;
        this.height = height;
        this.mass = mass;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getMass() {
        return mass;
    }
}
