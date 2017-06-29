package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.game.GameSessionAppState;
import de.gamedevbaden.crucified.enums.Type;
import de.gamedevbaden.crucified.es.components.EquippedBy;
import de.gamedevbaden.crucified.es.components.ObjectType;
import de.gamedevbaden.crucified.es.components.StoredIn;
import de.gamedevbaden.crucified.game.GameSession;

/**
 * Created by Domenic on 15.06.2017.
 */
public class PlayerInventoryState extends AbstractAppState {

    private EntitySet equippedItems;
    private EntitySet storedItems;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        GameSession gameSession = stateManager.getState(GameSessionAppState.class).getGameSession();
        EntityId playerId = gameSession.getPlayer();
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        // get all entities which have been equipped by this player
        this.equippedItems = entityData.getEntities(new FieldFilter<>(EquippedBy.class, "player", playerId), EquippedBy.class, ObjectType.class);

        // get all items this player stores
        this.storedItems = entityData.getEntities(new FieldFilter<>(StoredIn.class, "container", playerId), StoredIn.class, ObjectType.class);

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        this.equippedItems.applyChanges();
        this.storedItems.applyChanges();
    }

    public EntityId getFlashlight() {
        // hard coded
        for (Entity entity : equippedItems) {
            if (entity.get(ObjectType.class).getObjectType() == Type.FlashLight) {
                return entity.getId();
            }
        }

        for (Entity entity : storedItems) {
            if (entity.get(ObjectType.class).getObjectType() == Type.FlashLight) {
                return entity.getId();
            }
        }
        return null;
    }

    /**
     * This method just search the next entity of the specified type which is in the player inventory.
     * If the player has no entity of this type stored null is returned.
     *
     * @param type the object type you are looking for
     * @return the next entity of the given type or null if it does not exist.
     */
    public EntityId getNextOfType(Type type) {
        for (Entity entity : storedItems) {
            ObjectType objectType = entity.get(ObjectType.class);
            if (type.equals(objectType.getObjectType())) {
                return entity.getId();
            }
        }
        return null;
    }
}
