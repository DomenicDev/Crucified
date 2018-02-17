package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

@Serializable
public class AliveComponent implements EntityComponent {

    private int health;

    public AliveComponent() {
    }

    public AliveComponent(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
}
