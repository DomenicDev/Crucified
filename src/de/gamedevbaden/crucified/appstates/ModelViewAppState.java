package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;

/**
 * <code>ModelViewAppState</code> represents all entities with a @link{ModelType} component visually in the scene graph.
 * <p>
 * For "static" entities changed transformation will be applied as soon as received whereas dynamic (mobile) entities
 * are treated a little different. In order to get fluent movements interpolation for this entities is used.
 * <p>
 * Created by Domenic on 11.04.2017.
 */
public class ModelViewAppState extends AbstractAppState {

    private HashMap<EntityId, Spatial> spatials;
    private HashMap<EntityId, Transform> lastTransforms; // stores the Transform of the spatial before the change.
    // used to interpolate between positions for dynamic objects
    private EntitySet visibleEntities;

    private Node rootNode;
    private ModelLoaderAppState modelLoaderAppState;

    public ModelViewAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.rootNode = ((SimpleApplication) app).getRootNode();
        this.modelLoaderAppState = stateManager.getState(ModelLoaderAppState.class);

        this.spatials = new HashMap<>();
        this.lastTransforms = new HashMap<>();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.visibleEntities = entityData.getEntities(Transform.class, Model.class);


        AmbientLight ambientLight = new AmbientLight(ColorRGBA.White);
        rootNode.addLight(ambientLight);
        super.initialize(stateManager, app);
    }

    /**
     * Get the associated spatial for the given entity
     *
     * @param entityId the entity id you want the associated spatial from
     * @return the spatial for that entity or null if there is no spatial
     */
    public Spatial getSpatial(EntityId entityId) {
        return spatials.get(entityId);
    }

    @Override
    public void update(float tpf) {

        if (visibleEntities.applyChanges()) {

            for (Entity entity : visibleEntities.getAddedEntities()) {
                addSpatial(entity); // adds the entity with its initial values
            }

            for (Entity entity : visibleEntities.getChangedEntities()) {
                updateSpatial(entity);
            }

            for (Entity entity : visibleEntities.getRemovedEntities()) {
                removeSpatial(entity);
            }
        }

    }


    private void addSpatial(Entity entity) {
        if (spatials.containsKey(entity.getId())) {
            return;
        }
        // load spatial
        Spatial spatial = getSpatial(entity);
        spatials.put(entity.getId(), spatial);

        // apply transform for that spatial
        Transform transform = entity.get(Transform.class);
        spatial.setLocalTranslation(transform.getTranslation());
        spatial.setLocalRotation(transform.getRotation());
        spatial.setLocalScale(transform.getScale());

        // tag the spatial with its entity id
        spatial.setUserData("entityId", entity.getId().getId());

        // store the initial transform as old transform
        storeOldTransform(entity, spatial);

        // finally attach to the scene
        rootNode.attachChild(spatial);
    }

    private void updateSpatial(Entity entity) {
        Spatial spatial = spatials.get(entity.getId());
        storeOldTransform(entity, spatial); // store the last transform (could be needed for interpolation
        if (spatial != null) {
            Transform transform = entity.get(Transform.class);
            spatial.setLocalTranslation(transform.getTranslation());
            spatial.setLocalRotation(transform.getRotation());
            spatial.setLocalScale(transform.getScale());
        }
    }

    /**
     * This class is the first which gets the entity updates.
     * On client side it should be possible to get the old Transform in order to interpolate between
     * position and rotation. But to do so we need to store the data beforehand.
     * Note: Transform is only stored for entity which changed their transform.
     */
    private void storeOldTransform(Entity entity, Spatial spatial) {
        Vector3f location = spatial.getLocalTranslation().clone();
        Quaternion rotation = spatial.getLocalRotation().clone();
        Vector3f scale = spatial.getLocalScale().clone();

        lastTransforms.put(entity.getId(), new Transform(location, rotation, scale));
    }

    /**
     * Returns the Transform before the last update.
     * This can be used for interpolation between two positions for example.
     *
     * @param entityId the entity id you want the old transform from
     * @return the Transform before the last update.
     */
    public Transform getOldTransform(EntityId entityId) {
        return lastTransforms.get(entityId);
    }

    private void removeSpatial(Entity entity) {
        Spatial spatial = spatials.remove(entity.getId());
        rootNode.detachChild(spatial);
    }

    private Spatial getSpatial(Entity entity) {
        System.out.println(entity.get(Transform.class).getTranslation() + " " + entity.get(Model.class).getModelType());
        return modelLoaderAppState.loadModel(entity.get(Model.class).getModelType().getModelPath());
    }

    @Override
    public void cleanup() {
        for (Entity entity : visibleEntities) {
            removeSpatial(entity);
        }
        spatials.clear();
        spatials = null;

        lastTransforms.clear();
        lastTransforms = null;

        visibleEntities.release();
        visibleEntities.clear();
        visibleEntities = null;

        modelLoaderAppState = null;

        super.cleanup();
    }
}
