package de.gamedevbaden.crucified.appstates.gui;

public interface GuiEventListener {

    void exitGame();

    void startSinglePlayerGame();

    void createNetworkGame();

    void connectToNetworkGame(String ipAddress);

    void cancelNetworkGame();

    void startNetworkGame();

    void showCredits();

    void backToMainMenu();

}
