package de.gamedevbaden.crucified.enums;

import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.es.components.FixedTransformation;
import de.gamedevbaden.crucified.es.components.MeshCollisionShape;
import de.gamedevbaden.crucified.es.components.Model;

/**
 * Created by Domenic on 25.04.2017.
 */
public enum Type {

    StaticPhysicObject(Model.class, FixedTransformation.class, MeshCollisionShape.class),
    ;

    Type(Class<? extends EntityComponent>... components ) {
        this.components = components;
    }

    private Class<? extends EntityComponent>[] components;

    public Class<? extends EntityComponent>[] getComponents() {
        return components;
    }
}
