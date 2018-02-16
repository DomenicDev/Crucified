package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.view.ModelLoaderAppState;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.physics.CustomCharacterControl;
import de.gamedevbaden.crucified.physics.PhysicConstants;
import de.gamedevbaden.crucified.utils.GameOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>{@link PhysicAppState}</code> takes care of all physical entities.
 * Currently there is RigidBody and CharacterControl support (most things are handled with those two)
 * This state will set new {@link Transform} components when physics position and rotation changes.
 *
 * Note that Terrain is handled a little different: Even though terrain shapes are added by a RigidBodyControl
 * it uses its "own" component: PhysicsTerrain. The reason terrain has a separate component is that
 * we want be able to modify the terrain while scene composing and thus we don't want the terrain to be stored
 * in an own j3o file.
 *
 * Created by Domenic on 13.04.2017.
 */
public class PhysicAppState extends AbstractAppState {

    private EntitySet characters;
    private EntitySet rigidBodies;
    private EntitySet terrains;
    private EntitySet fireballs;
    private EntitySet nonFireballResistentEntities;
    private EntityData entityData;

    private HashMap<EntityId, CustomCharacterControl> characterControls;
    private HashMap<EntityId, RigidBodyControl> rigidBodyControls;
    private HashMap<EntityId, Integer> movingEntities; // contains all entities who are moving right now
    // the integer value is used to make some steps until the
    // entity is removed from the list

    private ArrayList<RigidBodyControl> staticPhysicalObjects;

    private FireballCollisionListener fireballListener = new FireballCollisionListener();

    private AppStateManager stateManager;
    private BulletAppState bulletAppState;
    private ModelLoaderAppState modelLoader; // might be needed to create collision shapes out of a spatial

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.modelLoader = stateManager.getState(ModelLoaderAppState.class);
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();

        this.bulletAppState = new BulletAppState();
        this.bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        this.bulletAppState.setDebugEnabled(GameOptions.ENABLE_PHYSICS_DEBUG);
        this.stateManager.attach(bulletAppState);
        this.bulletAppState.getPhysicsSpace().addCollisionListener(fireballListener);

        this.characterControls = new HashMap<>();
        this.rigidBodyControls = new HashMap<>();
        this.movingEntities = new HashMap<>();

        this.staticPhysicalObjects = new ArrayList<>();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.characters = entityData.getEntities(Model.class, PhysicsCharacterControl.class, Transform.class);
        this.rigidBodies = entityData.getEntities(Model.class, PhysicsRigidBody.class, Transform.class);
        this.terrains = entityData.getEntities(PhysicsTerrain.class, Transform.class);
        this.fireballs = entityData.getEntities(PhysicsRigidBody.class, Transform.class, Fireball.class, Model.class);
        this.nonFireballResistentEntities = entityData.getEntities(PhysicsCharacterControl.class, ExplosionImpactComponent.class, AliveComponent.class);

        // if there are already entities in the sets
        // create the physical controls for them...
        if (!characters.isEmpty()) {
            for (Entity entity : characters) {
                addCharacterControl(entity);
            }
        }

        if (!rigidBodies.isEmpty()) {
            for (Entity entity : rigidBodies) {
                addRigidBodyControl(entity);
            }
        }

        if (!terrains.isEmpty()) {
            for (Entity entity : terrains) {
                addTerrain(entity);
            }
        }

