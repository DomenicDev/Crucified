package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import de.gamedevbaden.crucified.enums.InputMapping;
import de.gamedevbaden.crucified.game.GameSession;


/**
 * This app state listens for game events, mainly input changes.
 * It basically calls the methods of GameSession
 * <p>
 * Created by Domenic on 02.05.2017.
 */
public class GameEventAppState extends AbstractAppState implements ActionListener {

    private GameSession gameSession;
    private InputManager inputManager;
    private Camera cam;

    private Vector3f lastCamDirection;
    private float camUpdateTime;


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.gameSession = stateManager.getState(GameSessionAppState.class).getGameSession();
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.lastCamDirection = cam.getDirection(new Vector3f());

        // init listener for input events
        for (InputMapping input : InputMapping.values()) {
            this.inputManager.addListener(this, input.name());
        }

        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {
        // check for camera change

        if ((camUpdateTime += tpf) >= 0.02f && !lastCamDirection.equals(cam.getDirection())) { // we send 50 updates per second

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
    public void cleanup() {
        this.inputManager.removeListener(this);
        super.cleanup();
    }
}
