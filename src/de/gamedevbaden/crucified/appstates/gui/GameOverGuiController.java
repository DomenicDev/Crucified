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

    private GameDecisionType type;

    public GameOverGuiController(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.infoText = screen.findElementById("infoText");
        refreshText();
    }

    public void setGameOver(GameDecisionType type) {
        this.type = type;
        refreshText();
    }

    @Override
    public void onStartScreen() {
        refreshText();
    }

    @Override
    public void onEndScreen() {

    }

    private void refreshText() {
        if (type != null && infoText != null) {
            if (type == GameDecisionType.MonsterWins) {
                infoText.getRenderer(TextRenderer.class).setText("The monster won the game!");
            } else {
                infoText.getRenderer(TextRenderer.class).setText("The survivor won the game!");
            }
        }
    }

    // called by nifty
    public void returnToMainMenu() {
        listener.cancelNetworkGame();
    }
}
