package de.gamedevbaden.crucified.tests.test2;

import com.jme3.app.SimpleApplication;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;

import java.io.IOException;

/**
 * Created by Domenic on 04.05.2017.
 */
public class TestServer extends SimpleApplication {

    public static void main(String[] args) {
        new TestServer().start();

    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        try {
            Server server = Network.createServer(2233);
            server.addConnectionListener(new ConnectionListener() {
                @Override
                public void connectionAdded(Server server, HostedConnection conn) {
                    System.out.println("conn added");
                }

                @Override
                public void connectionRemoved(Server server, HostedConnection conn) {
                    System.out.println("conn removed");
                }
            });
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
