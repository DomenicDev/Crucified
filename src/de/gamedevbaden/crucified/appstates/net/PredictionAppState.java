package de.gamedevbaden.crucified.appstates.net;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.simsilica.es.*;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.view.ModelLoaderAppState;
import de.gamedevbaden.crucified.appstates.view.ModelViewAppState;
import de.gamedevbaden.crucified.controls.HeadRotatingControl;
import de.gamedevbaden.crucified.enums.InputCommand;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.physics.CustomCharacterControl;
import de.gamedevbaden.crucified.physics.PhysicConstants;
import de.gamedevbaden.crucified.userdata.StaticPhysicsSceneObjectUserData;
import de.gamedevbaden.crucified.utils.GameConstants;
import de.gamedevbaden.crucified.utils.GameOptions;
import de.gamedevbaden.crucified.utils.PlayerInputCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This AppState tries to predict the movement of the player.
 * The player moves with a CharacterControl so we need to run local physics but we only add static
 * rigid bodies to our local physic space.
 * <p>
 * Created by Domenic on 05.05.2017.
 */
public class PredictionAppState extends AbstractAppState implements ActionListener {

    private BulletAppState bulletAppState;
    private InputManager inputManager;
    private ModelViewAppState modelViewAppState;
    private ModelLoaderAppState modelLoader;
    private Camera cam;

    private EntitySet staticRigidBodies;
    private EntitySet physicCharacters; // could be players or monsters
    private EntitySet terrains; // terrain is handles differently and not part of a model

    private HashMap<EntityId, RigidBodyControl> staticBodyControls;
    private HashMap<EntityId, CustomCharacterControl> characterControlHashMap;

    private ArrayList<RigidBodyControl> staticPhysicalObjects = new ArrayList<>();

    // attributes for own (predicted) player
    private WatchedEntity player;
    private EntityId playerId;
    private Spatial playerModel;
    private PlayerInputCollector inputCollector;
    private CustomCharacterControl playerCharacterControl;

    // this list will hold a collection of positions which are going to be needed
    // to compare those with the incoming server updates and decide whether
    // a correction is necessary or not
    private List<Vector3f> positionList;

    private Vector3f lastCamLocation = new Vector3f();
    private Vector3f lastServerLocation = new Vector3f();
    private boolean needToInterpolate;

    public PredictionAppState(EntityId playerId) {
        this.playerId = playerId;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.positionList = new ArrayList<>();

        this.staticBodyControls = new HashMap<>();
        this.characterControlHashMap = new HashMap<>();

        this.cam = app.getCamera();

        this.inputCollector = new PlayerInputCollector();

        this.bulletAppState = stateManager.getState(BulletAppState.class);
        this.bulletAppState.setDebugEnabled(GameOptions.ENABLE_PHYSICS_DEBUG);

        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);
        this.playerModel = stateManager.getState(ModelViewAppState.class).getSpatial(playerId);
        this.modelLoader = stateManager.getState(ModelLoaderAppState.class);

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.staticRigidBodies = entityData.getEntities(new FieldFilter<>(PhysicsRigidBody.class, "mass", 0f), Model.class, PhysicsRigidBody.class, Transform.class);
        this.physicCharacters = entityData.getEntities(Model.class, PhysicsCharacterControl.class, Transform.class);
        this.terrains = entityData.getEntities(PhysicsTerrain.class, Transform.class);
        this.player = entityData.watchEntity(playerId, Transform.class);

        this.playerCharacterControl = createCharacterControl();

        // register input for walk direction apply
        this.inputManager = app.getInputManager();
        for (InputCommand input : InputCommand.values()) {
            this.inputManager.addListener(this, input.name());
        }


