package de.gamedevbaden.crucified.net.server;

import com.jme3.network.HostedConnection;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.enums.GameDecisionType;
import de.gamedevbaden.crucified.enums.PaperScript;
import de.gamedevbaden.crucified.enums.Scene;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.net.messages.GameDecidedMessage;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;
import de.gamedevbaden.crucified.net.messages.ReadNoteMessage;

/**
 * This is the server-side implementation of the game commander.
 * This implementation will just send messages to the corresponding client.
 * <p>
 * Created by Domenic on 12.06.2017.
 */
public class ServerGameCommander implements GameCommander {

    private HostedConnection conn;

    ServerGameCommander(HostedConnection conn) {
        this.conn = conn;
    }

    @Override
    public void loadScene(Scene scene) {
        conn.send(new LoadLevelMessage(SceneEntityLoader.sceneToLoad));
    }

    @Override
    public void readNote(PaperScript script) {
        conn.send(new ReadNoteMessage(script));
    }

    @Override
    public void onGameDecided(GameDecisionType decisionType) {
        conn.send(new GameDecidedMessage(decisionType));
    }
}
