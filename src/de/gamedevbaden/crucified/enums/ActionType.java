package de.gamedevbaden.crucified.enums;

import com.jme3.network.serializing.Serializable;

@Serializable
public enum ActionType {

    ShootFireball(3f),

    Scream(1),

    ShowPlayer(30f);

    private float delay;

    ActionType() {

    }

    ActionType(float delay) {
        this.delay = delay;
    }

    public float getDelay() {
        return delay;
    }
}
