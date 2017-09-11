package de.gamedevbaden.crucified.appstates.view;

import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

/**
 * The current position of the specified player is applied to the camera.
 * Created by Domenic on 05.05.2017.
 */
public class FirstPersonCameraView extends AbstractAppState {

    private Camera cam;
    private Vector3f offset = new Vector3f();
    private Node cameraNode;
    private Node playerModel;

    public FirstPersonCameraView(Node player, Vector3f offset) {
        this.playerModel = player;
        this.offset.set(offset);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.cam = app.getCamera();

        // we attach our cam node to the head of the player model
        Node head = playerModel.getControl(SkeletonControl.class).getAttachmentsNode("head");
        this.cameraNode = new Node("CameraNode");
        this.cameraNode.setLocalTranslation(offset);
        head.attachChild(cameraNode);
        super.initialize(stateManager, app);
    }

    @Override
    public void render(RenderManager rm) {
        // we call this in render() to avoid this known "shaking" effect of the camera
        cam.setLocation(cameraNode.getWorldTranslation());
    }

    @Override
    public void cleanup() {
        this.cameraNode.removeFromParent();
        super.cleanup();
    }
}
