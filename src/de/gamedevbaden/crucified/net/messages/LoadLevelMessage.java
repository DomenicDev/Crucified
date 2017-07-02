package de.gamedevbaden.crucified.net.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.gamedevbaden.crucified.enums.Scene;

/**
 * This message asks the client to load the specified scene.
 * Created by Domenic on 27.05.2017.
 */
@Serializable
public class LoadLevelMessage extends AbstractMessage {

    private Scene scene;

    public LoadLevelMessage() {
    }

    public LoadLevelMessage(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
