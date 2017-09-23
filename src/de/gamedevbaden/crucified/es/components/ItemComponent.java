package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.ItemType;

/**
 * This component is used by systems such as the Crafting system or the inventory state.
 * This class contains the type of the entity e.g. wood.
 */
@Serializable
public class ItemComponent implements EntityComponent {

    private ItemType itemType;

    public ItemComponent() {
    }

    public ItemComponent(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
