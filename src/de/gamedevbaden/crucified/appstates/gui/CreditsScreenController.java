package de.gamedevbaden.crucified.appstates.gui;

import de.gamedevbaden.crucified.utils.HelperMethods;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreditsScreenController implements ScreenController {

    private GuiEventListener listener;

    public CreditsScreenController(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        Element credits = screen.findElementById("credits");

        String text = HelperMethods.getTextFromFile("docs/Credits.txt");
        credits.getRenderer(TextRenderer.class).setText(text);
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void backToMainMenu() {
        listener.backToMainMenu();
    }
}
