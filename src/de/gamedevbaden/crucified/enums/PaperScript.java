package de.gamedevbaden.crucified.enums;

/**
 * In here we collect all scripts which can be read by the player.
 * Each object references a key for the real text which is stored in a XML file.
 * <p>
 * Created by Domenic on 08.06.2017.
 */
public enum PaperScript {

    TestText("testText");


    private String key;

    PaperScript(String keyToText) {
        this.key = keyToText;
    }

    /**
     * Returns the key for this text.
     * Use this key to get the real text from the properties file.
     *
     * @return the key for this text in the properties file
     */
    public String getKey() {
        return key;
    }
}
