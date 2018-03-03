package de.gamedevbaden.crucified.utils;

import de.gamedevbaden.crucified.appstates.gui.CreditsScreenController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelperMethods {

    public static final String getTextFromFile(String path) {
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);

            String text = "";
            String line;
            while ((line = br.readLine()) != null) {
                text += line + System.getProperty("line.separator");
            }

            fr.close();
            br.close();

            return text;
        } catch (FileNotFoundException e) {
            Logger.getLogger(CreditsScreenController.class.getName()).log(Level.SEVERE, "could not load credits file!");
        } catch (IOException e) {
            Logger.getLogger(CreditsScreenController.class.getName()).log(Level.WARNING, "Error while reading file!");
        }
        return null;
    }

}
