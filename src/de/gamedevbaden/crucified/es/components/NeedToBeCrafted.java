package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.ItemType;

import java.util.Map;

/**
 * This component says that the entity still need to be crafted.
 * The map in there tells you what is needed to craft it.
 * Created by Domenic on 28.06.2017.
 */
@Serializable
public class NeedToBeCrafted implements EntityComponent {

    private Map<ItemType, Integer> neededItems;

    public NeedToBeCrafted() {
    }

    public NeedToBeCrafted(Map<ItemType, Integer> neededItems) {
        this.neededItems = neededItems;
    }

    public Map<ItemType, Integer> getNeededItems() {
        return neededItems;
    }

}
