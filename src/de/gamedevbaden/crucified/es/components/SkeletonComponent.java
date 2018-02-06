package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * This component gives information about the skeleton used for animations.
 * We define this in a component because we can not just go for the Model component,
 * since there might be more than one model using the exact same skeleton (with animations)
 * but just has a different model (mesh).
 */
@Serializable
public class SkeletonComponent implements EntityComponent {

    private int skeletonType;

    public SkeletonComponent() {
    }

    public SkeletonComponent(int skeletonType) {
        this.skeletonType = skeletonType;
    }

    public int getSkeletonType() {
        return skeletonType;
    }
}
