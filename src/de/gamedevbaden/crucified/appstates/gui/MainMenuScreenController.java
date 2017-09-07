package de.gamedevbaden.crucified.appstates.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class MainMenuScreenController implements ScreenController {

    private Nifty nifty;
    private GuiEventListener guiEventListener;

    MainMenuScreenController(GuiEventListener guiEventListener) {
        this.guiEventListener = guiEventListener;
    }

    public void startSingleplayerGame() {
        this.guiEventListener.startSinglePlayerGame();
    }

    public void createCoopGame() {
        // todo
    }

    public void connectToGame() {
        // todo
    }

    public void showSettings() {
        nifty.gotoScreen(NiftyAppState.NiftyScreen.SettingsScreen.getScreenId());
    }

    public void showCredits() {
        // todo
    }

    public void quitGame() {
        this.guiEventListener.exitGame();
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
}
