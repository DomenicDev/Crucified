package de.gamedevbaden.crucified.net.client;

import com.jme3.app.Application;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import de.gamedevbaden.crucified.MainGameAppState;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.net.messages.GameDecidedMessage;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;
import de.gamedevbaden.crucified.net.messages.ReadNoteMessage;
import de.gamedevbaden.crucified.net.messages.StartGameMessage;

/**
 * Receives messages from the server and calls the relevant app states
 * about what the server sent.
 *
 * Created by Domenic on 27.05.2017.
 */
public class ClientMessageListener implements MessageListener<Client> {

    private GameCommander gameCommander;
    private Application app;

    public ClientMessageListener(Application app, GameCommander gameCommander) {
        this.app = app;
        this.gameCommander = gameCommander;
    }

    @Override
    public void messageReceived(Client source, Message m) {
        if (m instanceof LoadLevelMessage) {
            this.app.enqueue(() -> {
                LoadLevelMessage lm = (LoadLevelMessage) m;
                gameCommander.loadScene(lm.getScene());
                return null;
            });

        }

        if (m instanceof ReadNoteMessage) {
            this.app.enqueue(() -> {
                ReadNoteMessage rm = (ReadNoteMessage) m;
                gameCommander.readNote(rm.getScript());
                return null;
            });
        }

        if (m instanceof GameDecidedMessage) {
            this.app.enqueue(() -> {
                GameDecidedMessage dm = (GameDecidedMessage) m;
                gameCommander.onGameDecided(dm.getGameDecisionType());
                return null;
            });
        }

        if (m instanceof StartGameMessage) {
            this.app.enqueue(() -> {
                app.getStateManager().getState(MainGameAppState.class).startGame();
            });
        }
    }
}
