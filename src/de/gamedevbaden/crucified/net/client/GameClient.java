package de.gamedevbaden.crucified.net.client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.MessageConnection;
import com.jme3.network.Network;
import com.jme3.network.service.rmi.RmiClientService;
import com.jme3.network.service.rpc.RpcClientService;
import com.simsilica.es.EntityData;
import com.simsilica.es.client.EntityDataClientService;
import de.gamedevbaden.crucified.game.GameSession;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Domenic on 04.05.2017.
 */
public class GameClient extends AbstractAppState implements ClientStateListener {

    CountDownLatch startedSignal;
    private String address;
    private int port;
    private EntityData entityData;
    private Client client;
    private RmiClientService rmiClientService;
    private GameSession gameSession;

    public GameClient() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    public boolean connect(String address, int port) {
        startedSignal = new CountDownLatch(1);
        try {
            this.client = Network.connectToServer(address, port);
            this.client.addClientStateListener(this);

            this.rmiClientService = new RmiClientService();
            this.client.getServices().addService(new RpcClientService());
            this.client.getServices().addService(rmiClientService);
            this.client.getServices().addService(new EntityDataClientService(MessageConnection.CHANNEL_DEFAULT_RELIABLE));

            this.address = address;
            this.port = port;

            this.client.start();
            startedSignal.await();
            this.gameSession = rmiClientService.getRemoteObject(GameSession.class);
            this.entityData = this.client.getServices().getService(EntityDataClientService.class).getEntityData();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public EntityData getEntityData() {
        return entityData;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public Client getClient() {
        return client;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    @Override
    public void clientConnected(Client c) {
        startedSignal.countDown();

    }

    @Override
    public void clientDisconnected(Client c, DisconnectInfo info) {

    }

    @Override
    public void cleanup() {
        if (client != null && client.isConnected()) {
            client.close();
        }
        super.cleanup();
    }
}
