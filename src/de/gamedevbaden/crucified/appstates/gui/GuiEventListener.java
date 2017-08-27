package de.gamedevbaden.crucified.appstates.gui;

/**
 * The events of this class are fired by the {@link NiftyAppState}.
 * Those events are used by another app state which in turn is responsible
 * for the overall game state.
 */
public interface GuiEventListener {

    void exitGame();

    void startSinglePlayerGame();
}
