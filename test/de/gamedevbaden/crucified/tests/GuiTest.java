package de.gamedevbaden.crucified.tests;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
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


//        stateManager.attach(new SettingsAppState());
//        stateManager.attach(new NiftyAppState(this));
//
//        this.mainGameAppState = new MainGameAppState();
//        stateManager.attach(mainGameAppState);

        stateManager.attach(new Initializer());
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

    @Override
    public void showCredits() {
        stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.CreditsScreen);
    }

    @Override
    public void backToMainMenu() {
        stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.MainMenu);
    }

    @Override
    public void showInstructions() {
        stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.InstructionsScreen);
    }

    private class Initializer extends AbstractAppState {

        private int x;

        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            this.x = 0;
            super.initialize(stateManager, app);
        }

        @Override
        public void update(float tpf) {
            switch (x++) {
                case 0:
                    stateManager.attach(new SettingsAppState());
                    break;
                case 1:
                    stateManager.attach(new NiftyAppState(GuiTest.this));
                    break;
                case 2:
                    mainGameAppState = new MainGameAppState();
                    stateManager.attach(mainGameAppState);
                    break;
                case 3:
                    stateManager.getState(NiftyAppState.class).goToScreen(NiftyAppState.NiftyScreen.MainMenu);
                    break;
                default:
                    stateManager.detach(this);
            }
        }
    }
}
