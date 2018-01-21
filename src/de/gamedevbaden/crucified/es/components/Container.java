package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.ItemType;

/**
 * This component tags the entity with the ability to store items (entities).
 * <p>
 * This can be used for the player because he should be able to store items to his inventory.
 * <p>
 * Created by Domenic on 19.05.2017.
 */
@Serializable
public class Container implements EntityComponent {

    private int capacity;
    private ItemType typeToStore;

    /**
     * This container will have unlimited space.
     */
    public Container() {
        this.capacity = -1;
        this.typeToStore = null;
    }

    /**
     * This container will be able to hold
     * the specified amount of entities.
     * It is also possible to specify the type of item
     * this container can hold.
     *
     * @param capacity    specifies how many entities this container can hold
     * @param typeToStore the type of item this container can hold.
     */
    public Container(int capacity, ItemType typeToStore) {
        this.capacity = capacity;
        this.typeToStore = typeToStore;
    }

    public int getCapacity() {
        return capacity;
    }

    public ItemType getTypeToStore() {
        return typeToStore;
    }
}
