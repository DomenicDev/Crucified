package de.gamedevbaden.crucified.es.components;

import com.simsilica.es.EntityComponent;

/**
 * Created by Paul Speed on 12.05.2017.
 */
public class Decay implements EntityComponent {

    private long start;
    private long delta;

    public Decay(long deltaMillis) {
        this.start = System.nanoTime();
        this.delta = deltaMillis * 1000000;
    }

    public double getPercent() {
        long time = System.nanoTime();
        return (double) (time - start) / delta;
    }

    @Override
    public String toString() {
        return "Decay[" + (delta / 1000000.0) + " ms]";
    }
}
