package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.enums.Type;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.NeedToBeCrafted;
import de.gamedevbaden.crucified.es.components.ObjectType;
import de.gamedevbaden.crucified.es.components.Transform;
import de.gamedevbaden.crucified.es.utils.EntityFactory;

import java.util.HashMap;

/**
 * This app state basically adds models for the entities which have to be crafted.
 * To differ those from the "final" entities they will be displayed transparently.
 * <p>
 * Created by Domenic on 28.06.2017.
 */
public class VisualCraftingAppState extends AbstractAppState {

    private static final float ALPHA_VALUE = 0.35f;
    private HashMap<EntityId, Spatial> models;
    private HashMap<Geometry, MatInfoHolder> matHolder;
    private EntitySet itemsToCraft;
    private AssetManager assetManager;
    private ModelViewAppState modelViewAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.models = new HashMap<>();
        this.matHolder = new HashMap<>();
        this.assetManager = app.getAssetManager();
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.itemsToCraft = entityData.getEntities(NeedToBeCrafted.class, Transform.class, ObjectType.class, Model.class);

        for (Entity entity : itemsToCraft) {
            addTransparency(entity);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (itemsToCraft.applyChanges()) {

            for (Entity entity : itemsToCraft.getAddedEntities()) {
                addTransparency(entity);
            }

            for (Entity entity : itemsToCraft.getRemovedEntities()) {
                removeTransparency(entity);
            }
        }
    }

    private void addTransparency(Entity entity) {
        Type type = entity.get(ObjectType.class).getObjectType();
        String modelPath = EntityFactory.getModelForType(type);
        if (modelPath == null) return;

        Transform transform = entity.get(Transform.class);

//        Spatial model = assetManager.loadModel(modelPath);
//        model.setLocalTranslation(transform.getTranslation());
//        model.setLocalRotation(transform.getRotation());
//        model.setLocalScale(transform.getScale());
//        model.setUserData(GameConstants.USER_DATA_ENTITY_ID, entity.getId().getId());
        Spatial model = modelViewAppState.getSpatial(entity.getId());
        //  modelViewAppState.attachSpatial(model);

        // give the model a transparent look
        model.depthFirstTraversal(spatial -> {
            if (spatial instanceof Geometry) {
                Geometry geom = (Geometry) spatial;

                Material mat = geom.getMaterial();
                MaterialDef def = mat.getMaterialDef();
                if (!def.getName().equals("Phong Lighting")) {
                    return;
                }

                // before we change something we store the predefined settings for that material
                // so that we can reset those when the model has been crafted
                matHolder.put(geom, new MatInfoHolder(mat.getAdditionalRenderState().getBlendMode(), geom.getQueueBucket()));

                // get ambient and diffuse color
                ColorRGBA ambient = (ColorRGBA) mat.getParam("Ambient").getValue();
                ColorRGBA diffuse = (ColorRGBA) mat.getParam("Diffuse").getValue();

                // change their alpha values
                ambient.set(ambient.getRed(), ambient.getGreen(), ambient.getBlue(), ALPHA_VALUE);
                diffuse.set(diffuse.getRed(), diffuse.getGreen(), diffuse.getBlue(), ALPHA_VALUE);

                // enable material colors to make the effect take place
                mat.setParam("UseMaterialColors", VarType.Boolean, true);

                // change blend mode to alpha so we are able to see the opacity
                mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

                // put model into transparent bucket.
                geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            }
        });

        models.put(entity.getId(), model);
    }

    private void removeTransparency(Entity entity) {

        Spatial model = models.remove(entity.getId());

        model.depthFirstTraversal(spatial -> {

            if (spatial instanceof Geometry) {
                Geometry geom = (Geometry) spatial;

                Material mat = geom.getMaterial();
                MaterialDef def = mat.getMaterialDef();
                if (!def.getName().equals("Phong Lighting")) {
                    return;
                }

                // get ambient and diffuse color
                ColorRGBA ambient = (ColorRGBA) mat.getParam("Ambient").getValue();
                ColorRGBA diffuse = (ColorRGBA) mat.getParam("Diffuse").getValue();

                // reset their alpha values
                ambient.set(ambient.getRed(), ambient.getGreen(), ambient.getBlue(), 1f);
                diffuse.set(diffuse.getRed(), diffuse.getGreen(), diffuse.getBlue(), 1f);

                // restore the old material settings
                MatInfoHolder infoHolder = matHolder.remove(geom);
                if (infoHolder != null) {
                    geom.setQueueBucket(infoHolder.bucket);
                    geom.getMaterial().getAdditionalRenderState().setBlendMode(infoHolder.blendMode);
                }
            }

        });
    }

    @Override
    public void cleanup() {
        this.itemsToCraft.release();
        this.itemsToCraft.clear();
        this.itemsToCraft = null;

        for (Spatial s : models.values()) {
            s.removeFromParent();
        }

        this.models.clear();
        this.models = null;
        super.cleanup();
    }

    private class MatInfoHolder {

        RenderState.BlendMode blendMode;
        RenderQueue.Bucket bucket;

        MatInfoHolder(RenderState.BlendMode blendMode, RenderQueue.Bucket bucket) {
            this.blendMode = blendMode;
            this.bucket = bucket;
        }
    }
}
