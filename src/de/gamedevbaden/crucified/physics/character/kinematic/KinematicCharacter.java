package de.gamedevbaden.crucified.physics.character.kinematic;

import com.simsilica.es.EntityComponent;

public final class KinematicCharacter implements EntityComponent {

    private final float mass;
    private final float radius;
    private final float height;
    private final float stepHeight;

    /**
     *
     * @param mass
     * @param radius
     * @param height
     * @param stepHeight
     */
    public KinematicCharacter(float mass, float radius, float height, float stepHeight) {
        this.mass = mass;
        this.radius = radius;
        this.height = height;
        this.stepHeight = stepHeight;
    }

    public float getMass() {
        return mass;
    }

    public float getRadius() {
        return radius;
    }

    public float getHeight() {
        return height;
    }

    public float getStepHeight() {
        return stepHeight;
    }
}