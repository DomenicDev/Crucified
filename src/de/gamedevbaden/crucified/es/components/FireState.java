package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * A component which stores the state of burnable entities like campfires, torches etc.
 * Another visual system will enable / disable particle effects based on the on-value.
 * Created by Domenic on 28.06.2017.
 */
@Serializable
public class FireState implements EntityComponent {

    private boolean on;

    public FireState() {
    }

    public FireState(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }
}
