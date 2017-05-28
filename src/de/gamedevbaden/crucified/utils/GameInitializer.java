package de.gamedevbaden.crucified.utils;

import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.appstates.game.GameSessionAppState;
import de.gamedevbaden.crucified.appstates.net.MovementInterpolator;
import de.gamedevbaden.crucified.appstates.net.PredictionAppState;
import de.gamedevbaden.crucified.appstates.view.ModelLoaderAppState;
import de.gamedevbaden.crucified.appstates.view.ModelViewAppState;
import de.gamedevbaden.crucified.appstates.view.VisualEquipmentAppState;
import de.gamedevbaden.crucified.appstates.view.VisualStoringAppState;
import de.gamedevbaden.crucified.game.GameSession;

/**
 * Contains static methods which are the building blocks of the application.
 * Created by Domenic on 21.05.2017.
 */
public class GameInitializer {

    public static void initEssentialAppStates(AppStateManager stateManager) {
        stateManager.attach(new ModelLoaderAppState());
    }

    public static void initClientAppStates(AppStateManager stateManager) {
        stateManager.attach(new BulletAppState()); // local physics
        stateManager.attach(new MovementInterpolator());
    }

    public static void initInputAppStates(AppStateManager stateManager) {
        stateManager.attach(new InputAppState());
    }

    public static void initViewAppStates(AppStateManager stateManager) {
        stateManager.attach(new ModelViewAppState());
        stateManager.attach(new VisualStoringAppState());
        stateManager.attach(new VisualEquipmentAppState());
        stateManager.attach(new SoundAppState());
        stateManager.attach(new CameraAppState());
    }

    public static void initGameSessionRelatedAppStates(AppStateManager stateManager, GameSession gameSession) {
        stateManager.attach(new GameSessionAppState(gameSession));
    }

    public static void initClientStatesWithGameSessionDependency(AppStateManager stateManager, GameSession gameSession) {
        stateManager.attach(new PredictionAppState(gameSession.getPlayer()));
    }

    public static void initGameLogicAppStates(AppStateManager stateManager) {
        stateManager.attach(new PlayerInputControlAppState());
        stateManager.attach(new PhysicAppState());
        stateManager.attach(new PhysicsPlayerMovementAppState());
        stateManager.attach(new PlayerControlledCharacterMovementState());
        stateManager.attach(new TriggerAppState());
        stateManager.attach(new AttachmentAppState());
        stateManager.attach(new InteractionAppState());
        stateManager.attach(new ItemStoreAppState());
        stateManager.attach(new EquipmentAppState());
        stateManager.attach(new DecayAppState());
    }

}
