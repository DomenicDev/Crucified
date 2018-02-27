package de.gamedevbaden.crucified.appstates.gui;

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

        try {
            FileReader fr = new FileReader("Credits/Credits.txt");
            BufferedReader br = new BufferedReader(fr);

            String text = "";
            String line;
            while ((line = br.readLine()) != null) {
                text += line += "\n";
            }
            credits.getRenderer(TextRenderer.class).setText(text);

            fr.close();
            br.close();
        } catch (FileNotFoundException e) {
            Logger.getLogger(CreditsScreenController.class.getName()).log(Level.SEVERE, "could not load credits file!");
        } catch (IOException e) {
            Logger.getLogger(CreditsScreenController.class.getName()).log(Level.WARNING, "Error while reading file!");
        }

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
