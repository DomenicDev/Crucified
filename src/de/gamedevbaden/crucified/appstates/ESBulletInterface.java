package de.gamedevbaden.crucified.appstates;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jvpichowski.jme3.es.bullet.components.CustomShape;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.bullet.components.WarpPosition;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacter;
import com.jvpichowski.jme3.es.bullet.extension.character.PhysicsCharacterMovement;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.view.ModelLoaderAppState;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.physics.PhysicConstants;
import de.gamedevbaden.crucified.physics.character.kinematic.KinematicCharacter;

/**
 * This class contains the wiring between the entity data system and the ESBulletSystem.
 */
final class ESBulletInterface {

    @FunctionalInterface
    public interface HeightMapProvider {

        float[] getHeightMap(String scenePath, String terrainName);

        static HeightMapProvider of(ModelLoaderAppState modelLoader){
            return (scenePath, terrainName) -> {
                Spatial terrainModel = ((Node) modelLoader.loadModel(scenePath)).getChild(terrainName);
                if (terrainModel instanceof TerrainQuad) {
                    return ((TerrainQuad) terrainModel).getHeightMap();
                }
                return null;
            };
        }
    }

    @FunctionalInterface
    public interface CollisionShapeProvider {

        CollisionShape getCollisionShape(int shapeType, String modelPath, Vector3f scale);

        static CollisionShapeProvider of(ModelLoaderAppState modelLoader){
            return (type, modelPath, scale) -> {
                if (type == CollisionShapeType.BOX_COLLISION_SHAPE) {
                    Spatial model = modelLoader.loadModel(modelPath);
                    model.setLocalScale(scale);
                    return CollisionShapeFactory.createBoxShape(model);
                } else if (type == CollisionShapeType.MESH_COLLISION_SHAPE) {
                    Spatial model = modelLoader.loadModel(modelPath);
                    if (model != null) {
                        model.setLocalScale(scale);
                        return CollisionShapeFactory.createMeshShape(model);
                    }
                    return null;
                } else if (type == CollisionShapeType.TERRAIN_COLLISION_SHAPE) {
                    // Terrain is handled a bit differently.
                    // Terrain is not linked into the scene, but added as a part of the scene
                    // That's why we need to search for the TerrainQuad
                    Node model = (Node) modelLoader.loadModel(modelPath);
                    model.setLocalScale(scale);
                    for (Spatial s : model.getChildren()) {
                        if (s instanceof TerrainQuad) {
                            // we create the shape out of the first quad we find
                            return CollisionShapeFactory.createMeshShape(s);
                        }
                    }

                }
                return null;
            };
        }
    }

    private final EntityData entityData;
    private final EntitySet physicsPositions;
    private final HeightMapProvider heightMapProvider;
    private final CollisionShapeProvider collisionShapeProvider; //TODO use ESBullets custom collision shape system!

    private ESBulletInterface(EntityData entityData, HeightMapProvider heightMapProvider, CollisionShapeProvider collisionShapeProvider){
        this.entityData = entityData;
        this.heightMapProvider = heightMapProvider;
        this.collisionShapeProvider = collisionShapeProvider;
        this.physicsPositions = entityData.getEntities(PhysicsPosition.class);
    }

    public static ESBulletInterface of(EntityData entityData, HeightMapProvider heightMapProvider, CollisionShapeProvider collisionShapeProvider){
        return new ESBulletInterface(entityData, heightMapProvider, collisionShapeProvider);
    }

    public void close(){
        physicsPositions.release();
    }


    /**
     * Add a terrain part to the physics engine. It is described by the entity.
     *
     * @param entity needs PhysicsTerrain, Transform
     */
    public void addTerrain(Entity entity) {
        PhysicsTerrain terrain = entity.get(PhysicsTerrain.class);
        Transform transform = entity.get(Transform.class);
        float[] heightMap = heightMapProvider.getHeightMap(terrain.getScenePath(), terrain.getTerrainName());
        if(heightMap != null){
            CollisionShape terrainShape = new HeightfieldCollisionShape(heightMap, transform.getScale());
            addRigidBody(entity, true, 0, terrainShape); //false?
        }

    }

