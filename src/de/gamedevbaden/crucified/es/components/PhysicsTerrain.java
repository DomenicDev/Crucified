package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * Terrain is handled differently than other physics objects.
 * Here we specify the path to the j3o file where the terrain is stored.
 * In this file the terrain can be accessed with the specified terrainName.
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
