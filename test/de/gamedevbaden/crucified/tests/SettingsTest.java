package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import de.gamedevbaden.crucified.appstates.export.SettingsAppState;

public class SettingsTest extends SimpleApplication {

    public static void main(String[] args) {
        new SettingsTest().start();
    }

    @Override
    public void simpleInitApp() {
        SettingsAppState settingsAppState = new SettingsAppState();
        stateManager.attach(settingsAppState);
    }
}
