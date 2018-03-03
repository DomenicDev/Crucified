package de.gamedevbaden.crucified.appstates.gui;

import com.jme3.input.InputManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class ReturnToMainMenuScreenController implements ScreenController {

    private GuiEventListener listener;
    private InputManager inputManager;
    private Nifty nifty;

    public ReturnToMainMenuScreenController(GuiEventListener listener, InputManager inputManager) {
        this.listener = listener;
        this.inputManager = inputManager;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
        this.inputManager.setCursorVisible(true);
    }

    @Override
    public void onEndScreen() {

    }

    public void yes() {
        listener.cancelNetworkGame();
        this.inputManager.setCursorVisible(true);
    }

    public void no() {
        nifty.gotoScreen(NiftyAppState.NiftyScreen.EmptyScreen.getScreenId());
        this.inputManager.setCursorVisible(false);
    }
}
