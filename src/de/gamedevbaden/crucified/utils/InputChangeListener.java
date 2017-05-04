package de.gamedevbaden.crucified.utils;

import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.enums.InputMapping;

/**
 * A listener who listens for input changes.
 * Created by Domenic on 01.05.2017.
 */
public interface InputChangeListener {

    void onInputChange(EntityId entityId, InputMapping changedInput, PlayerInputCollector playerInputCollector);

}
