package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.Type;

/**
 * Created by Domenic on 15.06.2017.
 */
@Serializable
public class ObjectType implements EntityComponent {

    private Type objectType;

    public ObjectType() {
    }

    public ObjectType(Type objectType) {
        this.objectType = objectType;
    }

    public Type getObjectType() {
        return objectType;
    }
}
