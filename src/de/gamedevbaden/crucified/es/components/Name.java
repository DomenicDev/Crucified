package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Gives the entity a name.
 * Might be used as a unique identifier to later find entities by their name instead of their id.
 */
public class Name implements EntityComponent {

    private String name;

    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
