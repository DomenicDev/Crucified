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
import de.gamedevbaden.crucified.es.components.HitComponent;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;

public class HitSoundAppState extends AbstractAppState {

    private HashMap<EntityId, AudioNode> sounds = new HashMap<>();
    private EntitySet hitEntities;

    private AssetManager assetManager;
    private Node hitNode = new Node("HitSoundNode");

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.hitEntities = entityData.getEntities(Transform.class, HitComponent.class);

        stateManager.getState(GameCommanderAppState.class).getMainWorldNode().attachChild(hitNode);

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (hitEntities.applyChanges()) {

            for (Entity entity : hitEntities.getAddedEntities()) {
                addHitSound(entity);
            }

            for (Entity entity : hitEntities.getRemovedEntities()) {
                removeHitSound(entity);
            }
        }
    }

    private void addHitSound(Entity entity) {
        Transform t = entity.get(Transform.class);
        HitComponent hit = entity.get(HitComponent.class);
        String soundPath = "Sounds/SoundEffects/";
        switch (hit.getHitType()) {
            case HitComponent.HIT_PLAYER: soundPath += "player_hit.WAV"; break;
            case HitComponent.HIT_GROUND: soundPath += "ground_hit.WAV"; break;
            default: soundPath += "ground_hit.WAV";
        }

        AudioNode audioNode = new AudioNode(assetManager, soundPath, AudioData.DataType.Buffer);
        audioNode.setVolume(0.4f);
        audioNode.setPositional(true);
        audioNode.setLocalTranslation(t.getTranslation());
        audioNode.setRefDistance(2f);
        audioNode.setLooping(false);
        hitNode.attachChild(audioNode);
        audioNode.play();
        sounds.put(entity.getId(), audioNode);
    }

    private void removeHitSound(Entity entity) {
        AudioNode audioNode = sounds.remove(entity.getId());
        audioNode.stop();
        audioNode.removeFromParent();
    }

    @Override
    public void cleanup() {
        for (Entity entity : hitEntities) {
            removeHitSound(entity);
        }

        this.hitEntities.release();
        this.hitEntities.clear();
        this.hitEntities = null;

        this.hitNode.removeFromParent();
        this.sounds.clear();

        super.cleanup();
    }
}