        if (!fireballs.isEmpty()) {
            for (Entity entity : fireballs) {
                fireFireball(entity);
            }
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (!bulletAppState.isInitialized()) return;


        // character controls
        if (characters.applyChanges()) {

            for (Entity entity : characters.getAddedEntities()) {
                addCharacterControl(entity);
            }

            for (Entity entity : characters.getChangedEntities()) {
                updateCharacterControl(entity);
            }

            for (Entity entity : characters.getRemovedEntities()) {
                removeCharacterControl(entity);
            }

        }

        // rigid body controls
        if (rigidBodies.applyChanges()) {

            for (Entity entity : rigidBodies.getAddedEntities()) {
                addRigidBodyControl(entity);
            }

            for (Entity entity : rigidBodies.getChangedEntities()) {
                if (entity.get(PhysicsRigidBody.class).isKinematic()) {
                    RigidBodyControl rigidBodyControl = getRigidBodyControl(entity.getId());
                    Transform transform = entity.get(Transform.class);
                    rigidBodyControl.setPhysicsLocation(transform.getTranslation());
                    rigidBodyControl.setPhysicsRotation(transform.getRotation());
                }

            }

            for (Entity entity : rigidBodies.getRemovedEntities()) {
                removeRigidBodyControl(entity);
            }

        }

        // terrains
        if (terrains.applyChanges()) {

            for (Entity entity : terrains.getAddedEntities()) {
                addTerrain(entity);
            }

            for (Entity entity : terrains.getRemovedEntities()) {
                removeTerrain(entity);
            }
        }

        nonFireballResistentEntities.applyChanges();
        if (fireballs.applyChanges()) {

            for (Entity entity : fireballs.getAddedEntities()) {
                fireFireball(entity);
            }

            for (Entity entity : fireballs.getRemovedEntities()) {
                removeFireball(entity);
            }

        }


        // apply new transforms for rigid bodies
        for (Entity entity : rigidBodies) {
            com.jme3.bullet.objects.PhysicsRigidBody rigidBody = rigidBodyControls.get(entity.getId());
            if (!entity.get(PhysicsRigidBody.class).isKinematic()) {
                Vector3f location = rigidBody.getPhysicsLocation();
                Quaternion rotation = rigidBody.getPhysicsRotation();
                Vector3f scale = entity.get(Transform.class).getScale();
                applyNewChanges(entity, location, rotation, scale);
            }
        }


        // apply new transforms for characters
        for (Entity entity : characters) {
            CustomCharacterControl characterControl = characterControls.get(entity.getId());
            Vector3f location = characterControl.getPhysicsRigidBody().getPhysicsLocation();
            Quaternion rotation = characterControl.getCharacterRotation(); //ToDo: Shall that be changed? PlayerControlled Rotation is just a thing of the view, so how could we implement this instantly
            Vector3f scale = entity.get(Transform.class).getScale();
            applyNewChanges(entity, location, rotation, scale);
        }

    }

    /**
     * This method sets if necessary a new transformation component for that entity
     * @param entity the entity the new transformation shall be applied to
     * @param location the latest physic location
     * @param rotation the latest physic rotation
     * @param scale the scale
     */
    private void applyNewChanges(Entity entity, Vector3f location, Quaternion rotation, Vector3f scale) {
        Transform currentTransform = entity.get(Transform.class);

        // we only will set a new Transform if the spatial has really changed its position, rotation or scale
        if (location.equals(currentTransform.getTranslation()) &&
                rotation.equals(currentTransform.getRotation()) &&
                scale.equals(currentTransform.getScale())) {
            if (movingEntities.containsKey(entity.getId())) {
                int tickCounter = movingEntities.get(entity.getId());
                movingEntities.put(entity.getId(), ++tickCounter);
                if (tickCounter > 30) {
                    entityData.removeComponent(entity.getId(), OnMovement.class);
                    movingEntities.remove(entity.getId());
                }

            }
            return;
        }

        // create new Transform for this entity
        entity.set(new Transform(location, rotation, scale));

        // create a marker that this entity is on movement right now
        // so clients could then interpolate between the positions for those entities
        if (!movingEntities.containsKey(entity.getId())) {
            entityData.setComponent(entity.getId(), new OnMovement());
        }
        movingEntities.put(entity.getId(), 0);

    }

