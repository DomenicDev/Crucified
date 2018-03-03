package de.gamedevbaden.crucified.appstates.view;


import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.game.GameSessionAppState;
import de.gamedevbaden.crucified.es.components.ArtifactComponent;
import de.gamedevbaden.crucified.es.components.CantSeeArtifactComponent;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.game.GameSession;

public class ArtifactHiderAppState extends AbstractAppState {

    private EntitySet artifacts;
    private ModelViewAppState modelViewAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();

        GameSession gameSession = stateManager.getState(GameSessionAppState.class).getGameSession();
        EntityId player = gameSession.getPlayer();

        if (entityData.getComponent(player, CantSeeArtifactComponent.class) != null) {
            // we are the demon so we hide all artifact
            artifacts = entityData.getEntities(Model.class, ArtifactComponent.class);
        }

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (artifacts != null && artifacts.applyChanges()) {

            for (Entity entity : artifacts.getAddedEntities()) {
                Spatial model = modelViewAppState.getSpatial(entity.getId());
                if (model != null) {
                    model.setCullHint(Spatial.CullHint.Always);
                }
            }

        }

    }

    @Override
    public void cleanup() {
        if (artifacts != null) {
            this.artifacts.release();
            this.artifacts.clear();
            this.artifacts = null;
        }

        super.cleanup();
    }
}
