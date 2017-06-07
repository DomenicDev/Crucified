package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.bounding.BoundingVolume;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.InteractionType;
import de.gamedevbaden.crucified.enums.Scene;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.triggersystem.OpenCloseEvent;
import de.gamedevbaden.crucified.es.triggersystem.PlaySoundEventType;
import de.gamedevbaden.crucified.es.triggersystem.TriggerType;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.userdata.EntityType;
import de.gamedevbaden.crucified.userdata.ReadablePaperScriptUserData;
import de.gamedevbaden.crucified.userdata.eventgroup.EventGroupData;
import de.gamedevbaden.crucified.userdata.events.OpenCloseEventUserData;
import de.gamedevbaden.crucified.userdata.events.SoundEvent;
import de.gamedevbaden.crucified.userdata.triggers.TriggerTypeData;
import de.gamedevbaden.crucified.utils.GameConstants;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This class is used to create entities from a scene.
 * For that it looks for spatials with the user data "type" on them and then creates the right entity object out of
 * that visual representation.
 * <p>
 * Created by Domenic on 25.04.2017.
 */
public class SceneEntityLoader extends AbstractAppState {

    private static final String TEST_SCENE = "Scenes/TestScene.j3o";
    private static final String BEACH_SCENE = "Scenes/IslandVersion1.j3o";
    public static Scene sceneToLoad = Scene.BeachScene;
    private static Logger log = Logger.getLogger(SceneEntityLoader.class.getName());
    private EntityData entityData;
    private AppStateManager stateManager;
    private AssetManager assetManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.assetManager = app.getAssetManager();

