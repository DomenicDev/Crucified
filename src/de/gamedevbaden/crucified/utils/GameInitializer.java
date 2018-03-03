package de.gamedevbaden.crucified.utils;

import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.appstates.action.ActionHandlerAppState;
import de.gamedevbaden.crucified.appstates.action.ActionSystemAppState;
import de.gamedevbaden.crucified.appstates.cooptasks.TestCoopDoorTask;
import de.gamedevbaden.crucified.appstates.game.GameSessionAppState;
import de.gamedevbaden.crucified.appstates.gamelogic.ArtifactContainerAppState;
import de.gamedevbaden.crucified.appstates.gamelogic.GameLogicAppState;
import de.gamedevbaden.crucified.appstates.gamelogic.GameStartupAppState;
import de.gamedevbaden.crucified.appstates.gui.HudAppState;
import de.gamedevbaden.crucified.appstates.net.MovementInterpolator;
import de.gamedevbaden.crucified.appstates.net.PredictionAppState;
import de.gamedevbaden.crucified.appstates.paging.GameWorldPagingManager;
import de.gamedevbaden.crucified.appstates.sound.*;
import de.gamedevbaden.crucified.appstates.story.StoryManager;
import de.gamedevbaden.crucified.appstates.view.*;
import de.gamedevbaden.crucified.game.GameSession;

/**
 * Contains static methods which are the building blocks of the application.
 * Created by Domenic on 21.05.2017.
 */
public class GameInitializer {

    public static void initEssentialAppStates(AppStateManager stateManager) {
        stateManager.attach(new ModelLoaderAppState());
    }

