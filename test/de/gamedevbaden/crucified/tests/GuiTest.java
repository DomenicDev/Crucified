package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import de.gamedevbaden.crucified.MainGameAppState;
import de.gamedevbaden.crucified.appstates.export.SettingsAppState;
import de.gamedevbaden.crucified.appstates.gui.GuiEventListener;
import de.gamedevbaden.crucified.appstates.gui.NiftyAppState;

public class GuiTest extends SimpleApplication implements GuiEventListener {

    private MainGameAppState mainGameAppState;

    public static void main(String[] args) {
        new GuiTest().start();
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        flyCam.setEnabled(false);


        stateManager.attach(new SettingsAppState());
        stateManager.attach(new NiftyAppState(this));

        this.mainGameAppState = new MainGameAppState();
        stateManager.attach(mainGameAppState);
    }


    @Override
    public void exitGame() {
        stop();
    }

    @Override
    public void startSinglePlayerGame() {

    }

    @Override
    public void createNetworkGame() {
        mainGameAppState.hostGame();
    }

    @Override
    public void connectToNetworkGame(String ipAddress) {
        mainGameAppState.connectToGame(ipAddress);
    }

    @Override
    public void cancelNetworkGame() {
        mainGameAppState.closeExistingGame();
        stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.MainMenu);
    }

    @Override
    public void startNetworkGame() {
        mainGameAppState.startGame();
    }
}
