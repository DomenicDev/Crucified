package de.gamedevbaden.crucified.appstates.gui;

import de.gamedevbaden.crucified.appstates.export.SettingsAppState;
import de.gamedevbaden.crucified.enums.Quality;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.screen.Screen;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This screen controller applies the settings of the user by using
 * the {@link SettingsAppState}.
 */
public class SettingsScreenController extends AbstractScreenController {

    private SettingsAppState settingsAppState;

    private DropDown<String> resolutionBox;
    private DropDown<Quality> antiAliasingBox;

    private CheckBox vSyncCheckBox;
    private CheckBox fullScreenCheckBox;

    private Nifty nifty;

    SettingsScreenController(NiftyScreenEventTracker eventTracker, SettingsAppState settingsAppState) {
        super(eventTracker);
        this.settingsAppState = settingsAppState;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.resolutionBox = screen.findNiftyControl("resDropDown", DropDown.class);
        this.antiAliasingBox = screen.findNiftyControl("antiAliasingDropDown", DropDown.class);
        this.vSyncCheckBox = screen.findNiftyControl("vSyncCheckBox", CheckBox.class);
        this.fullScreenCheckBox = screen.findNiftyControl("fullScreenCheckBox", CheckBox.class);

        // fill resolution box with values
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();

        for (DisplayMode d : modes) {
            String res = makeResString(d);
            if (!this.resolutionBox.getItems().contains(res)) {
                this.resolutionBox.addItem(res);
            }
        }

        // fill anti aliasing quality box
        fillDropDown(antiAliasingBox, Quality.values());

    }

    @Override
    public void onStartScreen() {
        // select item for current resolution
        String currentRes = makeResString(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode());
        this.resolutionBox.selectItem(currentRes);

        // check checkbox if vSync is enabled
        vSyncCheckBox.setChecked(settingsAppState.isVSyncEnabled());

        // select current aa quality
        this.antiAliasingBox.selectItem(settingsAppState.getAntiAliasingQuality());

        // select current full screen mode
        this.fullScreenCheckBox.setChecked(settingsAppState.isFullscreen());
    }

    private <T> void fillDropDown(DropDown<T> dropDown, T[] values) {
        for (T value : values) {
            dropDown.addItem(value);
        }
    }

    private String makeResString(DisplayMode d) {
        return d.getWidth() + "x" + d.getHeight();
    }

    private int[] makeResInts(String res) {
        String[] r = res.split("x");
        int width = Integer.parseInt(r[0]);
        int height = Integer.parseInt(r[1]);
        return new int[] {width, height};
    }

    public void apply() {
        int[] res = makeResInts(this.resolutionBox.getSelection());
        //...

        // apply settings
        settingsAppState.setResolution(res[0], res[1]);
        settingsAppState.setAntiAliasingQuality(antiAliasingBox.getSelection());
        settingsAppState.setVsync(vSyncCheckBox.isChecked());
        settingsAppState.setFullscreen(fullScreenCheckBox.isChecked());

        // finally apply
        settingsAppState.applyToAppSettings();

    }

    public void cancel() {
        eventTracker.onClickBackToMainMenu();
    }
}