    /**
     * Creates a static rigid body control of that specified object and adds it to physics space.
     * You can define which kind of shall be used for that.
     * @param object the physical object you want to add to physic space
     * @param collisionShapeType the type of the collision shape
     */
    void addStaticPhysicalObject(Spatial object, int collisionShapeType) {
        CollisionShape shape = null;
        if (collisionShapeType == CollisionShapeType.BOX_COLLISION_SHAPE) {
            shape = CollisionShapeFactory.createBoxShape(object);
        } else if (collisionShapeType == CollisionShapeType.MESH_COLLISION_SHAPE) {
            shape = CollisionShapeFactory.createMeshShape(object);
        }
        // create rigid body control and set translation and rotation
        RigidBodyControl rigidBodyControl = new RigidBodyControl(shape, 0);
        rigidBodyControl.setPhysicsLocation(object.getWorldTranslation());
        rigidBodyControl.setPhysicsRotation(object.getWorldRotation());
        // add control to physic space
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        // add rigid body control to list
        staticPhysicalObjects.add(rigidBodyControl);
    }

    /**
     * Get the {@link CustomCharacterControl} for this entity.
     *
     * @param entityId the entity id the character control is added to
     * @return the character control or null if there is none
     */
    public CustomCharacterControl getCharacterControl(EntityId entityId) {
        return characterControls.get(entityId);
    }

    /**
     * Get the {@link RigidBodyControl} for this entity.
     * @param entityId the entity id the rigid body contol is added to
     * @return the rigid body control or null if there is none.
     */
    public RigidBodyControl getRigidBodyControl(EntityId entityId) {
        return rigidBodyControls.get(entityId);
    }

    private void addTerrain(Entity entity) {
        PhysicsTerrain terrain = entity.get(PhysicsTerrain.class);
        Transform transform = entity.get(Transform.class);
        Spatial terrainModel = ((Node) modelLoader.loadModel(terrain.getScenePath())).getChild(terrain.getTerrainName());
        if (terrainModel instanceof TerrainQuad) {
            float[] heightMap = ((TerrainQuad) terrainModel).getHeightMap();
            CollisionShape terrainShape = new HeightfieldCollisionShape(heightMap, transform.getScale());
            RigidBodyControl terrainControl = new RigidBodyControl(terrainShape, 0);
            terrainControl.setPhysicsLocation(transform.getTranslation());
            bulletAppState.getPhysicsSpace().add(terrainControl);
        }

    }

    private void removeTerrain(Entity entity) {
        removeRigidBodyControl(entity);
    }

    private void addCharacterControl(Entity entity) {
        PhysicsCharacterControl pcc = entity.get(PhysicsCharacterControl.class);
        CustomCharacterControl characterControl = new CustomCharacterControl(PhysicConstants.HUMAN_RADIUS, PhysicConstants.HUMAN_HEIGHT, PhysicConstants.HUMAN_WEIGHT);
        characterControl.getPhysicsRigidBody().setPhysicsLocation(entity.get(Transform.class).getTranslation());
        characterControl.setWalkDirection(pcc.getWalkDirection());
        characterControl.setViewDirection(pcc.getViewDirection());
        addPhysicsControl(characterControl);
        characterControls.put(entity.getId(), characterControl);
    }

    private void updateCharacterControl(Entity entity) {
        CustomCharacterControl characterControl = characterControls.get(entity.getId());
        PhysicsCharacterControl pcc = entity.get(PhysicsCharacterControl.class);
        characterControl.setWalkDirection(pcc.getWalkDirection());
        characterControl.setViewDirection(pcc.getViewDirection());
    }

    private void removeCharacterControl(Entity entity) {
        CustomCharacterControl cc = characterControls.remove(entity.getId());
        removePhysicsControl(cc.getPhysicsRigidBody());
    }

    private void addRigidBodyControl(Entity entity) {
        PhysicsRigidBody rigidBody = entity.get(PhysicsRigidBody.class);
        Transform transform = entity.get(Transform.class);
        int shapeType = entity.get(PhysicsRigidBody.class).getCollisionShapeType();
        CollisionShape collisionShape = getCollisionShape(shapeType, entity.get(Model.class).getPath(), transform.getScale());
        RigidBodyControl rigidBodyControl = new RigidBodyControl(collisionShape, rigidBody.getMass());
        addPhysicsControl(rigidBodyControl);
        rigidBodyControl.setPhysicsLocation(transform.getTranslation());
        rigidBodyControl.setPhysicsRotation(transform.getRotation());
        rigidBodyControl.setKinematic(rigidBody.isKinematic());
        rigidBodyControls.put(entity.getId(), rigidBodyControl);
    }

