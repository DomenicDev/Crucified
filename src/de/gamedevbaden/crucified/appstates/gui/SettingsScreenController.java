package de.gamedevbaden.crucified.appstates.gui;

import de.gamedevbaden.crucified.appstates.export.SettingsAppState;
import de.gamedevbaden.crucified.enums.Quality;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This screen controller applies the settings of the user by using
 * the {@link SettingsAppState}.
 */
public class SettingsScreenController implements ScreenController {

    private SettingsAppState settingsAppState;

    private DropDown<String> resolutionBox;
    private DropDown<Quality> antiAliasingBox;

    private CheckBox vSyncCheckBox;
    private CheckBox fullScreenCheckBox;

    private Element settingsAppliedPopup;

    private Nifty nifty;

    SettingsScreenController(SettingsAppState settingsAppState) {
        this.settingsAppState = settingsAppState;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.resolutionBox = screen.findNiftyControl("resDropDown", DropDown.class);
        this.antiAliasingBox = screen.findNiftyControl("antiAliasingDropDown", DropDown.class);
        this.vSyncCheckBox = screen.findNiftyControl("vSyncCheckBox", CheckBox.class);
        this.fullScreenCheckBox = screen.findNiftyControl("fullScreenCheckBox", CheckBox.class);

        // create popup instance
        this.settingsAppliedPopup = nifty.createPopup("popupSettingsApplied");

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

    @Override
    public void onEndScreen() {}

    /**
     * A simple helper method which fills the specified drop down element
     * with the specified values
     * @param dropDown the drop down element
     * @param values the values to fill the drop down element with
     * @param <T> the type of the values
     */
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

    /**
     * Is called when the user clicks on the apply button
     */
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

        // show popup
        nifty.showPopup(nifty.getCurrentScreen(), settingsAppliedPopup.getId(), null);
    }

    /**
     * this is called when the user clicks on the "ok" button of the popup
     */
    public void ok() {
        this.nifty.closePopup(settingsAppliedPopup.getId());
        this.nifty.gotoScreen(NiftyAppState.NiftyScreen.MainMenu.getScreenId());
    }

    /**
     * Is called when the user clicks on the cancel button
     */
    public void cancel() {
        this.nifty.gotoScreen(NiftyAppState.NiftyScreen.MainMenu.getScreenId());
    }
}
