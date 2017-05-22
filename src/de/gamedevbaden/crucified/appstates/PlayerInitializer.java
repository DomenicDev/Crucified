package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameSessionManager;

/**
 * Created by Domenic on 21.05.2017.
 */
public class PlayerInitializer extends AbstractAppState {

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        GameSessionManager gameSessionManager = stateManager.getState(GameSessionManager.class);
        EntityFactory.createPlayer(entityData);
        super.initialize(stateManager, app);
    }
}
