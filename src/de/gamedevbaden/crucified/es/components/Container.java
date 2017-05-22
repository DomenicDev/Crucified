package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * This component tags the entity with the ability to store items (entities).
 * <p>
 * This can be used for the player because he should be able to store items to his inventory.
 * <p>
 * Created by Domenic on 19.05.2017.
 */
@Serializable
public class Container implements EntityComponent {

    // todo:
    // private ContainerType <<-- tells the system what kind of items this container can store
    // private int size <<-- how many items this container can store

}
