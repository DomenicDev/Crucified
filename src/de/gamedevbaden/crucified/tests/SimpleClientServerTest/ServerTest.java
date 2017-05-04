package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.server.EntityDataHostedService;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.PhysicsRigidBody;
import de.gamedevbaden.crucified.es.components.Transform;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.game.DefaultGameSessionImplementation;
import de.gamedevbaden.crucified.game.GameEventHandler;
import de.gamedevbaden.crucified.net.server.GameServer;

/**
 * Created by Domenic on 26.04.2017.
 */
public class ServerTest extends SimpleApplication {

    public static void main(String[] args) {

        new ServerTest().start();
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);

        EntityDataState entityDataState = new EntityDataState();
        stateManager.attach(entityDataState);

        stateManager.attach(new ModelLoaderAppState());
        stateManager.attach(new PlayerInputControlAppState());
        stateManager.attach(new PhysicAppState());
        stateManager.attach(new PhysicsPlayerMovementAppState());
        stateManager.attach(new PlayerControlledCharacterMovementState());

        DefaultGameSessionImplementation dgsi = new DefaultGameSessionImplementation();
        stateManager.attach(dgsi);

        GameServer server = new GameServer(5555);
        stateManager.attach(server);

        stateManager.attach(new GameEventHandler(dgsi));

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
                    EntityData entityData = server.getServer().getServices().getService(EntityDataHostedService.class).getEntityData();
                    EntityId entityId = entityData.createEntity();
                    entityData.setComponents(entityId,
                            new Transform(new Vector3f((float) (Math.random() * 5f), 3, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                            new Model(ModelType.TestBox),
                            new PhysicsRigidBody(10, false, CollisionShapeType.BOX_COLLISION_SHAPE));
                }

            }
        }, "Space", "C");


    }

    @Override
    public void simpleUpdate(float tpf) {


    }
}
