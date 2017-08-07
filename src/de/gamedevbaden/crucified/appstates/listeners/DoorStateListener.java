package de.gamedevbaden.crucified.appstates.listeners;

import com.simsilica.es.EntityId;

public interface DoorStateListener {

    void onStateChanged(EntityId entityId, boolean open);

}
