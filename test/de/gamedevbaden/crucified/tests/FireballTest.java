package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import de.gamedevbaden.crucified.enums.Scene;


public class FireballTest extends SimpleApplication {

    public static void main(String[] args) {
        new FireballTest().start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(30);
        BulletAppState bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(true);

        stateManager.attach(bulletAppState);

        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));
        Spatial scene = assetManager.loadModel(Scene.GameLogicTestScene.getScenePath());
        RigidBodyControl scenePhysicsControl = new RigidBodyControl(CollisionShapeFactory.createMeshShape(scene), 0);
        bulletAppState.getPhysicsSpace().add(scenePhysicsControl);
        scene.addControl(scenePhysicsControl);
        rootNode.attachChild(scene);

        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White);
        rootNode.addLight(light);

        inputManager.addMapping("FIRE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!isPressed) return;

                Spatial fireball = assetManager.loadModel("Models/Effects/Fireball.j3o");
                fireball.setLocalTranslation(cam.getLocation());
                rootNode.attachChild(fireball);

                RigidBodyControl fireControl = new RigidBodyControl(CollisionShapeFactory.createBoxShape(fireball), 1);
                fireball.addControl(fireControl);

                bulletAppState.getPhysicsSpace().add(fireControl);
                fireControl.setGravity(new Vector3f(0, 0, 0));

                FireballCollisionListener listener = new FireballCollisionListener(fireControl);
                bulletAppState.getPhysicsSpace().addCollisionListener(listener);

                fireControl.setLinearVelocity(cam.getDirection().mult(10));
            }
        }, "FIRE");

    }

    private class FireballCollisionListener implements PhysicsCollisionListener {

        private RigidBodyControl fire;

        private FireballCollisionListener(RigidBodyControl fire) {
            this.fire = fire;
        }

        @Override
        public void collision(PhysicsCollisionEvent event) {
            if (event.getObjectA() == fire || event.getObjectB() == fire) {
                System.out.println("EXPLOSION!");
            }
        }
    }
}
