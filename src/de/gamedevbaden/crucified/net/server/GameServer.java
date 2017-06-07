package de.gamedevbaden.crucified.net.server;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.*;
import com.jme3.network.service.rmi.RmiHostedService;
import com.jme3.network.service.rmi.RmiRegistry;
import com.jme3.network.service.rpc.RpcHostedService;
import com.simsilica.es.EntityId;
import com.simsilica.es.ObservableEntityData;
import com.simsilica.es.server.EntityDataHostedService;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.appstates.game.GameSessionManager;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.NetworkUtils;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;
import de.gamedevbaden.crucified.net.messages.ReadyForGameStartMessage;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class manages the server.
 *
 * Created by Domenic on 16.04.2017.
 */
public class GameServer extends AbstractAppState implements ConnectionListener, MessageListener<HostedConnection> {

    private float timer;
    private float updateTime = 1f / 10f; // 10 updates per second

    private int port;
    private Server server;
    private ObservableEntityData entityData;
    private GameSessionManager gameSessionManager;
    private HashMap<HostedConnection, GameSession> gameSessionHashMap = new HashMap<>();
    private RmiHostedService rmiService;

    public GameServer(int port) {
        this.port = port;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = (ObservableEntityData) stateManager.getState(EntityDataState.class).getEntityData();
        this.gameSessionManager = stateManager.getState(GameSessionManager.class);

        try {

            this.server = Network.createServer(port);

            NetworkUtils.initEntityDataSerializers();
            NetworkUtils.initMessageSerializers();

            this.server.addConnectionListener(this);
            this.server.addMessageListener(this);

            this.rmiService = new RmiHostedService();

            this.server.getServices().addService(new EntityDataHostedService(MessageConnection.CHANNEL_DEFAULT_RELIABLE, entityData));
            this.server.getServices().addService(new RpcHostedService());
            this.server.getServices().addService(rmiService);
            this.server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        super.initialize(stateManager, app);
    }


    public void setAmountOfUpdatesPerSecond(int amount) {
        if (amount > 0) {
            this.updateTime = 1f / (float) amount;
        }
    }

    @Override
    public void update(float tpf) {
        if ((timer += tpf) >= updateTime) {
            getServer().getServices().getService(EntityDataHostedService.class).sendUpdates();
            timer = 0;
        }
    }


    public int getPort() {
        return port;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void connectionAdded(Server server, HostedConnection conn) {

        System.out.println("conn added");

        //ToDo: Later, we don't want to directly create a player entity, only when game starts

        EntityId player = EntityFactory.createPlayer(entityData);

        GameSession session = gameSessionManager.createSession(player);

        RmiRegistry rmi = rmiService.getRmiRegistry(conn);
        rmi.share(session, GameSession.class);

//        GameCommander gameCommander = rmi.getRemoteObject(GameCommander.class);
//        this.commanderCollector.addGameCommander(gameCommander);
//        gameCommander.loadScene("Scenes/TestScene.j3o");

        // let client directly load game world
        //   server.broadcast(Filters.equalTo(conn), new LoadLevelMessage(SceneEntityLoader.sceneToLoad));


        gameSessionHashMap.put(conn, session);
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        entityData.removeEntity(gameSessionHashMap.get(conn).getPlayer());
    }

    @Override
    public void messageReceived(HostedConnection source, Message m) {
        if (m instanceof ReadyForGameStartMessage) {
            ReadyForGameStartMessage rm = (ReadyForGameStartMessage) m;
            if (rm.isReady()) {
                source.send(new LoadLevelMessage(SceneEntityLoader.sceneToLoad));
            }
        }
    }

    @Override
    public void cleanup() {
        // TODO cleanup all
        if (server != null) {
            server.close();
        }
        super.cleanup();
    }


}
