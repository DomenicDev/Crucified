package de.gamedevbaden.crucified;

import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.appstates.AbstractGame;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.appstates.gui.NiftyAppState;
import de.gamedevbaden.crucified.appstates.net.PredictionAppState;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.client.GameClient;
import de.gamedevbaden.crucified.net.messages.ReadyForGameStartMessage;
import de.gamedevbaden.crucified.utils.GameInitializer;

public class RemoteGame extends AbstractGame {

    private GameClient client;
    private GameCommanderAppState commander;
    private GameSession gameSession;

    public RemoteGame(GameClient client) {
        this.client = client;
    }

    public RemoteGame(GameClient client, GameCommanderAppState commander) {
        this.client = client;
        this.commander = commander;
    }

    @Override
    public void setupGame() {
        stateManager.attach(commander);

        EntityData entityData = client.getEntityData();
        this.gameSession = client.getGameSession();

        // create entity data state with remote entity data
        stateManager.attach(new EntityDataState(entityData));



        // init game and view states
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

    @Override
    public void onGameStart() {

        // create our game session app states
        stateManager.attach(new PlayerInteractionState());




        stateManager.attach(new GameEventAppState());
        //     GameInitializer.initInputAppStates(stateManager);
        //     GameInitializer.initClientStatesWithGameSessionDependency(stateManager, stateManager.getState(GameSessionAppState.class).getGameSession());
        //     GameInitializer.initFirstPersonCameraView(stateManager);

        stateManager.getState(PredictionAppState.class).initPredictionForPlayer();

        stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.EmptyScreen);
    }

    @Override
    public void cleanup() {
        stateManager.detach(client);
        stateManager.detach(stateManager.getState(GameCommanderAppState.class));
        stateManager.detach(stateManager.getState(EntityDataState.class));

        GameInitializer.removeEssentialAppStates(stateManager);
        GameInitializer.removeInputAppStates(stateManager);
        GameInitializer.removeGameSessionRelatedAppStates(stateManager);
        GameInitializer.removeViewAppStates(stateManager);
        GameInitializer.removeSoundAppStates(stateManager);
        GameInitializer.removeClientAppStates(stateManager);
        GameInitializer.removeClientStatesWithGameSessionDependency(stateManager);
        GameInitializer.removePlayerStates(stateManager);
        GameInitializer.removeFirstPersonCameraView(stateManager);

        stateManager.detach(stateManager.getState(PlayerInteractionState.class));
        stateManager.detach(stateManager.getState(GameEventAppState.class));

        super.cleanup();
    }
}
