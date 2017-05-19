package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.server.EntityDataHostedService;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.es.components.ChildOf;
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

    float time;
    private EntityId cube;
    private EntityData entityData;

    public static void main(String[] args) {

        new ServerTest().start();
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);

        EntityDataState entityDataState = new EntityDataState();
        stateManager.attach(entityDataState);

        entityData = entityDataState.getEntityData();

        stateManager.attach(new ModelLoaderAppState());
        stateManager.attach(new PlayerInputControlAppState());
        stateManager.attach(new PhysicAppState());
        stateManager.attach(new PhysicsPlayerMovementAppState());
        stateManager.attach(new PlayerControlledCharacterMovementState());
        stateManager.attach(new TriggerAppState());
        stateManager.attach(new AttachmentAppState());
        stateManager.attach(new InteractionAppState());
        stateManager.attach(new ItemStoreAppState());

        DefaultGameSessionImplementation dgsi = new DefaultGameSessionImplementation();
        stateManager.attach(dgsi);

        GameServer server = new GameServer(5555);
        stateManager.attach(server);

        stateManager.attach(new GameEventHandler(dgsi));

        stateManager.attach(new SceneEntityLoader());

        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("C", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (!isPressed) {
                return;
            }
            if (name.equals("C")) {
                EntityData entityData = server.getServer().getServices().getService(EntityDataHostedService.class).getEntityData();
                cube = entityData.createEntity();
                entityData.setComponents(cube,
                        new Transform(new Vector3f((float) (Math.random() * 5f), 3, 0), new Quaternion(), new Vector3f(1, 1, 1)),
                        new Model(ModelType.TestBox),
                        new PhysicsRigidBody(10, true, CollisionShapeType.BOX_COLLISION_SHAPE));

                EntityId child = entityData.createEntity();
                entityData.setComponents(child,
                        new Transform(Vector3f.ZERO, new Quaternion(), Vector3f.UNIT_XYZ),
                        new ChildOf(cube, new Vector3f(0, 3, 0), new Quaternion()),
                        new Model(ModelType.TestBox),
                        new PhysicsRigidBody(10, true, CollisionShapeType.BOX_COLLISION_SHAPE));


            }

        }, "Space", "C");


    }

    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        if (cube != null && entityData != null) {
            entityData.setComponent(cube, new Transform(new Vector3f(FastMath.sin(time * 2) * 3, 5, 0), new Quaternion(), Vector3f.UNIT_XYZ));
        }

    }
}
