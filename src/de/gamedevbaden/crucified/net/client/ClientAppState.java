package de.gamedevbaden.crucified.net.client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.MessageConnection;
import com.jme3.network.Network;
import com.simsilica.es.EntityData;
import com.simsilica.es.client.EntityDataClientService;
import de.gamedevbaden.crucified.net.NetworkedEntityData;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Domenic on 16.04.2017.
 */
public class ClientAppState extends AbstractAppState implements ActionListener {

    private Client client;
    private EntityData entityData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    public boolean connectToServer(String hostIp, int port) {

        try {
            client = Network.connectToServer(hostIp, port);
            client.getServices().addService(new EntityDataClientService(MessageConnection.CHANNEL_DEFAULT_RELIABLE));
            this.entityData = client.getServices().getService(EntityDataClientService.class).getEntityData();
            final CountDownLatch startedSignal = new CountDownLatch(1);
            client.addClientStateListener(new ClientStateListener() {
                @Override
                public void clientConnected(com.jme3.network.Client c) {
                    startedSignal.countDown();
                }

                @Override
                public void clientDisconnected(com.jme3.network.Client c, ClientStateListener.DisconnectInfo info) {
                    System.out.println("ClientTest disconnected.");
                }
            });
            client.start();

            // Wait for the client to start
            System.out.println("Waiting for connection setup.");
            startedSignal.await();
            System.out.println("Connected.");


            return true;

        } catch (IOException ex) {
            Logger.getLogger(NetworkedEntityData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

    }

    public EntityData getEntityData() {
        return entityData;
    }
}
