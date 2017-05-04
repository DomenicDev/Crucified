package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.ObjectCategory;

/**
 * Created by Domenic on 30.04.2017.
 */
public class CharacterEquipmentState implements EntityComponent {

    private ObjectCategory equippedObject;

    public CharacterEquipmentState() {
    }

    public CharacterEquipmentState(ObjectCategory equippedObject) {
        this.equippedObject = equippedObject;
    }

    public ObjectCategory getEquippedObject() {
        return equippedObject;
    }
}
