package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;

/**
 * ToDo: Optimize update with camera node. There should not be any checks whether the camera is attached or not
 * Created by Domenic on 05.05.2017.
 */
public class FirstPersonCameraView extends AbstractAppState {

    private ModelViewAppState modelState;
    private Camera cam;

    private Vector3f offset = new Vector3f();
    private Vector3f camLocation = new Vector3f();
    private Node cameraNode;

    private EntityId playerId;
    private Spatial playerModel;

    public FirstPersonCameraView(EntityId playerId) {
        this.playerId = playerId;
    }

    public FirstPersonCameraView(EntityId playerId, Vector3f offset) {
        this.playerId = playerId;
        this.offset = offset;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.modelState = stateManager.getState(ModelViewAppState.class);
        this.cam = app.getCamera();
        this.playerModel = modelState.getSpatial(playerId);

        this.cameraNode = new Node("CameraNode");
        if (playerModel != null && playerModel instanceof Node) {
            ((Node) playerModel).attachChild(cameraNode);
            cameraNode.setLocalTranslation(offset);
        }

        super.initialize(stateManager, app);
    }

    public Vector3f getOffset() {
        return cameraNode.getLocalTranslation();
    }

    public void setOffset(Vector3f offset) {
        this.cameraNode.setLocalTranslation(offset);
    }

    @Override
    public void update(float tpf) {
        if (cameraNode.getParent() == null && playerModel != null && playerModel instanceof Node) {
            ((Node) playerModel).attachChild(cameraNode);
            cameraNode.setLocalTranslation(offset);
        }
    }

    @Override
    public void render(RenderManager rm) {
        // we call this in render() to avaoid this known "shaking" effect of the camera
        if (playerModel != null) {
            cam.setLocation(cameraNode.getWorldTranslation());
        } else {
            playerModel = modelState.getSpatial(playerId);

        }
    }
}
