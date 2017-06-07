package de.gamedevbaden.crucified.tests;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.appstates.game.GameCommanderAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventAppState;
import de.gamedevbaden.crucified.appstates.game.GameEventHandler;
import de.gamedevbaden.crucified.appstates.game.GameSessionManager;
import de.gamedevbaden.crucified.appstates.view.FirstPersonCameraView;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.utils.GameConstants;
import de.gamedevbaden.crucified.utils.GameInitializer;
import de.gamedevbaden.crucified.utils.GameOptions;

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

        GameOptions.ENABLE_PHYSICS_DEBUG = false; // for test

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

        // create our game session app states
        stateManager.attach(new PlayerInteractionState());
        stateManager.attach(new GameEventAppState());

        // init game logic states
        GameInitializer.initEssentialAppStates(stateManager);
        GameInitializer.initGameLogicAppStates(stateManager);
        GameInitializer.initViewAppStates(stateManager);
        GameInitializer.initInputAppStates(stateManager);
        GameInitializer.initGameSessionRelatedAppStates(stateManager, gameSession);


        stateManager.attach(new Loader()); // load scene


        // create first person cam view
        stateManager.attach(new FirstPersonCameraView(gameSession.getPlayer(), GameConstants.FIRST_PERSON_CAM_OFFSET)); // 0,1.7,0

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
            stateManager.getState(GameCommanderAppState.class).loadScene(SceneEntityLoader.sceneToLoad);
            super.initialize(stateManager, app);
        }
    }

}
