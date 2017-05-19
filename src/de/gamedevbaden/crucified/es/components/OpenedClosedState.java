package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 13.05.2017.
 */
public class OpenedClosedState implements EntityComponent {

    private boolean opened;

    public OpenedClosedState() {
    }

    public OpenedClosedState(boolean opened) {
        this.opened = opened;
    }

    public boolean isOpened() {
        return opened;
    }
}
