package de.gamedevbaden.crucified.net.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.gamedevbaden.crucified.enums.GameDecisionType;

@Serializable
public class GameDecidedMessage extends AbstractMessage {

    private GameDecisionType gameDecisionType;

    public GameDecidedMessage() {
    }

    public GameDecidedMessage(GameDecisionType type) {
        this.gameDecisionType = type;
    }

    public GameDecisionType getGameDecisionType() {
        return gameDecisionType;
    }
}
