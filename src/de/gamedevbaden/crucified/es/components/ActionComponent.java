package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.ActionType;

/**
 * This component tells what type of action the entity
 * currently performs.
 */
public class ActionComponent implements EntityComponent {

    private ActionType action;

    public ActionComponent(ActionType action) {
        this.action = action;
    }

    public ActionType getAction() {
        return action;
    }
}
