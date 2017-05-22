package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.appstates.view.FirstPersonCameraView;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.client.GameClient;
import de.gamedevbaden.crucified.utils.GameInitializer;

/**
 * Created by Domenic on 26.04.2017.
 */
public class ClientTest extends SimpleApplication {

    public static void main(String[] args) {
        new ClientTest().start();
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        flyCam.setEnabled(false);

        // init game client
        GameClient client = new GameClient();
        stateManager.attach(client);
        client.connect("localhost", 5555);

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
        GameInitializer.initViewAppStates(stateManager);
        GameInitializer.initClientAppStates(stateManager);
        GameInitializer.initClientStatesWithGameSessionDependency(stateManager, gameSession);
        GameInitializer.initGameSessionRelatedAppStates(stateManager, gameSession);

        // create first person camera view
        stateManager.attach(new FirstPersonCameraView(gameSession.getPlayer(), new Vector3f(0, 1.7f, 0.2f)));
    }


}
