package de.gamedevbaden.crucified;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.game.DefaultGameSessionImplementation;
import de.gamedevbaden.crucified.game.GameEventHandler;
import de.gamedevbaden.crucified.game.GameSession;

/**
 * <code>TestMain</code> contains the main-method.
 * Created by Domenic on 09.04.2017.
 */
public class TestMain extends SimpleApplication {

    EntityId player;

    public static void main(String[] args) {
        new TestMain().start();
    }

    @Override
    public void simpleInitApp() {

        flyCam.setMoveSpeed(10);

        stateManager.attach(new EntityDataState());

        // game related app states
        stateManager.attach(new ModelLoaderAppState());
        stateManager.attach(new ModelViewAppState());
        stateManager.attach(new MovementInterpolator());
        stateManager.attach(new InputAppState());
        stateManager.attach(new PlayerInputControlAppState());
        stateManager.attach(new PhysicAppState());
        stateManager.attach(new PhysicsPlayerMovementAppState());
        stateManager.attach(new PlayerControlledCharacterMovementState());


        DefaultGameSessionImplementation dgsi = new DefaultGameSessionImplementation();
        stateManager.attach(dgsi);
        stateManager.attach(new GameEventHandler(dgsi));

        stateManager.attach(new SceneEntityLoader());

        stateManager.attach(new PlayerInit());

        stateManager.attach(new CameraAppState());

    }

    private class PlayerInit extends AbstractAppState {


        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
            player = entityData.createEntity();
            entityData.setComponents(player,
                    new Model(ModelType.TestBox),
                    new Transform(new Vector3f(0, 2, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                    new PhysicsCharacterControl(new Vector3f(), app.getCamera().getDirection()),
                    new PlayerControlled(),
                    new CharacterMovementState());

            super.initialize(stateManager, app);

            GameSession gameSession = stateManager.getState(DefaultGameSessionImplementation.class).addPlayer(player);

            stateManager.attach(new GameSessionAppState(gameSession));
            stateManager.attach(new GameEventAppState());

        }


    }
}
