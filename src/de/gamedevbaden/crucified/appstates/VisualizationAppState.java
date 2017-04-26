package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.DynamicTransform;
import de.gamedevbaden.crucified.es.components.FixedTransformation;
import de.gamedevbaden.crucified.es.components.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <code>VisualizationAppState</code> represents all entities with a @link{ModelType} component visually in the scene graph.
 *
 * For "static" entities changed transformation will be applied as soon as received whereas dynamic (mobile) entities
 * are treated a little different. In order to get fluent movements interpolation for this entities is used.
 *
 * Created by Domenic on 11.04.2017.
 */
public class VisualizationAppState extends AbstractAppState {

    private HashMap<EntityId, Spatial> entities;
    private List<EntityId> retardedEntities; // these entities will be used for interpolation

    private EntitySet staticEntities;
    private EntitySet dynamicEntities;

    private Node rootNode;
    private ModelLoaderAppState modelLoaderAppState;

    public VisualizationAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.rootNode = ((SimpleApplication)app).getRootNode();
        this.modelLoaderAppState = stateManager.getState(ModelLoaderAppState.class);

        this.entities = new HashMap<>();
        this.retardedEntities = new ArrayList<>();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.staticEntities = entityData.getEntities(FixedTransformation.class, Model.class);
        this.dynamicEntities = entityData.getEntities(DynamicTransform.class, Model.class);
        super.initialize(stateManager, app);

        AmbientLight ambientLight = new AmbientLight(ColorRGBA.White);
        rootNode.addLight(ambientLight);
    }

    /**
     * Use this method to put the given spatial with the given spatial in the global list.
     * This is only needed when you want to make sure that no other model is loaded for that entity, because
     * is has already been loaded when loading the scene for example.
     * @param entityId
     * @param spatial
     */
    public void add(EntityId entityId, Spatial spatial) {
        if (entityId != null && spatial != null && !entities.containsKey(entityId)) {
            this.entities.put(entityId, spatial);
        }
    }

    /**
     * Get the associated spatial for the given entity
     * @param entityId the entity id you want the associated spatial from
     * @return the spatial for that entity or null if there is no spatial
     */
    public Spatial getSpatial(EntityId entityId) {
        return entities.get(entityId);
    }

    @Override
    public void update(float tpf) {

        // ------- STATIC ENTITIES ------------------

        if (staticEntities.applyChanges()) {

            for (Entity e : staticEntities.getAddedEntities()) {
                if (!entities.containsKey(e.getId())) {
                    addStaticEntity(e);
                }
            }

            for (Entity e : staticEntities.getChangedEntities()) {
                Spatial model = entities.get(e.getId());
                updateTransformation(e, model);
            }

            for (Entity e : staticEntities.getRemovedEntities()) {
               removeEntity(e);
            }
        }

        // ------- DYNAMIC (MOBILE) ENTITIES -----------------

        if (dynamicEntities.applyChanges()) {

            for (Entity entity : dynamicEntities.getAddedEntities()) {
                if (!entities.containsKey(entity.getId())) {
                    addDynamicEntity(entity); // adds the entity with its initial values
                }
            }

            for (Entity entity : dynamicEntities.getChangedEntities()) {
                if (!retardedEntities.contains(entity.getId())) {
                    retardedEntities.add(entity.getId());
                }
            }

            for (Entity entity : dynamicEntities.getRemovedEntities()) {
                removeEntity(entity);
            }
        }


        // Here we want to interpolate the dynamic entities if necessary

        // interpolate all retarded entities
        interpolateRetardedEntities(tpf);

    }

    private void addStaticEntity(Entity entity) {
        Spatial spatial = getSpatial(entity);
        entities.put(entity.getId(), spatial);
        updateTransformation(entity, spatial);
        rootNode.attachChild(spatial);
    }

    public void addDynamicEntity(Entity entity) {
        Spatial spatial = getSpatial(entity);
        entities.put(entity.getId(), spatial);
        DynamicTransform transform = entity.get(DynamicTransform.class);
        spatial.setLocalTranslation(transform.getTranslation());
        spatial.setLocalRotation(transform.getRotation());
        spatial.setLocalScale(transform.getScale());
        rootNode.attachChild(spatial);
    }

    private void removeEntity(Entity entity) {
        Spatial spatial = entities.remove(entity.getId());
        rootNode.detachChild(spatial);
    }

    private Spatial getSpatial(Entity entity) {
        return modelLoaderAppState.loadModel(entity.get(Model.class).getModelPath());
    }


    private void updateTransformation(Entity entity, Spatial spatial) {
        if (spatial != null) {
            FixedTransformation fixedTransformation = entity.get(FixedTransformation.class);
            spatial.setLocalTranslation(fixedTransformation.getTranslation());
            spatial.setLocalRotation(fixedTransformation.getRotation());
            spatial.setLocalScale(fixedTransformation.getScale());
        }
    }


    /**
     * Updates the given spatial with the given transform.
     * Note, interpolation is used here.
     * @param transform
     * @param spatial
     * @return true if interpolation etc. was necessary; false if transform was directly applied
     */
    private boolean updateDynamicEntity(DynamicTransform transform, Spatial spatial, float tpf) {
        boolean interpolationNecessary = false;
        // check for location
        if (spatial.getLocalTranslation().distance(transform.getTranslation()) <= 0.05f) {
            spatial.setLocalTranslation(transform.getTranslation());
        } else {
            spatial.setLocalTranslation(spatial.getLocalTranslation().interpolateLocal(transform.getTranslation(), tpf / 0.03f));
            interpolationNecessary = true;
        }

        // check for rotation ToDo: Do check if needed at all
        spatial.setLocalRotation(spatial.getLocalRotation().slerp(spatial.getLocalRotation(), transform.getRotation(), tpf / 0.03f));

        if (spatial.getLocalScale().distance(transform.getScale()) <= 0.01) {
            spatial.setLocalScale(transform.getScale());
        } else {
            spatial.setLocalScale(spatial.getLocalScale().interpolateLocal(transform.getScale(), tpf / 0.03f));
            interpolationNecessary = true;
        }

        return interpolationNecessary;
    }

    private void interpolateRetardedEntities(float tpf) {
        if (!retardedEntities.isEmpty()) {
            ArrayList<EntityId> entityIdsToRemove = new ArrayList<>();
            for (EntityId entityId : retardedEntities) {
                Entity entity = dynamicEntities.getEntity(entityId);
                if (entity != null) {
                    DynamicTransform transform = entity.get(DynamicTransform.class);
                    Spatial spatial = entities.get(entityId);
                    if (transform != null && spatial != null && !updateDynamicEntity(transform, spatial, tpf)) {
                        entityIdsToRemove.add(entityId);
                    }
                }
            }
            retardedEntities.removeAll(entityIdsToRemove);
        }
    }

    @Override
    public void cleanup() {
        entities.clear();
        entities = null;
        super.cleanup();
    }
}
