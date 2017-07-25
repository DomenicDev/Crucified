package de.gamedevbaden.crucified.appstates.listeners;

import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.InputCommand;
import de.gamedevbaden.crucified.utils.PlayerInputCollector;

/**
 * A listener who listens for input changes.
 * Created by Domenic on 01.05.2017.
 */
public interface InputChangeListener {

    void onInputChange(EntityId entityId, InputCommand changedInput, PlayerInputCollector playerInputCollector);

}
