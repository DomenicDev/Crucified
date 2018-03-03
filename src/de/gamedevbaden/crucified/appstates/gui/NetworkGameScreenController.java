package de.gamedevbaden.crucified.appstates.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkGameScreenController implements ScreenController {

    private GuiEventListener guiEventListener;
    private Nifty nifty;

    private boolean secondPlayerConnected = false;

    private Label secondPlayerStateLabel;
    private Button startGameButton;
    private Element sureAboutExitPopup;

    public NetworkGameScreenController(GuiEventListener listener) {
        this.guiEventListener = listener;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.secondPlayerStateLabel = nifty.getCurrentScreen().findNiftyControl("playerStateLabel", Label.class);
        this.startGameButton = nifty.getCurrentScreen().findNiftyControl("startGameButton", Button.class);
        this.sureAboutExitPopup = nifty.createPopup("popupReallyCancel");

        // local ip
        Element ipLabel = screen.findElementById("ipLabel");
        try {
            ipLabel.getRenderer(TextRenderer.class).setText("Local IP-Address: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartScreen() {
        setSecondPlayerConnected(false);
    }

    @Override
    public void onEndScreen() {
        setSecondPlayerConnected(false);
    }

    /**
     * This is called as soon as another player joins or leaves the game.
     * This will then activate the startGame button again to actually start the game session
     * or disables it if the player has left the current session.
     * @param connected the state change of the second player
     */
    public void setSecondPlayerConnected(boolean connected) {
        if (connected) {
            this.secondPlayerStateLabel.setText("Remote player has connected. Ready for Game start!");
            this.startGameButton.enable();
        } else {
            this.secondPlayerStateLabel.setText("Waiting for another Player ....");
            this.startGameButton.disable();
        }
    }

    /**
     * Is called when the user clicks on the button to start the network game session.
     * Can only be clicked when another player has joint this game session.
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
        this.guiEventListener.cancelNetworkGame();
    }

    /**
     * Is called when the user clicks the "no" button of the popup defined above
     */
    public void no() {
        this.nifty.closePopup(this.sureAboutExitPopup.getId());
    }
}