        createEntitiesFromScene(sceneToLoad);
    }

    /**
     * This method searches the supplied scene for spatials with the user data "type" on them
     * and will create the specific entity object for that model.
     *
     * @param scene the scene which shall be searched for entities
     */
    public void createEntitiesFromScene(Scene scene) {
        // The initialization is done in several steps:

        Node gameWorld = (Node) assetManager.loadModel(sceneToLoad.getScenePath());

        // 1. Create a HashMap to store the reference of a spatial (with entity data)
        // this map is filled when calling initEntities()
        HashMap<Spatial, EntityId> spatialEntities = new HashMap<>();

        initTerrain(scene, gameWorld);

        // 2. Search for entities in the scene graph and create "real" entity objects
        initEntities(gameWorld, spatialEntities);

        // 3. Search for trigger entities
        // because trigger might depend on other entities we initialize the triggers after the "normal" entities
        initTriggers(gameWorld, spatialEntities);
    }

    /**
     * Terrain is handled a little differently here.
     * The level designer wants to edit terrain directly in the scene and doesn't want the terrain stored
     * in a separate file. This makes composing a scene much easier.
     *
     * @param rootNode
     */
    private void initTerrain(Scene scene, Node rootNode) {
        for (Spatial spatial : rootNode.getChildren()) {
            if (spatial instanceof TerrainQuad) {
                TerrainQuad terrainQuad = (TerrainQuad) spatial;

                EntityId terrain = entityData.createEntity();
                entityData.setComponents(terrain,
                        createTransform(spatial),
                        new PhysicsTerrain(scene.getScenePath(), spatial.getName()));

                break; // we only create one terrain entity

                // Note: We don't add a Model component here, because then the hole level would be loaded
                // on client side since the terrain is directly added to the scene.
            }
        }
    }

    private void initEntities(Node rootNode, HashMap<Spatial, EntityId> entities) {
        rootNode.depthFirstTraversal(spatial -> {

            EntityType t = spatial.getUserData("type");
            if (t != null) {

                EntityId entityId = entityData.createEntity();

                // NOTE: WE ADD THE MODEL COMPONENT HERE !!! No need to add it in switch case later
                // all models should have been added with an AssetLinkNode
                // with that AssetLinkNode we can get the origin of the model ( = model path )
                if (spatial.getParent() instanceof AssetLinkNode) {
                    ModelKey key = ((AssetLinkNode) spatial.getParent()).getAssetLoaderKeys().get(0);
                    String path = key.getName();
                    //      ModelType modelType = ModelType.getModelType(key.getName());
                    //    if (modelType == null)
                    //      log.log(Level.SEVERE, "The model type for " + key.getName() + " has not been added yet!");
                    //    entityData.setComponent(entityId, new Model(ModelType.getModelType(key.getName())));
                    entityData.setComponent(entityId, new Model(path));
                }

                // we also add the transform component to the entity
                entityData.setComponent(entityId, createTransform(spatial));

                switch (t.getType()) {

//                    case DefaultModel:
//                        entityData.setComponents(entityId,
//                                createTransform(spatial));
//                        break;

                    case StaticPhysicObjectMeshShape:
                        entityData.setComponents(entityId,
                                new PhysicsRigidBody(0, false, CollisionShapeType.MESH_COLLISION_SHAPE));
                        break;

                    case DynamicPhysicObjectMeshShape:
                        entityData.setComponents(entityId,
                                new PhysicsRigidBody(10, false, CollisionShapeType.MESH_COLLISION_SHAPE));
                        break;

                    case StaticPhysicsObjectBoxShape:
                        entityData.setComponents(entityId,
                                new PhysicsRigidBody(0, false, CollisionShapeType.BOX_COLLISION_SHAPE));
                        break;

                    case DynamicPhysicsObjectBoxShape:
                        entityData.setComponents(entityId,
                                new PhysicsRigidBody(10, false, CollisionShapeType.BOX_COLLISION_SHAPE));
                        break;

                    case StaticTerrain:
                        entityData.setComponents(entityId,
                                createTransform(spatial),
                                new PhysicsRigidBody(0, false, CollisionShapeType.TERRAIN_COLLISION_SHAPE));
                        break;

                    case Door:
                        entityData.setComponents(entityId,
                                new PhysicsRigidBody(0, true, CollisionShapeType.BOX_COLLISION_SHAPE),
                                new InteractionComponent(InteractionType.PlayTestSound));

                        break;
                    case PickupableItem:
                        entityData.setComponents(entityId,
                                new Pickable(),
                                new Equipable())
                        ;
                        break;

                    case Tree:
                        entityData.setComponents(entityId,
                                new PhysicsRigidBody(0, false, CollisionShapeType.MESH_COLLISION_SHAPE));
                        break;

                    case ReadablePaper:
                        // get script
                        ReadablePaperScriptUserData script = spatial.getUserData(GameConstants.USER_DATA_READABLE_SCRIPT);
                        if (script != null) {
                            entityData.setComponents(entityId,
                                    new InteractionComponent(InteractionType.ReadText),
                                    new ReadableScript(script.getScript())
                            );
                        }
                        break;
                    default:
                        break;

                }

                entities.put(spatial, entityId);

            }

        });
    }

    private void initTriggers(Node scene, HashMap<Spatial, EntityId> spatialEntities) {
        scene.depthFirstTraversal(spatial -> {
            //----------- TRIGGERS AND EVENTS --------------------------//

            EventGroupData eventGroupData = spatial.getUserData("eventgroup");
            if (eventGroupData != null && spatial instanceof Node) {

                EntityId eventGroup = entityData.createEntity();
                entityData.setComponent(eventGroup, new EventGroup(eventGroupData.getAmountOfTriggers()));

                System.out.println("created entity group");

                // look for triggers and events
                spatial.depthFirstTraversal(spatial1 -> {

                    if (spatial1.getUserData("trigger") != null) {
                        TriggerTypeData triggerData = spatial1.getUserData("trigger");
                        BoundingVolume volume = spatial1.getWorldBound();
                        TriggerType triggerType = triggerData.getTriggerType();

                        EntityId trigger = entityData.createEntity();
                        entityData.setComponents(trigger,
                                createTransform(spatial1),
                                new Trigger(eventGroup, triggerType, volume)
                        );


                    } else if (spatial1.getUserData("event") != null) {

                        Object event = spatial1.getUserData("event");

                        if (event instanceof SoundEvent) {
                            SoundEvent soundEvent = (SoundEvent) event;

                            System.out.println("created sound event");

                            EntityId eventEntity = entityData.createEntity();
                            entityData.setComponents(eventEntity,
                                    new Event(eventGroup, new PlaySoundEventType(soundEvent.getSound(), soundEvent.isPositional())),
                                    createTransform(spatial1));

                        } else if (event instanceof OpenCloseEventUserData) {
                            OpenCloseEventUserData openCloseEvent = (OpenCloseEventUserData) event;
                            String spatialName = openCloseEvent.getSpatialName();
                            Spatial target = ((Node) spatial).getChild(spatialName);
                            EntityId targetId = spatialEntities.get(target);

                            EntityId eventEntity = entityData.createEntity();
                            entityData.setComponents(eventEntity,
                                    new Event(eventGroup, new OpenCloseEvent(targetId)),
                                    createTransform(spatial));
                        }
                    }
                });
            }
        });
    }

    private Transform createTransform(Spatial spatial) {
        return new Transform(spatial.getWorldTranslation(), spatial.getWorldRotation(), spatial.getWorldScale());
    }


}
