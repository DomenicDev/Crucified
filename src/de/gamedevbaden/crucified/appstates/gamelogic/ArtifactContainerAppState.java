package de.gamedevbaden.crucified.appstates.gamelogic;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.ItemStoreAppState;
import de.gamedevbaden.crucified.enums.InteractionType;
import de.gamedevbaden.crucified.es.components.*;

public class ArtifactContainerAppState extends AbstractAppState {

    private EntityData entityData;
    private EntitySet containers;
    private EntitySet artifacts;

    private ItemStoreAppState itemStoreAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.containers = entityData.getEntities(new FieldFilter<>(InteractionComponent.class, "type", InteractionType.ThrowArtifactIntoContainer), Container.class, InteractionComponent.class);
        this.artifacts = entityData.getEntities(ArtifactComponent.class);

        this.itemStoreAppState = stateManager.getState(ItemStoreAppState.class);
    }

    public void throwArtifactIntoContainer(EntityId containerId, EntityId artifactId) {
        this.containers.applyChanges();
        this.artifacts.applyChanges();

        if (!containers.containsId(containerId) || !artifacts.containsId(artifactId)) {
            return;
        }

        if (itemStoreAppState.storeItem(containerId, artifactId)) {

            // we could store the artifact successfully
            // now it's time to destroy it
            entityData.removeEntity(artifactId);

            Transform t = entityData.getEntity(containerId, Transform.class).get(Transform.class);
            EntityId fire = entityData.createEntity();
            entityData.setComponents(fire,
                    new Transform(t.getTranslation(), t.getRotation(), t.getScale()),
                    new FireState(true));
        }
    }

    @Override
    public void cleanup() {
        this.artifacts.release();
        this.artifacts.clear();
        this.artifacts = null;

        this.containers.release();
        this.containers.clear();
        this.containers = null;

        this.entityData = null;
    }
}
