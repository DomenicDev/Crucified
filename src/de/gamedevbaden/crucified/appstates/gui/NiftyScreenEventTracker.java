package de.gamedevbaden.crucified.appstates.gui;

/**
 * This interface fires all kind of nifty gui events.
 * This interface is used and implemented by the {@link NiftyAppState}.
 */
public interface NiftyScreenEventTracker {

    /**
     * Called from main menu.
     * The player wants to play the singleplayer mode
     */
    void onClickStartSinglePlayerGame();

    /**
     * Called from main menu. Tells that this player
     * wants to host an coop session.
     */
    void onClickCreateCoopGame();

    /**
     * This is called when clicked on the button
     * "connectToGame" in the main menu
     */
    void onClickConnectToGame();

    /**
     * With this method the player wants to connect to
     * the game which runs on the pc of the following address
     * @param address the address the desired game runs.
     */
    void onClickConnectToServer(String address);

    void onClickBackToMainMenu();

    void onClickStopGame();



}
