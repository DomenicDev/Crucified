package de.gamedevbaden.crucified.triggersystem;

/**
 * Created by Domenic on 10.05.2017.
 */
public abstract class AbstractTrigger {

    private boolean triggered;

    public abstract void trigger();

    public abstract boolean canBeTriggered();

}
