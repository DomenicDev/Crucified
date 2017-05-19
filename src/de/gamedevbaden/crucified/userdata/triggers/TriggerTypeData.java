package de.gamedevbaden.crucified.userdata.triggers;

import com.jme3.export.*;
import de.gamedevbaden.crucified.es.triggersystem.TriggerType;

import java.io.IOException;

/**
 * Created by Domenic on 11.05.2017.
 */
public class TriggerTypeData implements Savable {

    private TriggerType triggerType;

    public TriggerTypeData() {
    }

    public TriggerTypeData(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(triggerType, "type", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        triggerType = in.readEnum("type", TriggerType.class, null);
    }
}
