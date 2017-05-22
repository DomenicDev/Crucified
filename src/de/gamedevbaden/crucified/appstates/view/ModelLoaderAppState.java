package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import de.gamedevbaden.crucified.appstates.PhysicAppState;

/**
 * The <code>ModelLoaderAppState</code> is just there to load models.
 * It is used by {@link ModelViewAppState} and {@link PhysicAppState} mainly.
 * Created by Domenic on 14.04.2017.
 */
public class ModelLoaderAppState extends AbstractAppState {

    private AssetManager assetManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        super.initialize(stateManager, app);
    }

    /**
     * Loads the model with the given path and returns it.
     * @param modelPath the path of the model
     * @return the model (spatial) or null if not found.
     */
    public Spatial loadModel(String modelPath) {
        try {
            return assetManager.loadModel(modelPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void cleanup() {
        assetManager.clearCache();
        super.cleanup();
    }
}
