package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.es.components.FireState;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;

/**
 * This state adds particle effects for game objects like campfires, torches etc.
 * Created by Domenic on 28.06.2017.
 */
public class FireEffectAppState extends AbstractAppState {

    private EntitySet campFires;
    private ModelViewAppState modelViewAppState;
    private AssetManager assetManager;

    private HashMap<EntityId, Node> particleEffects;

    private Node fireEffectNode;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.particleEffects = new HashMap<>();
        this.assetManager = app.getAssetManager();
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);

        this.fireEffectNode = new Node("FireEffectNode");
        stateManager.getState(GameCommanderAppState.class).getMainWorldNode().attachChild(fireEffectNode);

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.campFires = entityData.getEntities(FireState.class, Transform.class);

        for (Entity entity : campFires) {
            addCampfireEffect(entity);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (campFires.applyChanges()) {

            for (Entity entity : campFires.getAddedEntities()) {
                addCampfireEffect(entity);
            }

            for (Entity entity : campFires.getChangedEntities()) {
                updateCampfireEffect(entity);
            }

            for (Entity entity : campFires.getRemovedEntities()) {
                removeCampfireEffect(entity);
            }

        }

    }

    private void addCampfireEffect(Entity entity) {
        Transform t = entity.get(Transform.class);
        Node effect = (Node) assetManager.loadModel("Models/Effects/Campfire.j3o");
        effect.setLocalTranslation(t.getTranslation());
        fireEffectNode.attachChild(effect);
        particleEffects.put(entity.getId(), effect);
        updateCampfireEffect(entity);
    }

    private void updateCampfireEffect(Entity entity) {
        Transform t = entity.get(Transform.class);
        Node effect = particleEffects.get(entity.getId());
        effect.setLocalTranslation(t.getTranslation());
        FireState fireState = entity.get(FireState.class);
        if (fireState.isOn()) {
            effect.setCullHint(Spatial.CullHint.Inherit); // enable
        } else {
            effect.setCullHint(Spatial.CullHint.Always); // disable
        }
    }

    private void removeCampfireEffect(Entity entity) {
        Spatial effect = particleEffects.remove(entity.getId());
        effect.removeFromParent();
    }

    @Override
    public void cleanup() {
        for (Entity entity : campFires) {
            removeCampfireEffect(entity);
        }
        this.campFires.release();
        this.campFires.clear();
        this.campFires = null;
        super.cleanup();
    }
}
