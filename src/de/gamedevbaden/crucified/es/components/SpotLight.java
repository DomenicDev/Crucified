package de.gamedevbaden.crucified.es.components;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 11.04.2017.
 */
public class SpotLight implements EntityComponent {

    private ColorRGBA lightColor;
    private Vector3f direction;

    /**
     * Do not use
     */
    public SpotLight() {}

    public SpotLight(ColorRGBA lightColor, Vector3f direction) {
        this.lightColor = lightColor;
        this.direction = direction;
    }

    public ColorRGBA getLightColor() {
        return lightColor;
    }

    public Vector3f getDirection() {
        return direction;
    }
}