    /**
     * Remove the terrain from the physics engine.
     *
     * @param entity
     */
    public void removeTerrain(Entity entity) {
        removeRigidBody(entity);
    }


    /**
     * Add a RigidBody to the physics engine described by the entity.
     *
     * @param entity needs Transform, PhysicsRigidBody
     */
    public void addRigidBody(Entity entity){
        PhysicsRigidBody rigidBody = entity.get(PhysicsRigidBody.class);
        Transform transform = entity.get(Transform.class);
        int shapeType = entity.get(PhysicsRigidBody.class).getCollisionShapeType();
        CollisionShape collisionShape = collisionShapeProvider.getCollisionShape(shapeType, entity.get(Model.class).getPath(), transform.getScale());
        addRigidBody(entity, rigidBody.isKinematic(), rigidBody.getMass(), collisionShape);
    }

    /**
     *
     * @param entity needs Transform
     * @param kinematic
     * @param mass
     * @param shape
     */
    public void addRigidBody(Entity entity, boolean kinematic, float mass, CollisionShape shape){
        Transform transform = entity.get(Transform.class);
        entity.set(new RigidBody(kinematic, mass));
        entity.set(new CustomShape(shape));
        entity.set(new WarpPosition(transform.getTranslation(), transform.getRotation()));
    }

    /**
     *
     * @param entity needs PhysicsRigidBody, Transform
     */
    public void updateRigidBody(Entity entity){
        if (entity.get(PhysicsRigidBody.class).isKinematic()) {
            Transform transform = entity.get(Transform.class);
            entity.set(new WarpPosition(transform.getTranslation(), transform.getRotation()));
        }
    }

    /**
     * Remove a RigidBody from the physics engine
     *
     * @param entity
     */
    public void removeRigidBody(Entity entity){
        entityData.removeComponent(entity.getId(), CustomShape.class);
        entityData.removeComponent(entity.getId(), RigidBody.class);
        entityData.removeComponent(entity.getId(), WarpPosition.class);
    }

    /**
     * Adds a character to the physics engine
     *
     * @param entity needs PhysicsCharacterControl
     */
    public void addCharacter(Entity entity){
        //TODO add run mode
        PhysicsCharacterControl pcc = entity.get(PhysicsCharacterControl.class);
        Transform transform = entity.get(Transform.class);
        //basic properties of the capsule
        //PhysicsCharacter characterComp = new PhysicsCharacter(PhysicConstants.HUMAN_RADIUS, PhysicConstants.HUMAN_HEIGHT, PhysicConstants.HUMAN_WEIGHT,
        //        0.5f,0.5f, 0.2f, 0.5f, 1); //max speed, acc, stepHeight, jumpHeight, maxJumpNumber
        KinematicCharacter characterComp = new KinematicCharacter(
                PhysicConstants.HUMAN_WEIGHT,
                PhysicConstants.HUMAN_RADIUS,
                PhysicConstants.HUMAN_HEIGHT,
                0
        );
        entity.set(characterComp);

        entity.set(new PhysicsCharacterMovement(new Vector2f(pcc.getWalkDirection().x, pcc.getWalkDirection().z)));
        entity.set(new WarpPosition(transform.getTranslation(), transform.getRotation()));
    }

    public void updateCharacter(Entity entity){
        PhysicsCharacterControl pcc = entity.get(PhysicsCharacterControl.class);
        entity.set(new PhysicsCharacterMovement(new Vector2f(pcc.getWalkDirection().x, pcc.getWalkDirection().z)));
        //TODO ? characterControl.setViewDirection(pcc.getViewDirection());
    }

    public void removeCharacter(Entity entity){
        entityData.removeComponent(entity.getId(), PhysicsCharacterMovement.class);
        entityData.removeComponent(entity.getId(), KinematicCharacter.class);
        //entityData.removeComponent(entity.getId(), PhysicsCharacter.class);
    }

    public PhysicsPosition getPhysicsPosition(Entity entity){
        physicsPositions.applyChanges();
        if(physicsPositions.containsId(entity.getId())) {
            return physicsPositions.getEntity(entity.getId()).get(PhysicsPosition.class);
        }
        return null;
    }
}
