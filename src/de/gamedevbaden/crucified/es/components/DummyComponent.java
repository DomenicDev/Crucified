package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

@Serializable
public class DummyComponent implements EntityComponent {

    private int x;

    public DummyComponent() {
    }

    public DummyComponent(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }
}
