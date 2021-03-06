package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.SimpleApplication;
import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.client.GameClient;
import de.gamedevbaden.crucified.net.messages.ReadyForGameStartMessage;
import de.gamedevbaden.crucified.utils.GameInitializer;
import de.gamedevbaden.crucified.utils.GameOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Domenic on 26.04.2017.
 */
public class ClientTest extends SimpleApplication {

    public static void main(String[] args) {
        new ClientTest().start();
    }

    @Override
    public void simpleInitApp() {
        Logger.getLogger("").setLevel(Level.FINE);
        setPauseOnLostFocus(false);
        flyCam.setEnabled(false);

        GameOptions.ENABLE_PHYSICS_DEBUG = false;

        GameCommanderAppState gameCommander = new GameCommanderAppState(this);
        stateManager.attach(gameCommander);

        // init game client
        GameClient client = new GameClient();
        stateManager.attach(client);
        client.connect("localhost", 5555, this, gameCommander);

        EntityData entityData = client.getEntityData();
        GameSession gameSession = client.getGameSession();

        // create entity data state with remote entity data
        stateManager.attach(new EntityDataState(entityData));

        // create our game session app states
        stateManager.attach(new PlayerInteractionState());
        stateManager.attach(new GameEventAppState());

        // init game and view states
        GameInitializer.initEssentialAppStates(stateManager);
        GameInitializer.initInputAppStates(stateManager);
        GameInitializer.initGameSessionRelatedAppStates(stateManager, gameSession);
        GameInitializer.initViewAppStates(stateManager);
        GameInitializer.initSoundAppStates(stateManager);
        GameInitializer.initClientAppStates(stateManager);
        GameInitializer.initClientStatesWithGameSessionDependency(stateManager, gameSession);
        GameInitializer.initPlayerStates(stateManager);
        GameInitializer.initFirstPersonCameraView(stateManager);


        client.sendMessage(new ReadyForGameStartMessage(true));

    }

}