    public static void removeEssentialAppStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(ModelLoaderAppState.class));
    }

    public static void initClientAppStates(AppStateManager stateManager) {
        stateManager.attach(new BulletAppState()); // local physics
        stateManager.attach(new MovementInterpolator());
    }

    public static void removeClientAppStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(BulletAppState.class));
        stateManager.detach(stateManager.getState(MovementInterpolator.class));
    }

    public static void initInputAppStates(AppStateManager stateManager) {
        stateManager.attach(new InputAppState());
    }

    public static void removeInputAppStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(InputAppState.class));
    }

    public static void initViewAppStates(AppStateManager stateManager) {
        stateManager.attach(new ModelViewAppState());
        stateManager.attach(new LightingDistanceAppState());
        stateManager.attach(new VisualStoringAppState());
        stateManager.attach(new VisualEquipmentAppState());
        stateManager.attach(new CameraAppState());
        stateManager.attach(new DemonAnimationAppState());
        stateManager.attach(new CharacterAnimationAppState());

        stateManager.attach(new ShadowRendererAppState(stateManager.getApplication().getAssetManager(), stateManager.getApplication().getViewPort()));
        stateManager.attach(new VisualFlashLightAppState());
        stateManager.attach(new HeadMovingAppState());
        stateManager.attach(new TerrainGrassGeneratorAppState());
        stateManager.attach(new VisualCraftingAppState());
        stateManager.attach(new FireEffectAppState());
        stateManager.attach(new FireLightAppState());
        stateManager.attach(new GameWorldPagingManager());

        // gui app states
        stateManager.attach(new HudAppState());
    }

    public static void removeViewAppStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(ModelViewAppState.class));
        stateManager.detach(stateManager.getState(LightingDistanceAppState.class));
        stateManager.detach(stateManager.getState(VisualStoringAppState.class));
        stateManager.detach(stateManager.getState(VisualEquipmentAppState.class));
        stateManager.detach(stateManager.getState(CameraAppState.class));
        stateManager.detach(stateManager.getState(DemonAnimationAppState.class));
        stateManager.detach(stateManager.getState(CharacterAnimationAppState.class));

        stateManager.detach(stateManager.getState(ShadowRendererAppState.class));
        stateManager.detach(stateManager.getState(VisualFlashLightAppState.class));
        stateManager.detach(stateManager.getState(HeadMovingAppState.class));
        stateManager.detach(stateManager.getState(TerrainGrassGeneratorAppState.class));
        stateManager.detach(stateManager.getState(VisualCraftingAppState.class));
        stateManager.detach(stateManager.getState(FireEffectAppState.class));
        stateManager.detach(stateManager.getState(FireLightAppState.class));
        stateManager.detach(stateManager.getState(GameWorldPagingManager.class));

        stateManager.detach(stateManager.getState(HudAppState.class));
    }

    public static void initSoundAppStates(AppStateManager stateManager) {
        stateManager.attach(new FootstepSoundAppState());
        stateManager.attach(new SoundAppState());
        stateManager.attach(new FireSoundAppState());
        stateManager.attach(new FireballSoundAppState());
        stateManager.attach(new HitSoundAppState());
        stateManager.attach(new DarkAmbienceMusicAppState());
    }

    public static void removeSoundAppStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(FootstepSoundAppState.class));
        stateManager.detach(stateManager.getState(SoundAppState.class));
        stateManager.detach(stateManager.getState(FireSoundAppState.class));
        stateManager.detach(stateManager.getState(FireballSoundAppState.class));
        stateManager.detach(stateManager.getState(HitSoundAppState.class));
        stateManager.detach(stateManager.getState(DarkAmbienceMusicAppState.class));
    }

    public static void initFirstPersonCameraView(AppStateManager stateManager) {
        GameSession gameSession = stateManager.getState(GameSessionAppState.class).getGameSession();
        stateManager.getState(ModelViewAppState.class).addModelListener(gameSession.getPlayer(), spatial -> stateManager.attach(new FirstPersonCameraView((Node) spatial, GameConstants.FIRST_PERSON_CAM_OFFSET)));
    }

    public static void removeFirstPersonCameraView(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(FirstPersonCameraView.class));
    }

    public static void initPlayerStates(AppStateManager stateManager) {
        stateManager.attach(new PlayerInventoryState());
    }

    public static void removePlayerStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(PlayerInventoryState.class));
    }

    public static void initGameSessionRelatedAppStates(AppStateManager stateManager, GameSession gameSession) {
        stateManager.attach(new GameSessionAppState(gameSession));
    }

    public static void removeGameSessionRelatedAppStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(GameSessionAppState.class));
    }

    public static void initClientStatesWithGameSessionDependency(AppStateManager stateManager, GameSession gameSession) {
        stateManager.attach(new PredictionAppState(gameSession.getPlayer()));
    }

    public static void removeClientStatesWithGameSessionDependency(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(PredictionAppState.class));
    }

    public static void initGameLogicAppStates(AppStateManager stateManager) {
        stateManager.attach(new PlayerInputControlAppState());
        stateManager.attach(new PhysicAppState());
        stateManager.attach(new DoorAppState());
        stateManager.attach(new PhysicsPlayerMovementAppState());
        stateManager.attach(new PlayerControlledCharacterMovementState());
        stateManager.attach(new ItemFunctionalityAppState());
        stateManager.attach(new AttachmentAppState());
        stateManager.attach(new ItemStoreAppState());
        stateManager.attach(new EquipmentAppState());
        stateManager.attach(new InteractionAppState());
        stateManager.attach(new FireAppState());
        stateManager.attach(new DoorAppState());
        stateManager.attach(new PhysicalDoorAppState());
        stateManager.attach(new CraftingAppState());
        stateManager.attach(new DecayAppState());
        stateManager.attach(new NameAppState());
        stateManager.attach(new NewTriggerAppState());
        stateManager.attach(new TestCoopDoorTask());
        stateManager.attach(new ArtifactContainerAppState());
        stateManager.attach(new ActionSystemAppState());
        stateManager.attach(new ActionHandlerAppState());
        stateManager.attach(new GameLogicAppState());
    }

    public static void removeGameLogicAppStates(AppStateManager stateManager) {
        stateManager.detach(stateManager.getState(PlayerInputControlAppState.class));
        stateManager.detach(stateManager.getState(PhysicAppState.class));
        stateManager.detach(stateManager.getState(DoorAppState.class));
        stateManager.detach(stateManager.getState(PhysicsPlayerMovementAppState.class));
        stateManager.detach(stateManager.getState(PlayerControlledCharacterMovementState.class));
        stateManager.detach(stateManager.getState(ItemFunctionalityAppState.class));
        stateManager.detach(stateManager.getState(AttachmentAppState.class));
        stateManager.detach(stateManager.getState(ItemStoreAppState.class));
        stateManager.detach(stateManager.getState(EquipmentAppState.class));
        stateManager.detach(stateManager.getState(InteractionAppState.class));
        stateManager.detach(stateManager.getState(FireAppState.class));
        stateManager.detach(stateManager.getState(DoorAppState.class));
        stateManager.detach(stateManager.getState(PhysicalDoorAppState.class));
        stateManager.detach(stateManager.getState(CraftingAppState.class));
        stateManager.detach(stateManager.getState(DecayAppState.class));
        stateManager.detach(stateManager.getState(NameAppState.class));
        stateManager.detach(stateManager.getState(NewTriggerAppState.class));
        stateManager.detach(stateManager.getState(TestCoopDoorTask.class));
        stateManager.detach(stateManager.getState(ArtifactContainerAppState.class));
        stateManager.detach(stateManager.getState(ActionSystemAppState.class));
        stateManager.detach(stateManager.getState(ActionHandlerAppState.class));
        stateManager.detach(stateManager.getState(GameLogicAppState.class));
    }

    public static void initThirdPersonCameraView(AppStateManager stateManager) {
        GameSession gameSession = stateManager.getState(GameSessionAppState.class).getGameSession();
        stateManager.getState(ModelViewAppState.class).addModelListener(gameSession.getPlayer(), spatial -> stateManager.attach(new ThirdPersonCameraView((Node) spatial)));

    }
}
