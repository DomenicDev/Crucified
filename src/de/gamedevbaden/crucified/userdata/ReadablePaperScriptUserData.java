package de.gamedevbaden.crucified.userdata;

import com.jme3.export.*;
import de.gamedevbaden.crucified.enums.PaperScript;

import java.io.IOException;

/**
 * Stores the script (text) for the object (e.g. paper).
 * It is used to later create the right entity.
 * <p>
 * Created by Domenic on 08.06.2017.
 */
public class ReadablePaperScriptUserData implements Savable {

    private PaperScript script;

    public PaperScript getScript() {
        return script;
    }

    public void setScript(PaperScript script) {
        this.script = script;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(script, "script", PaperScript.TestText);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        script = in.readEnum("script", PaperScript.class, PaperScript.TestText);
    }
}
