package de.gamedevbaden.crucified.appstates.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.gamedevbaden.crucified.appstates.export.SettingsAppState;
import de.lessvoid.nifty.Nifty;

public class NiftyAppState extends AbstractAppState {

    private ViewPort guiViewPort;
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;

    private GuiEventListener listener;

    public enum NiftyScreen {

        MainMenu("mainMenu"),
        SettingsScreen("settingsScreen");

        NiftyScreen(String screenId) {
            this.screenId = screenId;
        }

        private String screenId;

        public String getScreenId() {
            return screenId;
        }
    }


    public NiftyAppState(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.guiViewPort = app.getGuiViewPort();

        SettingsAppState settingsAppState = stateManager.getState(SettingsAppState.class);

        app.getInputManager().setCursorVisible(true);
        this.niftyDisplay = new NiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        this.nifty = niftyDisplay.getNifty();
        this.nifty.fromXml("Interface/Screens/screens.xml", NiftyScreen.MainMenu.getScreenId(),
                new MainMenuScreenController(listener),
                new SettingsScreenController(settingsAppState));

        this.nifty.setDebugOptionPanelColors(false); // for debugging

        app.getGuiViewPort().addProcessor(niftyDisplay);
        super.initialize(stateManager, app);
    }

    public void goToScreen(NiftyScreen screen) {
        nifty.gotoScreen(screen.getScreenId());
    }

    @Override
    public void cleanup() {
        this.guiViewPort.removeProcessor(niftyDisplay);
        super.cleanup();
    }
}
