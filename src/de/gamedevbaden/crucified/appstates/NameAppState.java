package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.Name;

public class NameAppState extends AbstractAppState {

    private EntitySet namedEntities;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.namedEntities = entityData.getEntities(Name.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        namedEntities.applyChanges();
    }

    /**
     * Will search all named entities for the same specified name value.
     * It will return the first one found or null if nothing matched.
     * @param name the name of the searched entity
     * @return the first found entity with the specified name
     */
    public EntityId findEntityByName(String name) {
        if (name != null) {
            for (Entity entity : namedEntities) {
                Name nameComponent = entity.get(Name.class);
                if (nameComponent.getName().equals(name)) {
                    return entity.getId();
                }
            }
        }
        return null; // nothing found or name was null
    }
}
