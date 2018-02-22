package de.gamedevbaden.crucified;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.gamedevbaden.crucified.appstates.AbstractGame;
import de.gamedevbaden.crucified.appstates.HostedGame;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.net.client.GameClient;

public class MainGameAppState extends AbstractAppState {

    private AbstractGame game;
    private AppStateManager stateManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        super.initialize(stateManager, app);
    }

    public void hostGame() {
        if (game != null) {
            closeExistingGame();
        }
        this.game = new HostedGame();
        stateManager.attach(game);
    }

    public void closeExistingGame() {
        if (game != null) {
            stateManager.detach(game);
            game = null;
        }
    }

    public void startGame() {
        if (game != null) {
            game.onGameStart();
        }
    }

    public void connectToGame(String address) {
        GameCommanderAppState commander = new GameCommanderAppState();
        GameClient client = new GameClient();
        if (client.connect(address, 5555, stateManager.getApplication(), commander)) {
            this.game = new RemoteGame(client, commander);
            stateManager.attach(game);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}