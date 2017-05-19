package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.es.components.SoundComponent;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;

/**
 * Created by Domenic on 12.05.2017.
 */
public class SoundAppState extends AbstractAppState {

    private EntitySet positionalSoundEntities;
    private EntitySet globalSoundEntities;

    private HashMap<EntityId, AudioNode> audios;

    private Node audioNode;
    private AssetManager assetManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.positionalSoundEntities = entityData.getEntities(new FieldFilter<>(SoundComponent.class, "positional", true), SoundComponent.class, Transform.class);
        this.globalSoundEntities = entityData.getEntities(new FieldFilter<>(SoundComponent.class, "positional", false), SoundComponent.class);

        this.audios = new HashMap<>();

        Node rootNode = ((SimpleApplication) app).getRootNode();
        audioNode = new Node("AudioNode");
        rootNode.attachChild(audioNode);

        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {

        if (positionalSoundEntities.applyChanges()) {

            for (Entity entity : positionalSoundEntities.getAddedEntities()) {
                createPositionalAudio(entity);
            }

            for (Entity entity : positionalSoundEntities.getChangedEntities()) {
                updatePositionalAudio(entity);
            }

            for (Entity entity : positionalSoundEntities.getRemovedEntities()) {
                removeAudio(entity);
            }

        }

        if (globalSoundEntities.applyChanges()) {

            for (Entity entity : globalSoundEntities.getAddedEntities()) {
                createAudioNode(entity);
            }

            for (Entity entity : globalSoundEntities.getRemovedEntities()) {
                removeAudio(entity);
            }

        }


    }

    private void createPositionalAudio(Entity entity) {
        Transform transform = entity.get(Transform.class);

        AudioNode audio = createAudioNode(entity);
        audio.setLocalTranslation(transform.getTranslation());
    }

    private void updatePositionalAudio(Entity entity) {
        Transform transform = entity.get(Transform.class);
        AudioNode audio = audios.get(entity.getId());
        if (audio != null) {
            audio.setLocalTranslation(transform.getTranslation());
        }
    }

    private AudioNode createAudioNode(Entity entity) {
        SoundComponent sound = entity.get(SoundComponent.class);

        AudioNode audio = new AudioNode(assetManager, sound.getSound().getAudioPath(), sound.getSound().getDataType());
        audio.setLooping(sound.isLooping());
        audio.setPositional(sound.isPositional());
        audioNode.attachChild(audio);
        audio.play();
        audios.put(entity.getId(), audio);
        return audio;
    }

    private void removeAudio(Entity entity) {
        if (audios.containsKey(entity.getId())) {
            AudioNode audio = audios.remove(entity.getId());
            audio.removeFromParent();
        }
    }

    @Override
    public void cleanup() {
        this.positionalSoundEntities.release();
        this.positionalSoundEntities.clear();
        this.positionalSoundEntities = null;

        this.globalSoundEntities.release();
        this.globalSoundEntities.clear();
        this.globalSoundEntities = null;

        this.audioNode.detachAllChildren();
        this.audioNode.removeFromParent();
        this.audioNode = null;

        this.audios.clear();
        this.audios = null;

        this.assetManager = null;
        super.cleanup();
    }
}
