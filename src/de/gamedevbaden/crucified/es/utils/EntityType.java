package de.gamedevbaden.crucified.es.utils;

import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 11.04.2017.
 */
public enum EntityType {

    ;
    EntityType(EntityComponent... components) {
        this.components = components;
    }

    private EntityComponent[] components;

    public EntityComponent[] getComponents() {
        return components;
    }
}
