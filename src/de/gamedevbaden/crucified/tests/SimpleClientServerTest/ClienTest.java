package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.MessageConnection;
import com.jme3.network.Network;
import com.jme3.renderer.RenderManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.client.EntityDataClientService;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.ModelLoaderAppState;
import de.gamedevbaden.crucified.appstates.ModelViewAppState;
import de.gamedevbaden.crucified.appstates.MovementInterpolator;
import de.gamedevbaden.crucified.net.NetworkedEntityData;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Domenic on 26.04.2017.
 */
public class ClienTest extends SimpleApplication {

    private EntityData entityData;

    public static void main(String[] args) {
        //   NetworkUtils.initSerializers();
        new ClienTest().start();
    }

    @Override
    public void simpleInitApp() {

        setPauseOnLostFocus(false);

        flyCam.setMoveSpeed(10);

        Client client;
        try {
            client = Network.connectToServer("localhost", 5555);
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
                    System.out.println("ClienTest disconnected.");
                }
            });
            client.start();

            // Wait for the client to start
            System.out.println("Waiting for connection setup.");
            startedSignal.await();
            System.out.println("Connected.");
        } catch (IOException ex) {
            Logger.getLogger(NetworkedEntityData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        stateManager.attach(new EntityDataState(entityData));
        stateManager.attach(new ModelLoaderAppState());
        stateManager.attach(new ModelViewAppState());
        stateManager.attach(new MovementInterpolator());

    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
