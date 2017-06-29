package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.Type;

import java.util.Map;

/**
 * This component says that the entity still need to be crafted.
 * The map in there tells you what is needed to craft it.
 * Created by Domenic on 28.06.2017.
 */
@Serializable
public class NeedToBeCrafted implements EntityComponent {

    private Type resultType;
    private Map<Type, Integer> neededItems;

    public NeedToBeCrafted() {

    }

    public NeedToBeCrafted(Type resultType, Map<Type, Integer> neededItems) {
        this.resultType = resultType;
        this.neededItems = neededItems;
    }

    public Map<Type, Integer> getNeededItems() {
        return neededItems;
    }

    public Type getResultType() {
        return resultType;
    }
}
