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

    public InteractionComponent() {
    }

    public InteractionComponent(InteractionType type) {
        this.type = type;
    }

    public InteractionType getType() {
        return type;
    }
}
