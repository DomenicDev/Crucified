package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * Created by Domenic on 19.05.2017.
 */
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
