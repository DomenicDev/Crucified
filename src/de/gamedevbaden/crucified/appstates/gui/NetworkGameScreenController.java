package de.gamedevbaden.crucified.appstates.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class NetworkGameScreenController implements ScreenController {

    private GuiEventListener guiEventListener;
    private Nifty nifty;

    private Element sureAboutExitPopup;

    public NetworkGameScreenController(GuiEventListener listener) {
        this.guiEventListener = listener;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.sureAboutExitPopup = nifty.createPopup("popupReallyCancel");
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    /**
     * Is called when the user clicks on the button to start the network game session.
     */
    public void startNetworkGame() {
        // the following call tells the game main state to start the game session
        // we then will switch to a loading screen and load the game
        this.guiEventListener.startNetworkGame();
    }

    /**
     * This is called when the user clicks the cancel button.
     * We show a popup and aks the user if he really wants to exit.
     */
    public void cancel() {
        this.nifty.showPopup(this.nifty.getCurrentScreen(), this.sureAboutExitPopup.getId(), null);
    }

    /**
     * Is called when the user clicks the "yes" button of the popup defined above
     */
    public void yes() {
        this.nifty.closePopup(this.sureAboutExitPopup.getId());
        this.nifty.gotoScreen(NiftyAppState.NiftyScreen.MainMenu.getScreenId());
    }

    /**
     * Is called when the user clicks the "no" button of the popup defined above
     */
    public void no() {
        this.nifty.closePopup(this.sureAboutExitPopup.getId());
    }
}
