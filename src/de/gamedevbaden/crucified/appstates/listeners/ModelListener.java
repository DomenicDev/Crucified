package de.gamedevbaden.crucified.appstates.listeners;

import com.jme3.scene.Spatial;

/**
 * This interface is used by {@link de.gamedevbaden.crucified.appstates.view.ModelViewAppState} to inform other classes
 * about events concerning models (spatial).
 * Created by Domenic on 26.06.2017.
 */
public interface ModelListener {

    void onModelAttached(Spatial spatial);

}
