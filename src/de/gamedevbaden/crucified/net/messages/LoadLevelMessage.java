package de.gamedevbaden.crucified.net.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Domenic on 27.05.2017.
 */
@Serializable
public class LoadLevelMessage extends AbstractMessage {

    private String levelPath;

    public LoadLevelMessage() {
    }

    public LoadLevelMessage(String levelPath) {
        this.levelPath = levelPath;
    }

    public String getLevelPath() {
        return levelPath;
    }
}
