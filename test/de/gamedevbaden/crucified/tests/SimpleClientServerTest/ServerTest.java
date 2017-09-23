package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.GameCommanderHolder;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.appstates.game.GameEventHandler;
import de.gamedevbaden.crucified.appstates.game.GameSessionManager;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.net.server.GameServer;
import de.gamedevbaden.crucified.utils.GameInitializer;
import de.gamedevbaden.crucified.utils.GameOptions;

/**
 * A simple server test application for testing in game features.
 *
 * Created by Domenic on 26.04.2017.
 */
public class ServerTest extends SimpleApplication {

    private float time;
    private EntityId cube;
    private EntityData entityData;

    public static void main(String[] args) {
        new ServerTest().start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        GameOptions.ENABLE_PHYSICS_DEBUG = true; // for now

        // this state will create an own EntityData which we can use
        EntityDataState entityDataState = new EntityDataState();
        stateManager.attach(entityDataState);

        // get self-created entity data
        entityData = entityDataState.getEntityData();

        // responsible for creating game sessions for connected players
        GameSessionManager gameSessionManager = new GameSessionManager();
        stateManager.attach(gameSessionManager);

        // create and setup game server
        GameServer server = new GameServer(5555);
        stateManager.attach(server);

        // attach the default GameEventHandler
        stateManager.attach(new GameEventHandler(gameSessionManager));

        // this state just holds the server side commanders
        stateManager.attach(new GameCommanderHolder());

        // inits game logic states
        GameInitializer.initEssentialAppStates(stateManager);
        GameInitializer.initGameLogicAppStates(stateManager);

        // load scene
        stateManager.attach(new SceneEntityLoader());

        stateManager.attach(new Loader());

        // --

        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("C", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (!isPressed) {
                return;
            }
            if (name.equals("C")) {
                cube = entityData.createEntity();
                entityData.setComponents(cube,
                        new Transform(new Vector3f((float) (Math.random() * 5f), 3, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                        new Model(ModelType.TestBox),
                        new PhysicsRigidBody(10, true, CollisionShapeType.BOX_COLLISION_SHAPE),
                        new OnMovement());

                EntityId child = entityData.createEntity();
                entityData.setComponents(child,
                        new Transform(Vector3f.ZERO, new Quaternion(), Vector3f.UNIT_XYZ),
                        new ChildOf(cube, new Vector3f(0, 3, 0), new Quaternion()),
                        new Model(ModelType.TestBox),
                        new PhysicsRigidBody(10, true, CollisionShapeType.BOX_COLLISION_SHAPE),
                        new OnMovement());


            }

        }, "Space", "C");


        System.out.println("Server initialized!");
    }

    private class Loader extends AbstractAppState {
        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            stateManager.getState(SceneEntityLoader.class).createEntitiesFromScene(SceneEntityLoader.sceneToLoad);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        if (cube != null && entityData != null) {
            entityData.setComponent(cube, new Transform(new Vector3f(FastMath.sin(time * 2) * 3, 5, 0), new Quaternion(), Vector3f.UNIT_XYZ));
        }

    }
}
