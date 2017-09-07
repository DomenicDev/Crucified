package de.gamedevbaden.crucified.appstates.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class ConnectionScreenController implements ScreenController {

    private Nifty nifty;
    private TextField ipTextField;
    private GuiEventListener listener;

    ConnectionScreenController(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.ipTextField = screen.findNiftyControl("ipTextField", TextField.class);
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void connect() {
        String ipAddress = this.ipTextField.getRealText();
        listener.connectToNetworkGame(ipAddress);
    }

    public void cancel() {
        this.nifty.gotoScreen(NiftyAppState.NiftyScreen.MainMenu.getScreenId());
    }
}