        super.initialize(stateManager, app);
    }

    private CustomCharacterControl createCharacterControl() {
        CustomCharacterControl characterControl = new CustomCharacterControl(PhysicConstants.HUMAN_RADIUS, PhysicConstants.HUMAN_HEIGHT, PhysicConstants.HUMAN_WEIGHT);
        bulletAppState.getPhysicsSpace().add(characterControl);

        Transform transform = this.player.get(Transform.class);
        characterControl.getPhysicsRigidBody().setPhysicsLocation(transform.getTranslation());
        characterControl.getPhysicsRigidBody().setPhysicsRotation(transform.getRotation());

        return characterControl;
    }


    private void addRigidBodyControl(Entity entity) {
        PhysicsRigidBody rigidBody = entity.get(PhysicsRigidBody.class);
        Transform transform = entity.get(Transform.class);
        int shapeType = entity.get(PhysicsRigidBody.class).getCollisionShapeType();
        CollisionShape collisionShape = getCollisionShape(shapeType, entity.get(Model.class).getPath(), transform.getScale());
        RigidBodyControl rigidBodyControl = new RigidBodyControl(collisionShape, rigidBody.getMass());
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        staticBodyControls.put(entity.getId(), rigidBodyControl);
        updateRigidBodyControl(entity);
    }

    private void updateRigidBodyControl(Entity entity) {
        Transform transform = entity.get(Transform.class);
        PhysicsRigidBody rigidBody = entity.get(PhysicsRigidBody.class);
        RigidBodyControl rigidBodyControl = staticBodyControls.get(entity.getId());

        rigidBodyControl.setPhysicsLocation(transform.getTranslation());
        rigidBodyControl.setPhysicsRotation(transform.getRotation());
        rigidBodyControl.setKinematic(rigidBody.isKinematic());
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
    public void update(float tpf) {

        //----------- STATIC RIGID BODIES ---------------//

        if (staticRigidBodies.applyChanges()) {

            for (Entity entity : staticRigidBodies.getAddedEntities()) {
                addRigidBodyControl(entity);
            }

            for (Entity entity : staticRigidBodies.getChangedEntities()) {
                updateRigidBodyControl(entity);
            }

        }

        //--------------- PHYSIC CHARACTERS ---------------//

        if (physicCharacters.applyChanges()) {

            for (Entity entity : physicCharacters.getAddedEntities()) {
                // we only care for other players here
                // the own player is handled differently
                if (!entity.getId().equals(this.playerId)) {

                    CustomCharacterControl control = createCharacterControl();
                    characterControlHashMap.put(entity.getId(), control);
                    updateCharacterControl(entity);
                }
            }

            for (Entity entity : physicCharacters.getChangedEntities()) {
                if (!entity.getId().equals(this.playerId)) {
                    updateCharacterControl(entity);
                }
            }

            for (Entity entity : physicCharacters.getRemovedEntities()) {
                if (!entity.getId().equals(this.playerId)) {
                    removeCharacterControl(entity);
                }
            }

        }

        if (terrains.applyChanges()) {

            for (Entity entity : terrains.getAddedEntities()) {
                addTerrain(entity);
            }

        }


        //********** PREDICTION FOR OWN PLAYER ****************//


        if (playerModel != null) {

            // if cam direction has changed we might need to refresh the walk direction vector
            if (!lastCamLocation.equals(cam.getDirection())) {

                playerCharacterControl.setViewDirection(cam.getDirection());
                refreshWalkDirection(); // should be changed if rotating while walking
            }

            // store the current location in the global list
            Vector3f currentLocation = playerCharacterControl.getPhysicsRigidBody().getPhysicsLocation();
            positionList.add(currentLocation);


            // when we receive an update from the server we need to check whether we are still
            // in "sync" with the server or if we need to correct our position
            if (player.applyChanges()) {

                Transform serverTransform = player.get(Transform.class);
                Vector3f serverLocation = serverTransform.getTranslation();
                Vector3f closestLocalPosition = searchClosestLocalPosition(serverLocation);

                this.lastServerLocation.set(serverLocation);

                if (closestLocalPosition.distance(serverLocation) > 5f) {
                    // we directly apply the server position because of too high de-synchronization
                    playerCharacterControl.getPhysicsRigidBody().setPhysicsLocation(serverLocation);

                    // delete all local position entries
                    positionList.clear();
                    needToInterpolate = true;

                } else if (closestLocalPosition.distance(serverLocation) >= 0.1f) {
                    // slight difference we interpolate to the servers location
                    removeAllPositionEntriesUpToSuppliedVector(closestLocalPosition);
                    needToInterpolate = true;

                } else { // we are close enough, no need for correction
                    removeAllPositionEntriesUpToSuppliedVector(closestLocalPosition);
                    needToInterpolate = false;
                }
            }

            // if we are not "synced" with the server we need to correct our position
            // we do this through interpolation
            if (needToInterpolate) {

                Vector3f interpolatedVector = currentLocation.interpolateLocal(lastServerLocation, tpf / 0.1f);
                playerCharacterControl.getPhysicsRigidBody().setPhysicsLocation(interpolatedVector);

                if (interpolatedVector.distance(lastServerLocation) <= 0.001f) {
                    playerCharacterControl.getPhysicsRigidBody().setPhysicsLocation(lastServerLocation);
                    needToInterpolate = false;
                }

            }


            // apply transform to players spatial
            playerModel.setLocalTranslation(playerCharacterControl.getPhysicsRigidBody().getPhysicsLocation());
            playerModel.setLocalRotation(playerCharacterControl.getCharacterRotation());

            // set head bone rotation
            HeadRotatingControl headControl = playerModel.getControl(HeadRotatingControl.class);
            if (headControl != null) {
                headControl.setViewDirection(cam.getDirection());
            }

        } else {
            // get player model if it hasn't been initialized yet
            this.playerModel = modelViewAppState.getSpatial(playerId);
        }

    }


    private Vector3f searchClosestLocalPosition(Vector3f serverLocation) {
        if (positionList.isEmpty()) {
            return serverLocation;
        }

        Vector3f closestVector = null;
        for (Vector3f position : positionList) {
            if (closestVector == null || serverLocation.distance(position) <= serverLocation.distance(closestVector)) {
                closestVector = position;
            }
        }
        return closestVector;
    }

    private void removeAllPositionEntriesUpToSuppliedVector(Vector3f localPosition) {
        ArrayList<Vector3f> positionsToRemove = new ArrayList<>();

        for (Vector3f position : positionList) {
            positionsToRemove.add(position);
            if (localPosition == position) { // we check reference
                break;
            }
        }

        positionList.removeAll(positionsToRemove);

    }


    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        InputCommand input = InputCommand.valueOf(name);
        input.setPressed(isPressed);

        PlayerInputCollector collector = this.inputCollector;
        if (input == InputCommand.Forward) {
            collector.setForward(input.isPressed());
        } else if (input == InputCommand.Backward) {
            collector.setBackward(input.isPressed());
        } else if (input == InputCommand.Left) {
            collector.setLeft(input.isPressed());
        } else if (input == InputCommand.Right) {
            collector.setRight(input.isPressed());
        } else if (input == InputCommand.Shift) {
            collector.setRunning(input.isPressed());
        }

        refreshWalkDirection();

    }

    private void refreshWalkDirection() {
        int movementState = calculateMovementState(inputCollector);
        Vector3f walkDirection = calculateWalkDirection(movementState);
        playerCharacterControl.setWalkDirection(walkDirection);
    }


    /******** THE FOLLOWING METHODS ARE MAINLY TAKEN FROM THE "REAL" APP STATES *********/

    private int calculateMovementState(PlayerInputCollector collector) {
        if (collector.isRunning()) {
            if (collector.isForward()) {
                if (collector.isRight()) {
                    return CharacterMovementState.RUNNING_FORWARD_RIGHT;
                } else if (collector.isLeft()) {
                    return CharacterMovementState.RUNNING_FORWARD_LEFT;
                } else {
                    return CharacterMovementState.RUNNING_FORWARD;
                }
            } else if (collector.isBackward()) {
                if (collector.isLeft()) {
                    return CharacterMovementState.RUNNING_BACK_LEFT;
                } else if (collector.isRight()) {
                    return CharacterMovementState.RUNNING_BACK_RIGHT;
                } else {
                    return CharacterMovementState.RUNNING_BACK;
                }
            }
        } else { // not running
            if (collector.isForward()) {
                if (collector.isRight()) {
                    return CharacterMovementState.MOVING_FORWARD_RIGHT;
                } else if (collector.isLeft()) {
                    return CharacterMovementState.MOVING_FORWARD_LEFT;
                } else {
                    return CharacterMovementState.MOVING_FORWARD;
                }
            } else if (collector.isBackward()) {
                if (collector.isLeft()) {
                    return CharacterMovementState.MOVING_BACK_LEFT;
                } else if (collector.isRight()) {
                    return CharacterMovementState.MOVING_BACK_RIGHT;
                } else {
                    return CharacterMovementState.MOVING_BACK;
                }
            }
        }
        if (collector.isLeft()) { // side walking (no running)
            return CharacterMovementState.MOVING_LEFT;
        } else if (collector.isRight()) {
            return CharacterMovementState.MOVING_RIGHT;
        }

        // if nothing fits, the character stands still
        return CharacterMovementState.IDLE;
    }

    // copy pasted from PhysicsPlayerMovementAppState
    private Vector3f calculateWalkDirection(int movementState) {
        Vector3f viewDirection = playerCharacterControl.getViewDirection();
        viewDirection.setY(0);
        viewDirection.normalizeLocal();

        Vector3f leftDirection = viewDirection.cross(Vector3f.UNIT_Y).negate();
        leftDirection.setY(0);
        leftDirection.normalizeLocal();

        Vector3f walkDirection = new Vector3f();

        float runningMultSpeed = 3f;
        float walkingMultSpeed = 1.5f;

        switch (movementState) {
            case CharacterMovementState.IDLE:
                return walkDirection; // (0,0,0)
            case CharacterMovementState.MOVING_FORWARD:
                return walkDirection.addLocal(viewDirection).multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_FORWARD_LEFT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection).normalizeLocal().multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_FORWARD_RIGHT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection.negateLocal()).normalizeLocal().multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_BACK:
                return walkDirection.addLocal(viewDirection.negateLocal()).multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_BACK_LEFT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection).normalizeLocal().multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_BACK_RIGHT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection.negateLocal()).multLocal(walkingMultSpeed);
            case CharacterMovementState.MOVING_LEFT:
                return walkDirection.addLocal(leftDirection);
            case CharacterMovementState.MOVING_RIGHT:
                return walkDirection.addLocal(leftDirection.negateLocal());
            case CharacterMovementState.RUNNING_FORWARD:
                return walkDirection.addLocal(viewDirection).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_FORWARD_LEFT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_FORWARD_RIGHT:
                return walkDirection.addLocal(viewDirection).addLocal(leftDirection.negateLocal()).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_BACK:
                return walkDirection.addLocal(viewDirection.negateLocal()).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_BACK_LEFT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection).multLocal(runningMultSpeed);
            case CharacterMovementState.RUNNING_BACK_RIGHT:
                return walkDirection.addLocal(viewDirection.negateLocal()).addLocal(leftDirection.negateLocal()).multLocal(runningMultSpeed);
            default:
                return walkDirection;
        }
    }

    private void updateCharacterControl(Entity entity) {
        Transform transform = entity.get(Transform.class);
        CustomCharacterControl control = characterControlHashMap.get(entity.getId());
        control.getPhysicsRigidBody().setPhysicsLocation(transform.getTranslation());
    }

    private void removeCharacterControl(Entity entity) {
        CustomCharacterControl control = characterControlHashMap.remove(entity.getId());
        this.bulletAppState.getPhysicsSpace().remove(control);
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

    public void initStaticPhysicalObjects(Node gameWorld) {
        gameWorld.depthFirstTraversal(spatial -> {
            StaticPhysicsSceneObjectUserData userData;
            if ((userData = spatial.getUserData(GameConstants.USER_DATA_STATIC_PHYSICAL_OBJECT)) != null) {
                if (spatial.getParent() instanceof AssetLinkNode) spatial = spatial.getParent();
                StaticPhysicsSceneObjectUserData.PhysicsShapeType type = userData.getShapeType();
                if (type == StaticPhysicsSceneObjectUserData.PhysicsShapeType.BoxShape) {
                    addStaticPhysicalObject(spatial, CollisionShapeType.BOX_COLLISION_SHAPE);
                } else if (type == StaticPhysicsSceneObjectUserData.PhysicsShapeType.MeshShape) {
                    addStaticPhysicalObject(spatial, CollisionShapeType.MESH_COLLISION_SHAPE);
                }
            }
        });
    }

    private void addStaticPhysicalObject(Spatial object, int collisionShapeType) {
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

    @Override
    public void cleanup() {
        this.inputManager.removeListener(this);
        for (RigidBodyControl rigidBodyControl : staticBodyControls.values()) {
            if (rigidBodyControl.getPhysicsSpace() != null) {
                bulletAppState.getPhysicsSpace().remove(rigidBodyControl);
            }
        }

        for (CustomCharacterControl control : characterControlHashMap.values()) {
            this.bulletAppState.getPhysicsSpace().remove(control);
        }

        if (playerCharacterControl != null && playerCharacterControl.getPhysicsSpace() != null) {
            bulletAppState.getPhysicsSpace().remove(playerCharacterControl);
        }

        this.player.release();
        this.player = null;

        this.staticRigidBodies.release();
        this.staticRigidBodies.clear();
        this.staticRigidBodies = null;

        this.physicCharacters.release();
        this.physicCharacters.clear();
        this.physicCharacters = null;

        super.cleanup();
    }
}
