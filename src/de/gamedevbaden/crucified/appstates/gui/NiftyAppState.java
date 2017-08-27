package de.gamedevbaden.crucified.appstates.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;

public class NiftyAppState extends AbstractAppState implements NiftyScreenEventTracker {

    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private GuiEventListener listener;


    public NiftyAppState(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.niftyDisplay = new NiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        this.nifty = niftyDisplay.getNifty();
        this.nifty.fromXml("Interface/Screens/screens.xml", "mainMenu",
                new MainMenuScreenController(this));

        this.nifty.setDebugOptionPanelColors(true); // for debugging

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

    }

    @Override
    public void onClickStopGame() {
        listener.exitGame();
    }

    @Override
    public void onClickConnectToServer(String address) {

    }
}
