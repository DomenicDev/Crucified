package de.gamedevbaden.crucified.net.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Sent by the client. Tells the server that the client is ready for game start.
 * Created by Domenic on 06.06.2017.
 */
@Serializable
public class ReadyForGameStartMessage extends AbstractMessage {

    private boolean ready;

    public ReadyForGameStartMessage() {
    }

    public ReadyForGameStartMessage(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }
}
