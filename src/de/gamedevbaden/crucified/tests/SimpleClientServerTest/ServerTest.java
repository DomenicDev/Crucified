package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.MessageConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.server.EntityDataHostedService;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.ModelLoaderAppState;
import de.gamedevbaden.crucified.appstates.PhysicAppState;
import de.gamedevbaden.crucified.appstates.SceneEntityLoader;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.PhysicsRigidBody;
import de.gamedevbaden.crucified.es.components.Transform;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.net.NetworkUtils;

import java.io.IOException;

/**
 * Created by Domenic on 26.04.2017.
 */
public class ServerTest extends SimpleApplication {

    float timer;

    Server server;

    public static void main(String[] args) {

        new ServerTest().start();
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);


        try {
            server = Network.createServer(5555);
            DefaultEntityData ed = new DefaultEntityData();
            server.getServices().addService(new EntityDataHostedService(MessageConnection.CHANNEL_DEFAULT_RELIABLE, ed));
            NetworkUtils.initSerializers();
            server.start();

            stateManager.attach(new EntityDataState(ed));
            stateManager.attach(new ModelLoaderAppState());
            //      stateManager.attach(new ModelViewAppState());
            //     stateManager.attach(new MovementInterpolator());
            //    stateManager.attach(new CameraAppState());
            stateManager.attach(new PhysicAppState());


        } catch (IOException e) {
            e.printStackTrace();
        }

        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("C", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!isPressed) {
                    return;
                }
                if (name.equals("Space")) {
                    if (stateManager.getState(SceneEntityLoader.class) == null) {
                        System.out.println("START");
                        stateManager.attach(new SceneEntityLoader());
                    }
                } else {
                    EntityData entityData = server.getServices().getService(EntityDataHostedService.class).getEntityData();
                    EntityId entityId = entityData.createEntity();
                    entityData.setComponents(entityId,
                            new Transform(new Vector3f((float) (Math.random() * 5f), 3, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                            new Model(ModelType.TestBox.getModelPath()),
                            new PhysicsRigidBody(10, false, CollisionShapeType.BOX_COLLISION_SHAPE));
                }

            }
        }, "Space", "C");

    }

    @Override
    public void simpleUpdate(float tpf) {

        timer += tpf;

        if (timer >= 0.1f) {
            server.getServices().getService(EntityDataHostedService.class).sendUpdates();
            timer = 0;
        }

    }
}
