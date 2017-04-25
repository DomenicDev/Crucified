package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 11.04.2017.
 */
public class FixedTransformation implements EntityComponent {

    private Vector3f translation;
    private Quaternion rotation;
    private Vector3f scale;

    /**
     * Do not use
     */
    public FixedTransformation() {}

    public FixedTransformation(Vector3f translation, Quaternion rotation, Vector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }


    @Override
    public String toString() {
        return "FixedTransformation{" +
                "translation=" + translation +
                ", rotation=" + rotation +
                ", scale=" + scale +
                '}';
    }
}
