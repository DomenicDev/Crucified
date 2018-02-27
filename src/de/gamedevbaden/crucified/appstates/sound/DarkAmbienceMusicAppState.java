package de.gamedevbaden.crucified.appstates.sound;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;

public class DarkAmbienceMusicAppState extends AbstractAppState {

    private AudioNode music;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.music = new AudioNode(app.getAssetManager(), "Sounds/Music/DarkAmbienceLoop.ogg", AudioData.DataType.Stream);
        music.setVolume(0.4f);
        music.setPositional(false);
        music.setLooping(true);
        stateManager.getState(GameCommanderAppState.class).getMainWorldNode().attachChild(music);
        music.play();

        super.initialize(stateManager, app);
    }

    @Override
    public void cleanup() {
        this.music.stop();
        this.music.removeFromParent();
        super.cleanup();
    }
}
