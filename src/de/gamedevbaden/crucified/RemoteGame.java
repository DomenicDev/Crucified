package de.gamedevbaden.crucified;

import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.appstates.AbstractGame;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.appstates.gui.NiftyAppState;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.client.GameClient;
import de.gamedevbaden.crucified.net.messages.ReadyForGameStartMessage;
import de.gamedevbaden.crucified.utils.GameInitializer;

public class RemoteGame extends AbstractGame {

    private GameClient client;
    private GameCommanderAppState commander;

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
        GameSession gameSession = client.getGameSession();

        // create entity data state with remote entity data
        stateManager.attach(new EntityDataState(entityData));

        // create our game session app states
        stateManager.attach(new PlayerInteractionState());
        stateManager.attach(new GameEventAppState());

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
        //     GameInitializer.initInputAppStates(stateManager);
        //     GameInitializer.initClientStatesWithGameSessionDependency(stateManager, stateManager.getState(GameSessionAppState.class).getGameSession());
        //     GameInitializer.initFirstPersonCameraView(stateManager);

        stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.EmptyScreen);
    }
}