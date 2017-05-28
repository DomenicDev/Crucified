package de.gamedevbaden.crucified.net.client;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;

/**
 * Created by Domenic on 27.05.2017.
 */
public class ClientMessageListener implements MessageListener<Client> {

    private GameCommander gameCommander;

    public ClientMessageListener(GameCommander gameCommander) {
        this.gameCommander = gameCommander;
    }

    @Override
    public void messageReceived(Client source, Message m) {
        if (m instanceof LoadLevelMessage) {
            LoadLevelMessage lm = (LoadLevelMessage) m;
            gameCommander.loadScene(lm.getLevelPath());
        }
    }
}
