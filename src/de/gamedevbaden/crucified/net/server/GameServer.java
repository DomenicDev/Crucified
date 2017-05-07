package de.gamedevbaden.crucified.net.server;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.*;
import com.jme3.network.service.rmi.RmiHostedService;
import com.jme3.network.service.rmi.RmiRegistry;
import com.jme3.network.service.rpc.RpcHostedService;
import com.simsilica.es.EntityId;
import com.simsilica.es.ObservableEntityData;
import com.simsilica.es.server.EntityDataHostedService;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.game.DefaultGameSessionImplementation;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class manages the server.
 *
 * Created by Domenic on 16.04.2017.
 */
public class GameServer extends AbstractAppState implements ConnectionListener {

    private float timer;
    private float updateTime = 1f / 10f; // 10 updates per second

    private int port;
    private Server server;
    private ObservableEntityData entityData;
    private DefaultGameSessionImplementation gameSessionManager;
    private HashMap<HostedConnection, GameSession> connections = new HashMap<>();
    private RmiHostedService rmiService;

    public GameServer(int port) {
        this.port = port;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = (ObservableEntityData) stateManager.getState(EntityDataState.class).getEntityData();
        this.gameSessionManager = stateManager.getState(DefaultGameSessionImplementation.class);

        try {

            this.server = Network.createServer(port);
            NetworkUtils.initSerializers();
            this.server.addConnectionListener(this);

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

        EntityId player = entityData.createEntity();
        entityData.setComponents(player,
                new Model(ModelType.Player),
                new Transform(new Vector3f(0, 2, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                new PhysicsCharacterControl(new Vector3f(), Vector3f.UNIT_X),
                new PlayerControlled(),
                new CharacterMovementState());

        GameSession session = gameSessionManager.addPlayer(player);

        RmiRegistry rmi = rmiService.getRmiRegistry(conn);
        rmi.share(session, GameSession.class);

        connections.put(conn, session);
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        entityData.removeEntity(connections.get(conn).getPlayer());
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
