package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * This component has information about the position, rotation and scale of the entity in world space.
 * For parent-child relationships have a look at the {@link ChildOf} component.
 *
 * Created by Domenic on 16.04.2017.
 */
@Serializable
public class Transform implements EntityComponent {

    private Vector3f location;
    private Quaternion quaternion;
    private Vector3f scale;

    public Transform() {
    }

    public Transform(Vector3f location, Quaternion quaternion, Vector3f scale) {
        this.location = location;
        this.quaternion = quaternion;
        this.scale = scale;
    }

    public Vector3f getTranslation() {
        return location;
    }

    public Quaternion getRotation() {
        return quaternion;
    }

    public Vector3f getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "Transform{" +
                "location=" + location +
                ", quaternion=" + quaternion +
                ", scale=" + scale +
                '}';
    }
}
