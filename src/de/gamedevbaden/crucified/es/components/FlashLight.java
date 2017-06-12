package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * Adds a flash light functionality to the entity.
 * <p>
 * Created by Domenic on 09.06.2017.
 */
@Serializable
public class FlashLight implements EntityComponent {

    private boolean enabled;

    public FlashLight() {
    }

    public FlashLight(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
