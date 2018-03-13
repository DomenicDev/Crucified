package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.game.GameSessionAppState;
import de.gamedevbaden.crucified.es.components.CurseComponent;
import de.gamedevbaden.crucified.game.GameSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CurseViewAppState extends AbstractAppState {

    private EntitySet curses;
    private ModelViewAppState modelViewAppState;

    private Map<EntityId, CustomCurseViewData> curseDataMap = new HashMap<>();

    private GameSession gameSession;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.curses = entityData.getEntities(CurseComponent.class);

        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);

        this.gameSession = stateManager.getState(GameSessionAppState.class).getGameSession();
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (curses.applyChanges()) {

            for (Entity entity : curses.getAddedEntities()) {
                addCurse(entity);
            }

            for (Entity entity : curses.getRemovedEntities()) {
                removeCurse(entity);
            }
        }

    }

    private void addCurse(Entity entity) {
        EntityId cursedEntity = entity.get(CurseComponent.class).getCursedEntity();

        if (Objects.equals(cursedEntity, gameSession.getPlayer())) {
            // the own player won't see him becoming lighted
            return;
        }

        Spatial model = modelViewAppState.getSpatial(cursedEntity);
        if (model != null) {
            Light light = createLight();
            model.addLight(light);
            model.setQueueBucket(RenderQueue.Bucket.Translucent);

            CustomCurseViewData ccvd = new CustomCurseViewData();
            ccvd.light = light;
            ccvd.model = model;

            this.curseDataMap.put(entity.getId(), ccvd);
        }
    }

    private void removeCurse(Entity entity) {
        CustomCurseViewData viewData = this.curseDataMap.remove(entity.getId());
        if (viewData != null) {
            viewData.model.removeLight(viewData.light);
            viewData.model.setQueueBucket(RenderQueue.Bucket.Opaque);
        }
    }

    private AmbientLight createLight() {
        AmbientLight light = new AmbientLight(ColorRGBA.Red);
        light.setEnabled(true);
        return light;
    }

    private class CustomCurseViewData {
        Spatial model;
        Light light;
    }

    @Override
    public void cleanup() {
        for (Entity entity : curses) {
            removeCurse(entity);
        }
        this.curses.release();
        this.curses.clear();
        this.curses = null;

        this.curseDataMap.clear();
        super.cleanup();
    }
}
