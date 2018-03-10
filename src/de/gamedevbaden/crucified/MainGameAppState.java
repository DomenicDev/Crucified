package de.gamedevbaden.crucified;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.gamedevbaden.crucified.appstates.AbstractGame;
import de.gamedevbaden.crucified.appstates.HostedGame;
import de.gamedevbaden.crucified.appstates.ScenePreloader;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.appstates.gui.NiftyAppState;
import de.gamedevbaden.crucified.net.client.GameClient;

public class MainGameAppState extends AbstractAppState {

    private AbstractGame game;
    private AppStateManager stateManager;


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.stateManager.attach(new ScenePreloader(app.getAssetManager()));
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
        stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.MainMenu);
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
            stateManager.attach(client);
            this.game = new RemoteGame(client, commander);
            stateManager.attach(game);
        }
    }

    @Override
    public void cleanup() {
        this.stateManager.detach(stateManager.getState(ScenePreloader.class));
        super.cleanup();
    }
}