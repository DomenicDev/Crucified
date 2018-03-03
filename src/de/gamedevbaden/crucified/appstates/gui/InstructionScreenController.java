package de.gamedevbaden.crucified.appstates.gui;

import de.gamedevbaden.crucified.utils.HelperMethods;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class InstructionScreenController implements ScreenController {

    private GuiEventListener listener;

    public InstructionScreenController(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        Element instructionsLabel = screen.findElementById("instructions");
        String text = HelperMethods.getTextFromFile("docs/Instructions.txt");
        instructionsLabel.getRenderer(TextRenderer.class).setText(text);

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void backToMainMenu() {
        this.listener.backToMainMenu();
    }
}
