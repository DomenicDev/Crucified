package de.gamedevbaden.crucified.appstates.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.gamedevbaden.crucified.appstates.export.SettingsAppState;
import de.gamedevbaden.crucified.enums.GameDecisionType;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class NiftyAppState extends AbstractAppState implements ActionListener {

    private ViewPort guiViewPort;
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;

    private Element popupPlayerWins, popupMonsterWins;
    private Element popupReturnToMainMenu;

    private InputManager inputManager;

    private GuiEventListener listener;

    public <T extends ScreenController> T getController(Class<T> c) {
        for (NiftyScreen s : NiftyScreen.values()) {
            String id = s.getScreenId();
            Screen screen = nifty.getScreen(id);
            if (screen != null) {
                ScreenController controller = screen.getScreenController();
                if (c.isAssignableFrom(controller.getClass())) {
                    return (T) controller;
                }
            }
        }
        return null;
    }

    public NiftyAppState(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.guiViewPort = app.getGuiViewPort();
        this.inputManager = app.getInputManager();

        SettingsAppState settingsAppState = stateManager.getState(SettingsAppState.class);

        app.getInputManager().setCursorVisible(true);
        this.niftyDisplay = new NiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        this.nifty = niftyDisplay.getNifty();
        this.nifty.fromXml("Interface/Screens/screens.xml", NiftyScreen.MainMenu.getScreenId(),
                new MainMenuScreenController(listener),
                new SettingsScreenController(settingsAppState),
                new NetworkGameScreenController(listener),
                new ConnectionScreenController(listener),
                new GameOverGuiController(listener),
                new ReturnToMainMenuScreenController(listener, inputManager),
                new CreditsScreenController(listener));

        this.nifty.setDebugOptionPanelColors(false); // for debugging

        // create popups
        this.popupMonsterWins = nifty.createPopup("popupMonsterWin");
        this.popupPlayerWins = nifty.createPopup("popupPlayerWin");
        this.popupReturnToMainMenu = nifty.createPopup("popupReallyCancel");


        // remove default escape action
        app.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        // therefore we add our own routine
        app.getInputManager().addMapping("ESC", new KeyTrigger(KeyInput.KEY_ESCAPE));
        app.getInputManager().addListener(this, "ESC");

        app.getGuiViewPort().addProcessor(niftyDisplay);
        super.initialize(stateManager, app);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        // this method handles the ESC key
        if (!isPressed) {
            return;
        }

        String currentScreenId = nifty.getCurrentScreen().getScreenId();

        if (currentScreenId.equals(NiftyScreen.MainMenu.getScreenId())) {
            listener.exitGame();
        } else if (currentScreenId.equals(NiftyScreen.NetworkGameScreen.getScreenId())) {
            // already server running, so we need to stop all and return to main menu
            // for that we just need to call the cancel() of this controller class
            getController(NetworkGameScreenController.class).cancel();
        } else if (currentScreenId.equals(NiftyScreen.ConnectionScreen.getScreenId())) {
            getController(ConnectionScreenController.class).cancel();
        } else if (currentScreenId.equals(NiftyScreen.EmptyScreen.screenId)) {
            goToScreen(NiftyScreen.ReturnToMainMenuScreen);
        }
    }

    public enum NiftyScreen {

        MainMenu("mainMenu"),
        SettingsScreen("settingsScreen"),
        NetworkGameScreen("networkGameScreen"),
        ConnectionScreen("connectionScreen"),
        CreditsScreen("creditsScreen"),
        EmptyScreen("emptyScreen"),
        LoadingScreen("loadingScreen"),
        GameOverScreen("gameOverScreen"),
        ReturnToMainMenuScreen("reallyReturnToMainMenuScreen");

        NiftyScreen(String screenId) {
            this.screenId = screenId;
        }

        private String screenId;

        public String getScreenId() {
            return screenId;
        }
    }

    public void showPopup(GameDecisionType type) {
        this.inputManager.setCursorVisible(true);
        if (type == GameDecisionType.MonsterWins) {
            nifty.showPopup(nifty.getCurrentScreen(), this.popupMonsterWins.getId(), null);
        } else if (type == GameDecisionType.HumanPlayersWin) {
            nifty.showPopup(nifty.getCurrentScreen(), this.popupPlayerWins.getId(), null);
        }
    }

    public void showGameOverScreen(GameDecisionType type) {
        inputManager.setCursorVisible(true);
        goToScreen(NiftyScreen.GameOverScreen);
        getController(GameOverGuiController.class).setGameOver(type);
    }

    /**
     * This is called by the popups shown after
     * the game is over (decided)
     */
    public void gameOverPopup() {
        this.nifty.closePopup(popupPlayerWins.getId());
        this.nifty.closePopup(popupMonsterWins.getId());
        listener.cancelNetworkGame();
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
