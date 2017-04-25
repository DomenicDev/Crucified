package de.gamedevbaden.crucified.es.utils.physics;

import com.jme3.math.Vector3f;

/**
 * This class tells the physics system that a mesh collision shape from the given spatial shall be created.
 * Since it could be that the models is scaled you also can give the scale as parameter in the constructor.
 *
 * Created by Domenic on 14.04.2017.
 */
public class CustomMeshCollisionShape extends CollisionShapeType {

    private String modelPath;
    private Vector3f modelScale;

    /**
     * Creates the MeshCollisionShape with the default scale (1,1,1)
     * @param modelPath the path to the model
     */
    public CustomMeshCollisionShape(String modelPath) {
        this.modelPath = modelPath;
        this.modelScale = new Vector3f(1,1,1);
    }

    /**
     * Will create the MeshCollisionShape with the given scale
     * @param modelPath the path to the model
     * @param modelScale the scalation of the model, so that the shape can be adjusted
     */
    public CustomMeshCollisionShape(String modelPath, Vector3f modelScale) {
        this.modelPath = modelPath;
        this.modelScale = modelScale;
    }

    public String getModelPath() {
        return modelPath;
    }

    public Vector3f getModelScale() {
        return modelScale;
    }
}
