package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.InteractionType;

/**
 * Implies what shall happen when interacted with this entity.
 * Created by Domenic on 18.05.2017.
 */
@Serializable
public class InteractionComponent implements EntityComponent {

    private InteractionType type;
    private boolean onlyOnce;

    public InteractionComponent() {
    }

    public InteractionComponent(InteractionType type) {
        this.type = type;
        this.onlyOnce = false;
    }

    public InteractionComponent(InteractionType type, boolean onlyOnce) {
        this.type = type;
        this.onlyOnce = onlyOnce;
    }

    public InteractionType getType() {
        return type;
    }

    public boolean isOnlyOnce() {
        return onlyOnce;
    }
}
