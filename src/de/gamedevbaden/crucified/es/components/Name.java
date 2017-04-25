package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 11.04.2017.
 */
public class Name implements EntityComponent {

    private String name;

    /**
     * Do not use!
     */
    public Name() {
        // for serialization
    }

    /**
     * Initializes this component with the given name
     * @param name
     */
    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name{" +
                "name='" + name + '\'' +
                '}';
    }
}
