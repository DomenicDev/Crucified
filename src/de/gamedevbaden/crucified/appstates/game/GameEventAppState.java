package de.gamedevbaden.crucified.appstates.game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.PlayerInteractionState;
import de.gamedevbaden.crucified.enums.ActionType;
import de.gamedevbaden.crucified.enums.InputCommand;
import de.gamedevbaden.crucified.game.GameSession;


/**
 * This app state listens for game events, mainly input changes.
 * It basically calls the methods of GameSession
 * <p>
 * Created by Domenic on 02.05.2017.
 */
public class GameEventAppState extends AbstractAppState implements ActionListener, PlayerInteractionState.PlayerInteractionListener {

    private GameSession gameSession;
    private InputManager inputManager;
    private Camera cam;

    private Vector3f lastCamDirection = new Vector3f();
    private float camUpdateTime;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.gameSession = stateManager.getState(GameSessionAppState.class).getGameSession();
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();

        // init listener for input events
        for (InputCommand input : InputCommand.values()) {
            if (input != InputCommand.Interaction) { // interaction is handled differently
                this.inputManager.addListener(this, input.name());
            }
        }

        // add interaction listener
        PlayerInteractionState playerInteractionState = stateManager.getState(PlayerInteractionState.class);
        playerInteractionState.addInteractionListener(this);

        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {
        // check for camera change

        if ((camUpdateTime += tpf) >= 0.05f && !lastCamDirection.equals(cam.getDirection())) { // we send 50 updates per second

            lastCamDirection.set(cam.getDirection());
            // when the camera has rotated we call the update method
            // the player rotation is updated by the camera rotation
            // there might be the possibility that the player doesn't rotate when cam is moving, for example when player is controlling a car
            // this case isn't handled yet...

            gameSession.applyViewDirection(cam.getDirection());
        }
    }

    // listen for player input
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        gameSession.applyInput(name, isPressed);
    }

    @Override
    public void onPutArtifactIntoContainer(EntityId containerId, EntityId artifactId) {
        gameSession.putArtifactIntoContainer(containerId, artifactId);
    }

    @Override
    public void onInteractionWith(EntityId interactedEntity) {
        gameSession.interactWithEntity(interactedEntity);
    }

    @Override
    public void onItemPickup(EntityId entityToPickup) {
        gameSession.pickUpItem(entityToPickup);
    }

    @Override
    public void onItemEquipped(EntityId entityToEquip) {
        gameSession.equipItem(entityToEquip);
    }

    @Override
    public void onItemUnequipped(EntityId itemToUnequip) {
        gameSession.unequipItem(itemToUnequip, gameSession.getPlayer());
    }

    @Override
    public void onItemDrop(EntityId itemToDrop) {
        gameSession.dropItem(itemToDrop);
    }

    @Override
    public void onFlashLightToggle(EntityId flashLight) {
        gameSession.toggleFlashLight(flashLight);
    }

    @Override
    public void onItemCraft(EntityId targetItem, EntityId ingredient) {
        gameSession.putItemToCraft(targetItem, ingredient);
    }

    @Override
    public void onPerformAction(ActionType type) {
        gameSession.performAction(type);
    }

    @Override
    public void cleanup() {
        this.inputManager.removeListener(this);
        super.cleanup();
    }
}
