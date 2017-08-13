package de.gamedevbaden.crucified.tests.zayesbullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.BoxShape;
import com.jvpichowski.jme3.es.bullet.components.CustomShape;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.bullet.components.WarpPosition;
import com.jvpichowski.jme3.states.ESBulletState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;

public class BasicExample extends SimpleApplication {

    public static void main(String[] args) {
        EntityData entityData = new DefaultEntityData();
        BasicExample app = new BasicExample(entityData);
        app.start();
    }

    private final EntityData entityData;

    public BasicExample(EntityData entityData){
        super(new FlyCamAppState(), new ESBulletState(entityData));
        this.entityData = entityData;
    }

    @Override
    public void simpleInitApp() {
        //Add some entities

        //This should be a rigid body with mass 0 and it should be dynamic (not kinematic).
        //The definition is the same as in bullet.
        //Every rigid body needs a collision shape. There exists no shape component for
        //planes yet. As a consequence we have to create a CustomShape component and attach
        //a PlaneCollisionShape to it. There is a factory in the background which converts this
        //collision shapes automatically to rigid objects. This collision shape could be very heavy (e.g. Terrain).
        //To circumvent this situation you could attach a custom factory and use small definition
        //objects. Examples concerning custom shape factories and more shape components will come.
        EntityId plane = entityData.createEntity();
        entityData.setComponents(plane,
                new RigidBody(false, 0),
                new CustomShape(new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y.clone(), 0))));

        //Here a simple box is created. The box should fall down and therefore it has a mass of 10kg.
        //It will be spawned at the WarpPosition. The shape is a simple box with size 1x1x1m.
        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new WarpPosition(new Vector3f(0,10,0), Quaternion.DIRECTION_Z.clone()),
                new RigidBody(false, 10),
                new BoxShape());

        //This is nearly the same as above. The box is spawned at (0,0,0) and it will be static
        //because it has no mass. It will have the same size as the other box because the half extents are
        //equal to the default ones.
        EntityId box2 = entityData.createEntity();
        entityData.setComponents(box2,
                new RigidBody(false, 0),
                new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)));

        //We could add a little force at the beginning to push the falling box slightly away.
        //The force is 10m/s^2 in each direction because the mass of the box is 10kg.
        //uncomment this line and notice the difference
        //entityData.setComponent(box, new Force(new Vector3f(100,100,100), new Vector3f()));

        //As you can see the whole system works unnoticeable in the background.
        //You add components and read components (e.g. PhysicsPosition) and you don't need to care anymore
        //how the physics is processed. Of course the physics system is accessible and
        //modifiable if you have advanced needs.

        //To see something we have to attach the bullet debug view. I didn't spend time to add fancy objects
        //which made things more complicated but there is a fancy debug view.
        //This is a very dirty way to attach the bullet debug view but we have to wait until
        //the ESBulletState is initialized. The task is called right after initialization of the esBulletState.
        ESBulletState esBulletState = stateManager.getState(ESBulletState.class);
        esBulletState.onInitialize(() -> {
            //Add Debug State to debug physics
            //As you see there are getters for physics space and so on.
            BulletDebugAppState debugAppState = new BulletDebugAppState(esBulletState.getPhysicsSpace());
            getStateManager().attach(debugAppState);
        });
    }

}
