package de.gamedevbaden.crucified.net.client;

import com.jme3.app.Application;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;

import java.util.concurrent.Callable;

/**
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
            this.app.enqueue((Callable) () -> {
                LoadLevelMessage lm = (LoadLevelMessage) m;
                gameCommander.loadScene(lm.getScene());
                return null;
            });

        }
    }
}
