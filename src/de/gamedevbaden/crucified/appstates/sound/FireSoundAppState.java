package de.gamedevbaden.crucified.appstates.sound;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
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
 * This app state adds sounds for entities which are on fire for example a campfire or a torch.
 * Created by Domenic on 01.07.2017.
 */
public class FireSoundAppState extends AbstractAppState {

    private EntitySet entitiesOnFire;
    private HashMap<EntityId, AudioNode> audios;
    private AssetManager assetManager;
    private Node gameNode;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.audios = new HashMap<>();
        this.assetManager = app.getAssetManager();
        this.gameNode = stateManager.getState(GameCommanderAppState.class).getMainWorldNode();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.entitiesOnFire = entityData.getEntities(FireState.class, Transform.class);

        for (Entity entity : entitiesOnFire) {
            addAudio(entity);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (entitiesOnFire.applyChanges()) {

            for (Entity entity : entitiesOnFire.getAddedEntities()) {
                addAudio(entity);
            }

            for (Entity entity : entitiesOnFire.getChangedEntities()) {
                updateAudio(entity);
            }

            for (Entity entity : entitiesOnFire.getRemovedEntities()) {
                removeAudio(entity);
            }

        }

    }

    private void addAudio(Entity entity) {
        AudioNode audioNode = new AudioNode(assetManager, "Sounds/SoundEffects/Fire.WAV", AudioData.DataType.Stream);
        audioNode.setVolume(0.4f);
        audioNode.setPositional(true);
        audioNode.setLocalTranslation(entity.get(Transform.class).getTranslation());
        audioNode.setRefDistance(5f);
        audioNode.setLooping(true);
        gameNode.attachChild(audioNode);
        audios.put(entity.getId(), audioNode);
        checkStatus(entity, audioNode);
    }

    private void updateAudio(Entity entity) {
        AudioNode audioNode = audios.get(entity.getId());
        audioNode.setLocalTranslation(entity.get(Transform.class).getTranslation());
        checkStatus(entity, audioNode);
    }

    private void removeAudio(Entity entity) {
        AudioNode audioNode = audios.remove(entity.getId());
        audioNode.removeFromParent();
    }

    private void checkStatus(Entity entity, AudioNode audioNode) {
        if (entity.get(FireState.class).isOn()) {
            audioNode.play();
        } else {
            audioNode.pause();
        }
    }

    @Override
    public void cleanup() {
        for (Entity entity : entitiesOnFire) {
            removeAudio(entity);
        }
        this.entitiesOnFire.release();
        this.entitiesOnFire.clear();
        this.entitiesOnFire = null;
        super.cleanup();
    }
}
