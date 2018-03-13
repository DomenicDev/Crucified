package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.gamelogic.PlayerHolderAppState;
import de.gamedevbaden.crucified.es.components.CurseComponent;
import de.gamedevbaden.crucified.es.components.CurseEmitterComponent;
import de.gamedevbaden.crucified.es.components.Decay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurseEmitterAppState extends AbstractAppState {

    private AppStateManager stateManager;
    private EntityData entityData;
    private EntitySet curseEmitters;
    private Map<EntityId, Float> delayMap = new HashMap<>();
    private List<EntityId> toRemoveList = new ArrayList<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.curseEmitters = entityData.getEntities(CurseEmitterComponent.class);

        this.stateManager = stateManager;
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (!delayMap.isEmpty()) {
            for (Map.Entry<EntityId, Float> e : delayMap.entrySet()) {
                EntityId id = e.getKey();
                float v = e.getValue() - tpf;
                if (v <= 0) {
                    toRemoveList.add(id);
                } else {
                    delayMap.put(id, v);
                }
            }
            for (EntityId entityId : toRemoveList) {
                delayMap.remove(entityId);
            }
            toRemoveList.clear();
        }

    }

    public void cursePlayer(EntityId emitterId) {
        curseEmitters.applyChanges();
        if (!curseEmitters.containsId(emitterId) || delayMap.keySet().contains(emitterId)) {
            return;
        }

        PlayerHolderAppState playerHolderAppState = stateManager.getState(PlayerHolderAppState.class);
        EntityId enemyId = playerHolderAppState.getPlayerOne().equals(emitterId) ? playerHolderAppState.getPlayerTwo() : playerHolderAppState.getPlayerOne();


        // we create a curse onto the survivor
        // so the monster will be able to see where the player is
        // even through walls, hills etc.
        // but this stays only for some time
        EntityId curse = entityData.createEntity();
        entityData.setComponents(curse,
                new CurseComponent(enemyId),
                new Decay(5000)
        );

        this.delayMap.put(emitterId, 30f);
    }

    @Override
    public void cleanup() {
        this.curseEmitters.release();
        this.curseEmitters.clear();
        this.curseEmitters = null;
        this.delayMap.clear();
        this.toRemoveList.clear();
        super.cleanup();
    }
}
