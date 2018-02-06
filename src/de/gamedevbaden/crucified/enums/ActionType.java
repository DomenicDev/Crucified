package de.gamedevbaden.crucified.enums;

import com.jme3.network.serializing.Serializable;

@Serializable
public enum ActionType {

    Attack(3f),

    Scream(1);

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
