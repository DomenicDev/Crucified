package de.gamedevbaden.crucified.es.utils;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.es.components.FixedTransformation;

/**
 * This class provides static methods to create entities.
 *
 * Created by Domenic on 11.04.2017.
 */
public class EntityFactory {


    public static EntityId createEntity(EntityData ed, EntityType type) {
        EntityId id = ed.createEntity();
        ed.setComponents(id, type.getComponents());
        return id;
    }


    public static FixedTransformation getDefaultTransformation() {
        return new FixedTransformation(new Vector3f(), new Quaternion(), new Vector3f(1,1,1));
    }

}
