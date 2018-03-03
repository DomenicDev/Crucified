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
import de.gamedevbaden.crucified.es.components.Fireball;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;

public class FireballSoundAppState extends AbstractAppState {

    private EntitySet fireballs;
    private HashMap<EntityId, AudioNode> sounds = new HashMap<>();
    private AssetManager assetManager;
    private Node fireballNode = new Node("FireballSounds");

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        stateManager.getState(GameCommanderAppState.class).getMainWorldNode().attachChild(fireballNode);
        this.assetManager = app.getAssetManager();
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.fireballs = entityData.getEntities(Transform.class, Fireball.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (fireballs.applyChanges()) {

            for (Entity entity : fireballs.getAddedEntities()) {
                addFireballSound(entity);
            }

            for (Entity entity : fireballs.getChangedEntities()) {
                updateFireballSound(entity);
            }

            for (Entity entity : fireballs.getRemovedEntities()) {
                removeFireballSound(entity);
            }

        }

    }

    private void addFireballSound(Entity entity) {
        Transform t = entity.get(Transform.class);
        AudioNode audioNode = new AudioNode(assetManager, "Sounds/SoundEffects/fireball.WAV", AudioData.DataType.Buffer);
        audioNode.setVolume(0.4f);
        audioNode.setPositional(true);
        audioNode.setLocalTranslation(t.getTranslation());
        audioNode.setRefDistance(5f);
        audioNode.setLooping(false);
        fireballNode.attachChild(audioNode);
        audioNode.play();
        sounds.put(entity.getId(), audioNode);
    }

    private void updateFireballSound(Entity entity) {
        Transform t = entity.get(Transform.class);
        AudioNode audioNode = sounds.get(entity.getId());
        audioNode.setLocalTranslation(t.getTranslation());
    }

    private void removeFireballSound(Entity entity) {
        AudioNode audioNode = sounds.remove(entity.getId());
        audioNode.stop();
        audioNode.removeFromParent();
    }

    @Override
    public void cleanup() {
        for (Entity entity : fireballs) {
            removeFireballSound(entity);
        }
        this.fireballs.release();
        this.fireballs.clear();
        this.fireballs = null;

        this.sounds.clear();
        this.fireballNode.removeFromParent();
        super.cleanup();
    }
}
