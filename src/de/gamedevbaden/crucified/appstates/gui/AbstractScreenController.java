package de.gamedevbaden.crucified.appstates.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

/**
 * This class all screen controllers inherit from.
 * Subclasses can use the eventTracker to fire events.
 */
abstract class AbstractScreenController implements ScreenController {

    /**
     * The events of this interface will be called from the subclasses.
     */
    NiftyScreenEventTracker eventTracker;

    /**
     * Every subclass requires at least the event listener.
     * @param eventTracker the event tracker
     */
    AbstractScreenController(NiftyScreenEventTracker eventTracker) {
        this.eventTracker = eventTracker;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        // might be used in subclass
    }

    @Override
    public void onStartScreen() {
        // might be used in subclass
    }

    @Override
    public void onEndScreen() {
        // might be used in subclass
    }
}
