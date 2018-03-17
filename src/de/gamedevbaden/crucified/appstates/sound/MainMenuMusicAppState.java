package de.gamedevbaden.crucified.appstates.sound;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;

public class MainMenuMusicAppState extends AbstractAppState {

    private AudioNode music;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.music = new AudioNode(app.getAssetManager(), "Sounds/Music/Main Theme Crucified.ogg", AudioData.DataType.Stream);
        this.music.setPositional(false);
        this.music.setLooping(true);
        this.music.setDirectional(false);
        this.music.setVolume(0.5f);
        this.music.play();
        super.initialize(stateManager, app);
    }

    public void play() {
        if (music != null) {
            this.music.play();
        }
    }

    public void stop() {
        if (music != null) {
            this.music.stop();
        }
    }

    @Override
    public void cleanup() {
        if (music != null) {
            music.stop();
            music.removeFromParent();
        }
    }
}
