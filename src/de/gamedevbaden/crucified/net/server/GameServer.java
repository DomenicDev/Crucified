package de.gamedevbaden.crucified.net.server;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.network.service.rmi.RmiHostedService;
import com.jme3.network.service.rmi.RmiRegistry;
import com.jme3.network.service.rpc.RpcHostedService;
import com.jme3.network.service.serializer.ServerSerializerRegistrationsService;
import com.simsilica.es.EntityId;
import com.simsilica.es.ObservableEntityData;
import com.simsilica.es.server.EntityDataHostedService;
import de.gamedevbaden.crucified.MainGameAppState;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.GameCommanderHolder;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.appstates.game.GameSessionManager;
import de.gamedevbaden.crucified.appstates.gui.NetworkGameScreenController;
import de.gamedevbaden.crucified.appstates.gui.NiftyAppState;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.NetworkUtils;
import de.gamedevbaden.crucified.net.messages.ReadyForGameStartMessage;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class manages the server.
 * Created by Domenic on 16.04.2017.
 */
public class GameServer extends AbstractAppState implements ConnectionListener, MessageListener<HostedConnection> {

    private float timer;

    private int port;
    private Server server;
    private ObservableEntityData entityData;
    private GameSessionManager gameSessionManager;
    private GameCommanderHolder commanderHolder;
    private HashMap<HostedConnection, GameSession> gameSessionHashMap = new HashMap<>();
    private RmiHostedService rmiService;
    private AppStateManager stateManager;

    private EntityId secondPlayer;

    private Application app;

    public GameServer(int port) {
        this.port = port;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
        this.stateManager = stateManager;
        this.entityData = (ObservableEntityData) stateManager.getState(EntityDataState.class).getEntityData();
        this.gameSessionManager = stateManager.getState(GameSessionManager.class);
        this.commanderHolder = stateManager.getState(GameCommanderHolder.class);

        try {
            Serializer.initialize();
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

    @Override
    public void update(float tpf) {
        if ((timer += tpf) >= 0.1f) { // 10 updates per second
            getServer().getServices().getService(EntityDataHostedService.class).sendUpdates();
            timer = 0;
        }
    }

    public Server getServer() {
        return server;
    }

    private int playerCounter = 0;
    @Override
    public void connectionAdded(Server server, HostedConnection conn) {

        System.out.println("Client #" + conn.getId() + " has connected!");

        this.secondPlayer = entityData.createEntity(); //(playerCounter++ % 2 == 0) ? EntityFactory.createDemon(entityData) : EntityFactory.createPlayer(entityData);

        System.out.println(secondPlayer);

        // create a game session for this player
        GameSession session = gameSessionManager.createSession(secondPlayer);

        // share this GameSession object so the client can access it
        RmiRegistry rmi = rmiService.getRmiRegistry(conn);
        rmi.share(session, GameSession.class);

        // create a server side game commander which basically sends commands to the client
        // the client also has one
        GameCommander commander = new ServerGameCommander(conn);
        commanderHolder.add(secondPlayer, commander);

        gameSessionHashMap.put(conn, session);

        NiftyAppState niftyAppState = stateManager.getState(NiftyAppState.class);
        if (niftyAppState != null) {
            niftyAppState.getController(NetworkGameScreenController.class).setSecondPlayerConnected(true);
        }
    }

    public EntityId getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        entityData.removeEntity(gameSessionHashMap.get(conn).getPlayer());

        NiftyAppState niftyAppState = stateManager.getState(NiftyAppState.class);
        if (niftyAppState != null) {
            niftyAppState.getController(NetworkGameScreenController.class).setSecondPlayerConnected(false);
        }

        // we close the game
        stateManager.getState(MainGameAppState.class).closeExistingGame();
    }

    @Override
    public void messageReceived(HostedConnection source, Message m) {
        if (false && m instanceof ReadyForGameStartMessage) {
            ReadyForGameStartMessage rm = (ReadyForGameStartMessage) m;
            if (rm.isReady()) {
                EntityId playerId = gameSessionHashMap.get(source).getPlayer();
                commanderHolder.get(playerId).loadScene(SceneEntityLoader.sceneToLoad);
            }
        }
    }

    @Override
    public void cleanup() {
        if (server != null && server.isRunning()) {
            for (HostedConnection conn : server.getConnections()) {
                conn.close("Server is shutting down!");
            }
            server.getServices().removeService(server.getServices().getService(ServerSerializerRegistrationsService.class));
            server.close();
        }

        this.gameSessionHashMap.clear();
        this.gameSessionHashMap = null;

        super.cleanup();
    }
}
