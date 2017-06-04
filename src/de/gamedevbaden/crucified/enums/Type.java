package de.gamedevbaden.crucified.enums;

/**
 * This enum contains all the different types of objects the game has.
 * Some are rather on an abstract level and others are more specific.
 *
 * <code>Type</code> is added as user data to all models.
 *
 * Created by Domenic on 25.04.2017.
 */
public enum Type {


    /**
     * Use this for models with just a Transform and a Model.
     * There is no physics and other things included,
     * only the model with its location/rotation/scale in the game world.
     * Examples: Grass
     */
    DefaultModel,

    /**
     * Use this for static physical objects with a custom collision shape.
     * Note: Collisions between mesh-accurate bodies will not be possible then!
     * Examples: Complex Tree, Buildings
     */
    StaticPhysicObjectMeshShape,

    /**
     * Use this for physical objects with a mass > 0 and also use the custom collision shape of the model.
     * Note: Collisions between mesh-accurate bodies will not be possible then!
     * Example: ???
     */
    DynamicPhysicObjectMeshShape,

    /**
     * Use this for static physical objects. They will get a BoxCollisionShape made out of the bounding boxes of the
     * model.
     * Examples: Simple Tree, Bushes
     */
    StaticPhysicsObjectBoxShape,

    /**
     * Use this for dynamic physical objects (mass > 0). They will get a BoxCollisionShape made out of the bounding
     * boxes of the model.
     * Examples: Items (such as a sword, torch, ...)
     */
    DynamicPhysicsObjectBoxShape,

    /**
     * Use this for static terrain. An optimized CollisionShape will be added for that terrain.
     */
    StaticTerrain,

    /**
     * A door one can interact with.
     */
    Door,

    /**
     *
     */
    PickupableItem,

    /**
     * All objects like trees, palms etc.
     */
    Tree,

}
