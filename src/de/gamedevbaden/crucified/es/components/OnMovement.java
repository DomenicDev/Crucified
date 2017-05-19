package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 * Tells the ES that this entity is on movement right now.
 * Could be used as a marker to indicate entities which need to be interpolated (on client side).
 * <p>
 * Created by Domenic on 26.04.2017.
 */
@Serializable
public class OnMovement implements EntityComponent {
}
