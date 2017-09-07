package de.gamedevbaden.crucified.appstates.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.gamedevbaden.crucified.appstates.export.SettingsAppState;
import de.lessvoid.nifty.Nifty;

public class NiftyAppState extends AbstractAppState implements NiftyScreenEventTracker {

    private ViewPort guiViewPort;
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;

    private GuiEventListener listener;

    private final String MAIN_MENU_SCREEN = "mainMenu";
    private final String SETTINGS_SCREEN = "settingsScreen";

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
        this.nifty.fromXml("Interface/Screens/screens.xml", MAIN_MENU_SCREEN,
                new MainMenuScreenController(this),
                new SettingsScreenController(this, settingsAppState));

        this.nifty.setDebugOptionPanelColors(false); // for debugging

        app.getGuiViewPort().addProcessor(niftyDisplay);
        super.initialize(stateManager, app);
    }

    @Override
    public void onClickStartSinglePlayerGame() {

    }

    @Override
    public void onClickCreateCoopGame() {

    }

    @Override
    public void onClickConnectToGame() {

    }

    @Override
    public void onClickBackToMainMenu() {
        nifty.gotoScreen(MAIN_MENU_SCREEN);
    }

    @Override
    public void onClickStopGame() {
        listener.exitGame();
    }

    @Override
    public void onClickShowSettings() {
        nifty.gotoScreen(SETTINGS_SCREEN);
    }

    @Override
    public void onClickShowCredits() {

    }

    @Override
    public void onClickConnectToServer(String address) {

    }

    @Override
    public void cleanup() {
        this.guiViewPort.removeProcessor(niftyDisplay);
        super.cleanup();
    }
}
