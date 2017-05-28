package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 27.05.2017.
 */
@Serializable
public class PhysicsTerrain implements EntityComponent {

    private float[] heightMap;

    public PhysicsTerrain() {
    }

    public PhysicsTerrain(float[] heightMap) {
        this.heightMap = heightMap;
    }

    public float[] getHeightMap() {
        return heightMap;
    }
}
