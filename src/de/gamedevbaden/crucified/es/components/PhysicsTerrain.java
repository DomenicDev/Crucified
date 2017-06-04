package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 27.05.2017.
 */
@Serializable
public class PhysicsTerrain implements EntityComponent {

    private String scenePath;
    private String terrainName;

    public PhysicsTerrain() {
    }

    public PhysicsTerrain(String scenePath, String terrainName) {
        this.scenePath = scenePath;
        this.terrainName = terrainName;
    }

    public String getScenePath() {
        return scenePath;
    }

    public String getTerrainName() {
        return terrainName;
    }
}
