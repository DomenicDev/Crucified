package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;
import de.gamedevbaden.crucified.enums.PaperScript;

/**
 * Adds a script to this entity.
 * You need to combine this with an interaction entity so players can interact
 * with the entity and read the text on it.
 * <p>
 * Created by Domenic on 08.06.2017.
 */
public class ReadableScript implements EntityComponent {

    private PaperScript script;

    public ReadableScript() {
    }

    public ReadableScript(PaperScript script) {
        this.script = script;
    }

    public PaperScript getScript() {
        return script;
    }
}
