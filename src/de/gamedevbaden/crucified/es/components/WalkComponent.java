package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

public class WalkComponent implements EntityComponent {

    private float walkSpeed;
    private float runSpeed;

    public WalkComponent() {}

    public WalkComponent(float walkSpeed, float runSpeed) {
        this.walkSpeed = walkSpeed;
        this.runSpeed = runSpeed;
    }

    public float getRunSpeed() {
        return runSpeed;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }
}
