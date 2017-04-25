package de.gamedevbaden.crucified.es.components;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * Created by Domenic on 11.04.2017.
 */
public class PointLight implements EntityComponent {

    private ColorRGBA lightColor;
    private float radius;

    public PointLight() {

    }

    public PointLight(ColorRGBA lightColor, float radius) {
        this.lightColor = lightColor;
        this.radius = radius;
    }

    public ColorRGBA getLightColor() {
        return lightColor;
    }

    public float getRadius() {
        return radius;
    }
}