    private void removeRigidBodyControl(Entity entity) {
        RigidBodyControl body = rigidBodyControls.remove(entity.getId());
        removePhysicsControl(body);
    }

    private void addPhysicsControl(PhysicsControl control) {
        bulletAppState.getPhysicsSpace().add(control);
    }

    private void removePhysicsControl(com.jme3.bullet.objects.PhysicsRigidBody control) {
        bulletAppState.getPhysicsSpace().remove(control);
    }

    private void fireFireball(Entity entity) {
        RigidBodyControl rigidBody = getRigidBodyControl(entity.getId());
        rigidBody.setGravity(new Vector3f(0, 0, 0));
        rigidBody.setLinearVelocity(entity.get(Fireball.class).getDirection().normalize().multLocal(10));
        fireballListener.flyingFireballs.put(entity.getId(), rigidBody);
    }

    private void removeFireball(Entity entity) {
        fireballListener.flyingFireballs.remove(entity.getId());
    }

    private class FireballCollisionListener implements PhysicsCollisionListener {

        private List<EntityId> entitiesToRemove = new ArrayList<>();
        private HashMap<EntityId, RigidBodyControl> flyingFireballs = new HashMap<>();

        @Override
        public void collision(PhysicsCollisionEvent event) {
            for (Map.Entry<EntityId, RigidBodyControl> e : flyingFireballs.entrySet()) {
                if (event.getObjectA().equals(e.getValue()) || event.getObjectB().equals(e.getValue())) {
                    entityData.removeEntity(e.getKey());
                    entitiesToRemove.add(e.getKey());

                    System.out.println(nonFireballResistentEntities.size());
                    for (Entity entity : nonFireballResistentEntities) {
                        CustomCharacterControl ccc = getCharacterControl(entity.getId());
                        com.jme3.bullet.objects.PhysicsRigidBody physicsRigidBody = ccc.getPhysicsRigidBody();
                        if (event.getObjectA().equals(physicsRigidBody) || event.getObjectB().equals(physicsRigidBody)) {
                            AliveComponent healthComp = nonFireballResistentEntities.getEntity(entity.getId()).get(AliveComponent.class);
                            int newHealth = healthComp.getHealth() - 40;
                            if (newHealth <= 0) {
                                entityData.removeComponent(entity.getId(), AliveComponent.class);
                            } else {
                                entityData.setComponent(entity.getId(), new AliveComponent(newHealth));
                            }
                            break;
                        } else {
                            // if no player was hit we create a little fire effect
                            EntityId fire = entityData.createEntity();
                            entityData.setComponents(fire,
                                    new FireState(true),
                                    new Transform(event.getPositionWorldOnA()),
                                    new Decay(20000));
                        }

                    }


                }
            }
            for (EntityId entityId : entitiesToRemove) {
                flyingFireballs.remove(entityId);
            }
            entitiesToRemove.clear();
        }
    }

    private CollisionShape getCollisionShape(int type, String modelPath, Vector3f scale) {
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

    }

    @Override
    public void cleanup() {
        for (Entity entity : characters) {
            removeCharacterControl(entity);
        }
        for (Entity entity : rigidBodies) {
            removeRigidBodyControl(entity);
        }
        this.characterControls.clear();
        this.rigidBodyControls.clear();
        this.characterControls = null;
        this.rigidBodyControls = null;

        this.characters.release();
        this.characters.clear();
        this.characters = null;

        this.rigidBodies.release();
        this.rigidBodies.clear();
        this.rigidBodies = null;

        for (RigidBodyControl body : staticPhysicalObjects) {
            body.getPhysicsSpace().remove(body);
        }
        staticPhysicalObjects.clear();

        if (stateManager.hasState(bulletAppState)) {
            stateManager.detach(bulletAppState);
        }
        this.bulletAppState = null;

        super.cleanup();
    }
}
