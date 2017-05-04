package de.gamedevbaden.crucified.game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.PhysicsPlayerMovementAppState;
import de.gamedevbaden.crucified.appstates.PlayerInputControlAppState;

/**
 * Created by Domenic on 03.05.2017.
 */
public class GameEventHandler extends AbstractAppState implements GameEventListener {

    private PlayerInputControlAppState playerInputControlAppState;
    private PhysicsPlayerMovementAppState physicsPlayerMovementAppState;

    private DefaultGameSessionImplementation gameSession;

    public GameEventHandler(DefaultGameSessionImplementation gameSession) {
        this.gameSession = gameSession;
        this.gameSession.addGameEventListener(this);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.playerInputControlAppState = stateManager.getState(PlayerInputControlAppState.class);
        this.physicsPlayerMovementAppState = stateManager.getState(PhysicsPlayerMovementAppState.class);
        super.initialize(stateManager, app);
    }


    @Override
    public void onInputChange(EntityId entityId, String mappingName, boolean isPressed) {
        this.playerInputControlAppState.applyInputChange(entityId, mappingName, isPressed);
    }

    @Override
    public void onViewDirectionChange(EntityId entityId, Vector3f newViewDirection) {
        physicsPlayerMovementAppState.setViewDirection(entityId, newViewDirection);
    }
}
