package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import de.gamedevbaden.crucified.utils.GameConstants;

public class ThirdPersonCameraView extends AbstractAppState {

    private Node player;

    private Camera cam;
    private Node camNode;

    public ThirdPersonCameraView(Node player) {
        this.player = player;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        this.cam = app.getCamera();

        camNode = new Node("CameraNode");
        this.player.attachChild(camNode);
        this.camNode.setLocalTranslation(GameConstants.THIRD_PERSON_CAM_OFFSET);


        super.initialize(stateManager, app);
    }

    @Override
    public void render(RenderManager rm) {
        cam.setLocation(camNode.getWorldTranslation());
    }
}
