package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.util.TempVars;
import com.jvpichowski.jme3.es.bullet.components.PhysicsPosition;
import com.jvpichowski.jme3.es.bullet.extension.logic.PhysicsSimpleLogicManager;
import com.jvpichowski.jme3.states.ESBulletState;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.view.ModelLoaderAppState;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.physics.character.kinematic.KinematicCharacterLogic;
import de.gamedevbaden.crucified.utils.GameOptions;

import java.util.HashMap;

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
    private EntityData entityData;

    private HashMap<EntityId, CustomCharacterControl> characterControls;
    private HashMap<EntityId, RigidBodyControl> rigidBodyControls;
    private HashMap<EntityId, Integer> movingEntities; // contains all entities who are moving right now
    // the integer value is used to make some steps until the
    // entity is removed from the list

    private AppStateManager stateManager;
    private BulletAppState bulletAppState;
    private ModelLoaderAppState modelLoader; // might be needed to create collision shapes out of a spatial
    private ESBulletInterface bulletInterface;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.modelLoader = stateManager.getState(ModelLoaderAppState.class);
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();

        this.bulletInterface = ESBulletInterface.of(
                entityData,
                ESBulletInterface.HeightMapProvider.of(modelLoader),
                ESBulletInterface.CollisionShapeProvider.of(modelLoader)
        );

        stateManager.attach(new ESBulletState(entityData));
        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            //add a logic manager for scripts which should be executed on the physics thread
            PhysicsSimpleLogicManager physicsLogicManager = new PhysicsSimpleLogicManager();
            physicsLogicManager.initialize(entityData, esBulletState.getBulletSystem());
            //TODO in a real application don't forget to destroy it after usage:
            //physicsLogicManager.destroy();

            // add the character logic
            //PhysicsCharacterLogic characterLogic = new PhysicsCharacterLogic();
            //characterLogic.initLogic(physicsLogicManager.getPreTickLogicManager(), esBulletState.getBulletSystem());
            //physicsLogicManager.getPreTickLogicManager().attach(characterLogic);
            KinematicCharacterLogic kinematicCharacterLogic = new KinematicCharacterLogic();
            kinematicCharacterLogic.initLogic(physicsLogicManager.getPreTickLogicManager(), entityData);
            physicsLogicManager.getPreTickLogicManager().attach(kinematicCharacterLogic);

            // if there are already entities in the sets
            // add them to the physics engine...
            characters.forEach(bulletInterface::addCharacter);
            rigidBodies.forEach(bulletInterface::addRigidBody);
            terrains.forEach(bulletInterface::addTerrain);
        });


        this.characterControls = new HashMap<>();
        this.rigidBodyControls = new HashMap<>();
        this.movingEntities = new HashMap<>();

        this.characters = entityData.getEntities(Model.class, PhysicsCharacterControl.class, Transform.class);
        this.rigidBodies = entityData.getEntities(Model.class, PhysicsRigidBody.class, Transform.class);
        this.terrains = entityData.getEntities(PhysicsTerrain.class, Transform.class);

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (!bulletAppState.isInitialized()) return;

        if(GameOptions.ENABLE_PHYSICS_DEBUG) {
            if (stateManager.getState(BulletDebugAppState.class) == null) {
                stateManager.attach(new BulletDebugAppState(stateManager.getState(ESBulletState.class).getPhysicsSpace()));
            }
        }

        // character controls
        if (characters.applyChanges()) {
            characters.getAddedEntities().forEach(bulletInterface::addCharacter);
            characters.getChangedEntities().forEach(bulletInterface::updateCharacter);
            characters.getRemovedEntities().forEach(bulletInterface::removeCharacter);
        }

        // rigid body controls
        if (rigidBodies.applyChanges()) {
            rigidBodies.getAddedEntities().forEach(bulletInterface::addRigidBody);
            rigidBodies.getChangedEntities().forEach(bulletInterface::updateRigidBody);
            rigidBodies.getRemovedEntities().forEach(bulletInterface::removeRigidBody);
        }

        // terrains
        if (terrains.applyChanges()) {
            terrains.getAddedEntities().forEach(bulletInterface::addTerrain);
            terrains.getRemovedEntities().forEach(bulletInterface::removeTerrain);
        }


        // apply new transforms for rigid bodies
        for (Entity entity : rigidBodies) {
            PhysicsPosition pos = bulletInterface.getPhysicsPosition(entity);
            if (pos != null && !entity.get(PhysicsRigidBody.class).isKinematic()) {
                Vector3f scale = entity.get(Transform.class).getScale();
                applyNewChanges(entity, pos.getLocation(), pos.getRotation(), scale);
            }
        }


        // apply new transforms for characters
        for (Entity entity : characters) {
            PhysicsPosition pos = bulletInterface.getPhysicsPosition(entity);
            if(pos != null) {
                Vector3f scale = entity.get(Transform.class).getScale();
                Quaternion rot = Quaternion.DIRECTION_Z.clone();
                Vector3f viewDirection = entity.get(PhysicsCharacterControl.class).getViewDirection();
                calculateNewForward(rot, viewDirection, Vector3f.UNIT_Y);
                applyNewChanges(entity, pos.getLocation(), rot, scale);
            }
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
        EntityId staticEntityId = entityData.createEntity();
        entityData.setComponent(staticEntityId, new Transform(object.getWorldTranslation(), object.getWorldRotation(), object.getWorldScale()));
        Entity entity = entityData.getEntity(staticEntityId, Transform.class);
        bulletInterface.addRigidBody(entity, true, 0, shape);
    }

    /**
     * This method works similar to Camera.lookAt but where lookAt sets the
     * priority on the direction, this method sets the priority on the up vector
     * so that the result direction vector and rotation is guaranteed to be
     * perpendicular to the up vector.
     *
     * @param rotation The rotation to set the result on or null to create a new
     * Quaternion, this will be set to the new "z-forward" rotation if not null
     * @param direction The direction to base the new look direction on, will be
     * set to the new direction
     * @param worldUpVector The up vector to use, the result direction will be
     * perpendicular to this
     * @return
     */
    protected final void calculateNewForward(Quaternion rotation, Vector3f direction, Vector3f worldUpVector) {
        if (direction == null) {
            return;
        }
        TempVars vars = TempVars.get();
        Vector3f newLeft = vars.vect1;
        Vector3f newLeftNegate = vars.vect2;

        newLeft.set(worldUpVector).crossLocal(direction).normalizeLocal();
        if (newLeft.equals(Vector3f.ZERO)) {
            if (direction.x != 0) {
                newLeft.set(direction.y, -direction.x, 0f).normalizeLocal();
            } else {
                newLeft.set(0f, direction.z, -direction.y).normalizeLocal();
            }
        }
        newLeftNegate.set(newLeft).negateLocal();
        direction.set(worldUpVector).crossLocal(newLeftNegate).normalizeLocal();
        if (direction.equals(Vector3f.ZERO)) {
            direction.set(Vector3f.UNIT_Z);
        }
        if (rotation != null) {
            rotation.fromAxes(newLeft, worldUpVector, direction);
        }
        vars.release();
    }

    @Override
    public void cleanup() {
        this.characters.applyChanges();
        //TODO for all because otherwise they have to be cleaned from the entity system before this cleanup method is called?
        characters.getRemovedEntities().forEach(bulletInterface::removeCharacter);
        rigidBodies.getRemovedEntities().forEach(bulletInterface::removeRigidBody);
        terrains.getRemovedEntities().forEach(bulletInterface::removeTerrain);
        this.characters.release();
        this.rigidBodies.release();
        this.characters = null;
        this.rigidBodies = null;

        bulletInterface.close();
        stateManager.detach(stateManager.getState(ESBulletState.class));
        if(stateManager.getState(BulletDebugAppState.class) != null){
            stateManager.detach(stateManager.getState(BulletDebugAppState.class));
        }

        super.cleanup();
    }



}
