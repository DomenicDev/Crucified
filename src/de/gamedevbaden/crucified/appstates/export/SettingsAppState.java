package de.gamedevbaden.crucified.appstates.export;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.system.AppSettings;
import de.gamedevbaden.crucified.enums.Quality;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsAppState extends AbstractAppState {

    private Application app;
    private AppSettings appSettings;
    private Properties settings;

    private static final String SETTINGS_FILE_PATH = "settings/settings.properties";

    // we save some of the keys of the properties file here
    private static final String SHADOW_QUALITY = "shadowQuality";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String V_SYNC = "vsync";
    private static final String AA_Quality = "antialiasing";
    private static final String FULLSCREEN = "fullscreen";

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
        this.appSettings = app.getContext().getSettings();

        // load settings file
        try {
            FileInputStream input = new FileInputStream(new File(SETTINGS_FILE_PATH));
            this.settings = new Properties();
            this.settings.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //    applyToAppSettings();

        super.initialize(stateManager, app);
    }

    public void applyToAppSettings() {
        appSettings.setResolution(getWidth(), getHeight());
        appSettings.setVSync(isVSyncEnabled());
        appSettings.setFullscreen(isFullscreen());
        appSettings.setSamples(1); // always will stay the same (because of bug in
        appSettings.setGammaCorrection(true); // force gamma correction

        appSettings.setBitsPerPixel(24); // hard coded
        appSettings.setFrequency(60); // hard coded, we have to set it otherwise app crashes
        this.app.restart();
    }

    private int getSamples(Quality aaQuality) {
        switch (aaQuality) {
            case Off: return 1;
            case Low: return 2;
            case Medium: return 4;
            case High: return 8;
            case VeryHigh: return 16;
            default: return 1;
        }
    }


    // --------- RESOLUTION -------------------- //

    public void setResolution(int width, int height) {
        storeProperty(WIDTH, width);
        storeProperty(HEIGHT, height);
    }

    public int[] getResolution() {
        return new int[] {getWidth(), getHeight()};
    }

    public int getWidth() {
        return getIntProperty(WIDTH);
    }

    public int getHeight() {
        return getIntProperty(HEIGHT);
    }

    // ------------ SHADOW QUALITY --------------------//

    public void setShadowQuality(Quality q) {
        storeProperty(SHADOW_QUALITY, q);
    }

    public Quality getShadowQuality() {
        return getEnumProperty(SHADOW_QUALITY, Quality.class);
    }

    // -------------- VSYNC ----------------- //

    public void setVsync(boolean enabled) {
        storeProperty(V_SYNC, enabled);
    }

    public boolean isVSyncEnabled() {
        return getBooleanProperty(V_SYNC);
    }

    // ---------- FULLSCREEN -------------- //

    public void setFullscreen(boolean enabled) {
        storeProperty(FULLSCREEN, enabled);
    }

    public boolean isFullscreen() {
        return getBooleanProperty(FULLSCREEN);
    }

    //------------ AA_Quality (ANTI ALIASING) ----------- //

    public void setAntiAliasingQuality(Quality q) {
        storeProperty(AA_Quality, q);
    }

    public Quality getAntiAliasingQuality() {
        return getEnumProperty(AA_Quality, Quality.class);
    }

   private <T extends Enum<T>> T getEnumProperty(String key, Class<T> c) {
        String v = getStringProperty(key);
        return Enum.valueOf(c, v);
   }

    private int getIntProperty(String key) {
        return Integer.parseInt(getStringProperty(key));
    }

    private String getStringProperty(String key) {
        return this.settings.getProperty(key);
    }

    private boolean getBooleanProperty(String key) {
        String v = getStringProperty(key);
        return v.equals("1");
    }

    private void storeProperty(String key, boolean enabled) {
        storeProperty(key, enabled ? "1" : "0");
    }

    private void storeProperty(String key, int value) {
        storeProperty(key, String.valueOf(value));
    }

    private void storeProperty(String key, Enum enumValue) {
        storeProperty(key, enumValue.name());
    }

    private void storeProperty(String key, String value) {
        try {
            FileOutputStream out = new FileOutputStream(SETTINGS_FILE_PATH);
            this.settings.setProperty(key, value);
            this.settings.store(out, null);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
