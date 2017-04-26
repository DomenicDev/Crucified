package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.es.components.DynamicTransform;
import de.gamedevbaden.crucified.es.components.FixedTransformation;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.PhysicsRigidBody;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.userdata.EntityType;

/**
 * This class is used to create entities from a scene.
 * For that it looks for spatials with the user data "type" on them and then creates the right entity object out of
 * that visual representation.
 * <p>
 * Created by Domenic on 25.04.2017.
 */
public class SceneEntityLoader extends AbstractAppState {

    private EntityData entityData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        createEntitiesFromScene((Node) app.getAssetManager().loadModel("Scenes/TestScene.j3o"));
    }

    /**
     * This method searches the supplied scene for spatials with the user data "type" on them
     * and will create the specific entity object for that model.
     *
     * @param scene
     */
    public void createEntitiesFromScene(Node scene) {
        scene.depthFirstTraversal(spatial -> {

            if (spatial instanceof Node) {
                EntityType t = spatial.getUserData("type");
                if (t != null) {

                    EntityId entityId = entityData.createEntity();

                    // NOTE: WE ADD THE MODEL COMPONENT HERE !!! No need to add it in switch case later
                    // all models should have been added with an AssetLinkNode
                    // with that AssetLinkNode we can get the origin of the model
                    if (spatial.getParent() instanceof AssetLinkNode) {
                        ModelKey key = ((AssetLinkNode) spatial.getParent()).getAssetLoaderKeys().get(0);
                        entityData.setComponent(entityId, new Model(key.getName()));
                    }

                    switch (t.getType()) {

                        case DefaultModel:
                            entityData.setComponents(entityId,
                                    createFixedTransform(spatial));
                            break;

                        case StaticPhysicObjectMeshShape:
                            entityData.setComponents(entityId,
                                    createDynamicTransform(spatial),
                                    new PhysicsRigidBody(0, false, CollisionShapeType.MESH_COLLISION_SHAPE));
                            break;

                        case DynamicPhysicObjectMeshShape:
                            entityData.setComponents(entityId,
                                    createDynamicTransform(spatial),
                                    new PhysicsRigidBody(10, false, CollisionShapeType.MESH_COLLISION_SHAPE));
                            break;

                        case StaticPhysicsObjectBoxShape:
                            entityData.setComponents(entityId,
                                    createDynamicTransform(spatial),
                                    new PhysicsRigidBody(0, false, CollisionShapeType.BOX_COLLISION_SHAPE));
                            break;

                        case DynamicPhysicsObjectBoxShape:
                            entityData.setComponents(entityId,
                                    createDynamicTransform(spatial),
                                    new PhysicsRigidBody(10, false, CollisionShapeType.BOX_COLLISION_SHAPE));
                            break;

                        case StaticTerrain:
                            entityData.setComponents(entityId,
                                    createDynamicTransform(spatial),
                                    new PhysicsRigidBody(0, false, CollisionShapeType.TERRAIN_COLLISION_SHAPE));
                            break;


                        default:
                            break;

                    }

                }
            }

        });
    }


    private FixedTransformation createFixedTransform(Spatial spatial) {
        return new FixedTransformation(spatial.getWorldTranslation(), spatial.getWorldRotation(), spatial.getWorldScale());
    }

    private DynamicTransform createDynamicTransform(Spatial spatial) {
        return new DynamicTransform(spatial.getWorldTranslation(), spatial.getWorldRotation(), spatial.getWorldScale());
    }


}
