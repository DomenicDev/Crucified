package de.gamedevbaden.crucified.appstates.gui;

import de.gamedevbaden.crucified.enums.GameDecisionType;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class GameOverGuiController implements ScreenController {

    private GuiEventListener listener;

    private Element infoText;

    public GameOverGuiController(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.infoText = screen.findElementById("infoText");
    }

    public void setGameOver(GameDecisionType type) {
        if (type == GameDecisionType.MonsterWins) {
            infoText.getRenderer(TextRenderer.class).setText("The monster won the game!");
        } else {
            infoText.getRenderer(TextRenderer.class).setText("The survivor won the game!");
        }
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    // called by nifty
    public void returnToMainMenu() {
        listener.cancelNetworkGame();
    }
}
