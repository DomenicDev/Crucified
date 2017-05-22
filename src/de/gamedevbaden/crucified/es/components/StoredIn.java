package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * Created by Domenic on 19.05.2017.
 */
@Serializable
public class StoredIn implements EntityComponent {

    private EntityId container;

    public StoredIn() {
    }

    public StoredIn(EntityId container) {
        this.container = container;
    }

    public EntityId getContainer() {
        return container;
    }
}
