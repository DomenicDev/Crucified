package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * This entity is equipped by another entity.
 * Created by Domenic on 20.05.2017.
 */
@Serializable
public class EquippedBy implements EntityComponent {

    private EntityId player;

    public EquippedBy() {
    }

    public EquippedBy(EntityId player) {
        this.player = player;
    }

    public EntityId getPlayer() {
        return player;
    }
}
