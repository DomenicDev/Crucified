package de.gamedevbaden.crucified.net.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.gamedevbaden.crucified.enums.PaperScript;

/**
 * This message asks the client to read the specified note.
 * Created by Domenic on 12.06.2017.
 */
@Serializable
public class ReadNoteMessage extends AbstractMessage {

    private PaperScript script;

    public ReadNoteMessage() {

    }

    public ReadNoteMessage(PaperScript script) {
        this.script = script;
    }

    public PaperScript getScript() {
        return script;
    }
}
