package de.gamedevbaden.crucified.appstates.gui;

public class MainMenuScreenController extends AbstractScreenController {

    MainMenuScreenController(NiftyScreenEventTracker eventTracker) {
        super(eventTracker);
    }

    public void startSingleplayerGame() {
        eventTracker.onClickStartSinglePlayerGame();
    }

    public void createCoopGame() {
        eventTracker.onClickCreateCoopGame();
    }

    public void connectToGame() {
        eventTracker.onClickConnectToGame();
    }

    public void quitGame() {
        eventTracker.onClickStopGame();
    }

}
