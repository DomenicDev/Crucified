package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * Implies that something was hit at the entities position.
 */
@Serializable
public class HitComponent implements EntityComponent {

    public static final int HIT_PLAYER = 0;
    public static final int HIT_GROUND = 1;

    private int type;

    public HitComponent() {}

    public HitComponent(int type) {
        this.type = type;
    }

    public int getHitType() {
        return type;
    }
}
