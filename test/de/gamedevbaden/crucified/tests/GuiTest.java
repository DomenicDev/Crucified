package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import de.gamedevbaden.crucified.appstates.export.SettingsAppState;
import de.gamedevbaden.crucified.appstates.gui.GuiEventListener;
import de.gamedevbaden.crucified.appstates.gui.NiftyAppState;

public class GuiTest extends SimpleApplication implements GuiEventListener {

    public static void main(String[] args) {
        new GuiTest().start();
    }

    @Override
    public void simpleInitApp() {
        stateManager.attach(new SettingsAppState());
        stateManager.attach(new NiftyAppState(this));
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

    }

    @Override
    public void connectToNetworkGame(String ipAddress) {

    }

    @Override
    public void cancelNetworkGame() {

    }

    @Override
    public void startNetworkGame() {

    }
}
