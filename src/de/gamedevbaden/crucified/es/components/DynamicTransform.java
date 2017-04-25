package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * This component is used for entities with dynamically changing transforms.
 * Such entities are treated a little bit different (for example on client side).
 *
 * Created by Domenic on 16.04.2017.
 */
public class DynamicTransform implements EntityComponent {

    private Vector3f location;
    private Quaternion quaternion;
    private Vector3f scale;

    public DynamicTransform() {
    }

    public DynamicTransform(Vector3f location, Quaternion quaternion, Vector3f scale) {
        this.location = location;
        this.quaternion = quaternion;
        this.scale = scale;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public void setQuaternion(Quaternion quaternion) {
        this.quaternion = quaternion;
    }

    public void setScale(Vector3f scale) {
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
}
