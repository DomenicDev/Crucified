package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.PointLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import java.util.HashMap;
import java.util.Map;

/**
 * This app state will dynamically enable / disable added lights when they are to far away.
 */
public class LightingDistanceAppState extends AbstractAppState {

    private Camera cam;
    private Map<PointLight, LightDistanceControl> controlledPointLights = new HashMap<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.cam = app.getCamera();
        super.initialize(stateManager, app);
    }

    /**
     * Will watch the specified light and disable it when it comes out of range
     * @param light the light to add
     * @param distance the max distance the light will be stayed enabled
     */
    public void addPointLight(PointLight light, float distance) {
        if (light != null && distance > 0 && !controlledPointLights.containsKey(light)) {
            this.controlledPointLights.put(light, new LightDistanceControl(light.isEnabled(), distance));
        }
    }

    /**
     * This method will store the logical state of the light.
     * There might be lights for example of head lamps which can be
     * enabled and disabled dynamically. By saving the logic state of
     * the light we make sure we do not overwrite the "enabled" value
     * when we walk to far away and get into range again.
     * @param pointLight the point light to update the state of
     * @param enabled the logical state of that light.
     */
    public void updateEnabledState(PointLight pointLight, boolean enabled) {
        LightDistanceControl control = this.controlledPointLights.get(pointLight);
        control.logicallyEnabled = enabled;
    }

    /**
     * Will no longer watch this light.
     * It will not remove that light from the scene!
     * Note that this method will apply the last logical state for that light.
     * @param light the light to unwatch
     */
    public void unwatchLight(PointLight light) {
        LightDistanceControl c = this.controlledPointLights.remove(light);
        light.setEnabled(c.logicallyEnabled);
    }

    @Override
    public void update(float tpf) {
        // we take the camera position as reference position
        Vector3f camPos = cam.getLocation();
        // check all point lights
        for (Map.Entry<PointLight, LightDistanceControl> pointLightEntry : controlledPointLights.entrySet()) {
            PointLight light = pointLightEntry.getKey();
            LightDistanceControl control = pointLightEntry.getValue();
            float maxDistance = control.maxDistance;
            if (light.getPosition().distance(camPos) <= maxDistance) {
                // the light is in range, so now we want to apply the logic state of that light
                light.setEnabled(control.logicallyEnabled);
            } else {
                // we are not in range anymore, we deactivate the light
                light.setEnabled(false);
            }
        }
    }

    private class LightDistanceControl {

        boolean logicallyEnabled;
        float maxDistance;

        LightDistanceControl(boolean logicallyEnabled, float maxDistance) {
            this.logicallyEnabled = logicallyEnabled;
            this.maxDistance = maxDistance;
        }
    }

}
