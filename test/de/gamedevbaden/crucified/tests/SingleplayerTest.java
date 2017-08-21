package de.gamedevbaden.crucified.tests;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.GameCommanderHolder;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventHandler;
import de.gamedevbaden.crucified.appstates.game.GameSessionManager;
import de.gamedevbaden.crucified.enums.InteractionType;
import de.gamedevbaden.crucified.enums.ItemType;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.utils.GameInitializer;
import de.gamedevbaden.crucified.utils.GameOptions;

import java.util.HashMap;

/**
 * Created by Domenic on 21.05.2017.
 */
public class SingleplayerTest extends SimpleApplication {

    public static void main(String[] args) {
        new SingleplayerTest().start();
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        flyCam.setEnabled(false);

        GameOptions.ENABLE_PHYSICS_DEBUG = true; // for test

        // create entity data state
        EntityDataState entityDataState = new EntityDataState();
        stateManager.attach(entityDataState);
        EntityData entityData = entityDataState.getEntityData();

        // create game commander handler
        GameCommanderAppState commanderAppState = new GameCommanderAppState(this);
        stateManager.attach(commanderAppState);

        // load test scene
        stateManager.attach(new SceneEntityLoader());

        // create session manager to create a session for a single player
        GameSessionManager sessionManager = new GameSessionManager();
        stateManager.attach(sessionManager);

        stateManager.attach(new GameEventHandler(sessionManager));

        // create GameSession for our player
        GameSession gameSession = sessionManager.createSession(EntityFactory.createPlayer(entityData));

        // create GameCommanderHolder
        GameCommanderHolder commanderHolder = new GameCommanderHolder();
        stateManager.attach(commanderHolder);
        commanderHolder.add(gameSession.getPlayer(), commanderAppState); // that's the only one we need to add

        // create our game session app states
        stateManager.attach(new PlayerInteractionState());
        stateManager.attach(new GameEventAppState());

        // init game logic states
        GameInitializer.initEssentialAppStates(stateManager);
        GameInitializer.initGameSessionRelatedAppStates(stateManager, gameSession);
        GameInitializer.initGameLogicAppStates(stateManager);
        GameInitializer.initViewAppStates(stateManager);
        GameInitializer.initSoundAppStates(stateManager);
        GameInitializer.initInputAppStates(stateManager);
        GameInitializer.initPlayerStates(stateManager);
        GameInitializer.initFirstPersonCameraView(stateManager);

        stateManager.attach(new Loader()); // load scene


        EntityId campFire = entityData.createEntity();
        HashMap<ItemType, Integer> items = new HashMap<>();
        items.put(ItemType.Firewood, 2);
        entityData.setComponents(campFire,
                new Model(ModelType.Campfire),
                new PhysicsRigidBody(0, false, CollisionShapeType.BOX_COLLISION_SHAPE),
                new InteractionComponent(InteractionType.TurnOnCampfire, true),
                new NeedToBeCrafted(items),
                new Transform(new Vector3f(0, 0.2f, 0)),
                new FireState(false));


        inputManager.addMapping("h", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                System.out.println(cam.getLocation().add(cam.getDirection().mult(1000)));
            }
        }, "h");
    }

    private class Loader extends AbstractAppState {

        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            stateManager.getState(SceneEntityLoader.class).createEntitiesFromScene(SceneEntityLoader.sceneToLoad);
            stateManager.getState(GameCommanderAppState.class).loadScene(SceneEntityLoader.sceneToLoad);
            super.initialize(stateManager, app);
        }
    }

}
