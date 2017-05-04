package de.gamedevbaden.crucified.tests.test2;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Network;

import java.io.IOException;

/**
 * Created by Domenic on 04.05.2017.
 */
public class TestClient extends SimpleApplication {

    public static void main(String[] args) {
        new TestClient().start();
    }


    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        Client client = null;
        try {
            client = Network.connectToServer("localhost", 2233);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addClientStateListener(new ClientStateListener() {
            @Override
            public void clientConnected(Client c) {
                System.out.println("connected");
            }

            @Override
            public void clientDisconnected(Client c, DisconnectInfo info) {
                System.out.println("disconnected");
            }
        });

        client.start();
    }
}
