package de.gamedevbaden.crucified.es.components;

import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.ActionType;

/**
 * This component says what actions can be performed by
 * the entity
 */
@Serializable
public class ActionGroupComponent implements EntityComponent {

    private ActionType[] actions;

    public ActionGroupComponent() {
    }

    public ActionGroupComponent(ActionType... actions) {
        this.actions = actions;
    }

    public boolean contains(ActionType type) {
        if (actions == null) return false;
        for (ActionType t : actions) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }

    public ActionType[] getActions() {
        return actions;
    }
}
