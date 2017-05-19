package de.gamedevbaden.crucified.es.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * Created by Domenic on 12.05.2017.
 */
public class ChildOf implements EntityComponent {

    private EntityId parentId;
    private Vector3f offsetTranslation;
    private Quaternion offsetRotation;

    public ChildOf() {
    }

    public ChildOf(EntityId parentId) {
        this.parentId = parentId;
        this.offsetTranslation = new Vector3f(0, 0, 0);
        this.offsetRotation = new Quaternion();
    }

    public ChildOf(EntityId parentId, Vector3f offsetTranslation) {
        this.parentId = parentId;
        this.offsetTranslation = offsetTranslation;
        this.offsetRotation = new Quaternion();
    }

    public ChildOf(EntityId parentId, Vector3f offsetTranslation, Quaternion offsetRotation) {
        this.parentId = parentId;
        this.offsetTranslation = offsetTranslation;
        this.offsetRotation = offsetRotation;
    }

    public EntityId getParentId() {
        return parentId;
    }

    public Vector3f getOffsetTranslation() {
        return offsetTranslation;
    }

    public Quaternion getOffsetRotation() {
        return offsetRotation;
    }
}
