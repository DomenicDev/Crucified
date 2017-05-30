package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.GameCommanderCollector;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.appstates.view.FirstPersonCameraView;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameCommanderAppState;
import de.gamedevbaden.crucified.game.GameEventHandler;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.game.GameSessionManager;
import de.gamedevbaden.crucified.utils.GameInitializer;

/**
 * Created by Domenic on 21.05.2017.
 */
public class SingleplayerTest extends SimpleApplication {

    public static void main(String[] args) {
        new SingleplayerTest().start();
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        flyCam.setEnabled(false);

        //  GameOptions.ENABLE_PHYSICS_DEBUG = true; // for test

        // create entity data state
        EntityDataState entityDataState = new EntityDataState();
        stateManager.attach(entityDataState);
        EntityData entityData = entityDataState.getEntityData();

        // create game commander handler
        GameCommanderAppState commanderAppState = new GameCommanderAppState();
        stateManager.attach(commanderAppState);

        // load test scene
        stateManager.attach(new SceneEntityLoader());

        GameCommanderCollector collector = new GameCommanderCollector();
        stateManager.attach(collector);
        collector.addGameCommander(commanderAppState);

        // create session manager to create a session for a single player
        GameSessionManager sessionManager = new GameSessionManager();
        stateManager.attach(sessionManager);

        stateManager.attach(new GameEventHandler(sessionManager));

        // create GameSession for our player
        GameSession gameSession = sessionManager.createSession(EntityFactory.createPlayer(entityData));

        // create our game session app states
        stateManager.attach(new PlayerInteractionState());
        stateManager.attach(new GameEventAppState());

        // init game logic states
        GameInitializer.initEssentialAppStates(stateManager);
        GameInitializer.initGameLogicAppStates(stateManager);
        GameInitializer.initViewAppStates(stateManager);
        GameInitializer.initInputAppStates(stateManager);
        GameInitializer.initGameSessionRelatedAppStates(stateManager, gameSession);


        // create first person cam view
        stateManager.attach(new FirstPersonCameraView(gameSession.getPlayer(), new Vector3f(0, 1.7f, 0.2f))); // 0,1.7,0


    }

}
