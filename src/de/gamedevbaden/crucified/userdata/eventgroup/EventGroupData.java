package de.gamedevbaden.crucified.userdata.eventgroup;

import com.jme3.export.*;

import java.io.IOException;

/**
 * Created by Domenic on 11.05.2017.
 */
public class EventGroupData implements Savable {

    private int amountOfTriggers = 1;

    public EventGroupData() {
    }

    public EventGroupData(int amountOfTriggers) {
        this.amountOfTriggers = amountOfTriggers;
    }

    public int getAmountOfTriggers() {
        return amountOfTriggers;
    }

    public void setAmountOfTriggers(int amountOfTriggers) {
        this.amountOfTriggers = amountOfTriggers;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(amountOfTriggers, "amount", 1);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        amountOfTriggers = in.readInt("amount", 1);
    }
}
