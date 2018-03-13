package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * Implies that the referenced entity can be seen by the monster (for some time).
 */
@Serializable
public class CurseComponent implements EntityComponent {
    
    private EntityId cursedEntity;

    public CurseComponent() {

    }

    public CurseComponent(EntityId cursedEntity) {
        this.cursedEntity = cursedEntity;
    }

    public EntityId getCursedEntity() {
        return cursedEntity;
    }
}
