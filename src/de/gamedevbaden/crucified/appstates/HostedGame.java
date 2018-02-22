package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventHandler;
import de.gamedevbaden.crucified.appstates.game.GameSessionManager;
import de.gamedevbaden.crucified.appstates.gui.NiftyAppState;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameCommander;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.messages.StartGameMessage;
import de.gamedevbaden.crucified.net.server.GameServer;
import de.gamedevbaden.crucified.utils.GameInitializer;

import static de.gamedevbaden.crucified.appstates.SceneEntityLoader.sceneToLoad;

public class HostedGame extends AbstractGame {

    private EntityId localPlayer;

    @Override
    public void setupGame() {
        GameServer server = new GameServer(5555);
        stateManager.attach(server);

        // create entity data state
        EntityDataState entityDataState = new EntityDataState();
        stateManager.attach(entityDataState);
        EntityData entityData = entityDataState.getEntityData();

        // create game commander handler
        GameCommanderAppState commanderAppState = new GameCommanderAppState();
        stateManager.attach(commanderAppState);

        // load test scene
        stateManager.attach(new SceneEntityLoader());

        // create session manager to create a session for a single player
        GameSessionManager sessionManager = new GameSessionManager();
        stateManager.attach(sessionManager);

        this.localPlayer = entityData.createEntity();

        // create GameSession for our player
        GameSession gameSession = sessionManager.createSession(localPlayer);

        // create GameCommanderHolder
        GameCommanderHolder commanderHolder = new GameCommanderHolder();
        stateManager.attach(commanderHolder);
        commanderHolder.add(gameSession.getPlayer(), commanderAppState); // that's the only one we need to add


        // init game logic states
        GameInitializer.initEssentialAppStates(stateManager);
        GameInitializer.initGameSessionRelatedAppStates(stateManager, gameSession);

        // GameInitializer.initThirdPersonCameraView(stateManager);


    }

    @Override
    public void onGameStart() {
        // create our game session app states
        stateManager.attach(new PlayerInteractionState());
        stateManager.attach(new GameEventAppState());

        stateManager.attach(new GameEventHandler(stateManager.getState(GameSessionManager.class)));

        GameInitializer.initGameLogicAppStates(stateManager);
        GameInitializer.initViewAppStates(stateManager);
        GameInitializer.initSoundAppStates(stateManager);
        GameInitializer.initPlayerStates(stateManager);
        GameInitializer.initInputAppStates(stateManager);


        stateManager.attach(new Loader());
    }

    private class Loader extends AbstractAppState {
        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            EntityFactory.createPlayer(stateManager.getState(EntityDataState.class).getEntityData(), localPlayer);

            stateManager.getState(SceneEntityLoader.class).createEntitiesFromScene(sceneToLoad);

            for (GameCommander commander : stateManager.getState(GameCommanderHolder.class).getAll()) {
                commander.loadScene(SceneEntityLoader.sceneToLoad);
            }

            stateManager.getState(GameServer.class).getServer().broadcast(new StartGameMessage());

            GameInitializer.initFirstPersonCameraView(stateManager);

            stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.EmptyScreen);
        }
    }
}
