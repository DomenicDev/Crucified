package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.EquipmentLocation;

/**
 * Is used as a marker.
 * Entities with this component can be equipped to the players hand for example.
 * Created by Domenic on 20.05.2017.
 */
@Serializable
public class Equipable implements EntityComponent {

    private EquipmentLocation equipmentLocation;

    public Equipable() {
    }

    public Equipable(EquipmentLocation equipmentLocation) {
        this.equipmentLocation = equipmentLocation;
    }

    public EquipmentLocation getEquipmentLocation() {
        return equipmentLocation;
    }
}
