package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.listeners.DoorStateListener;
import de.gamedevbaden.crucified.es.components.OpenedClosedState;
import de.gamedevbaden.crucified.es.components.PhysicsRigidBody;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.ArrayList;
import java.util.HashMap;

public class PhysicalDoorAppState extends AbstractAppState implements DoorStateListener {

    private static final Quaternion FINAL_OPENED_ROTATION = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y );
    private static final float FACTOR = 0.05f;

    private EntitySet doors;

    private ArrayList<EntityId> doorsToUpdate = new ArrayList<>();
    private ArrayList<EntityId> doorsToRemoveFromUpdate = new ArrayList<>();

    private HashMap<EntityId, Quaternion> initRotations = new HashMap<>();
    private HashMap<EntityId, Quaternion> finalRot = new HashMap<>();


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.doors = entityData.getEntities(new FieldFilter<>(PhysicsRigidBody.class, "kinematic", true), Transform.class, OpenedClosedState.class, PhysicsRigidBody.class);

        // add this listener
        stateManager.getState(DoorAppState.class).addListener(this);

        for (Entity entity : doors) {
            addDoor(entity);
        }
        super.initialize(stateManager, app);
    }

    @Override
    public void onStateChanged(EntityId entityId, boolean open) {
        if (!doors.containsId(entityId)) {
            return;
        }

        Quaternion initRot = initRotations.get(entityId);
        Quaternion finalDoorRotation = initRot.clone();
        if (open) {
            finalDoorRotation.multLocal(FINAL_OPENED_ROTATION);
        }
        finalRot.put(entityId, finalDoorRotation);
        doorsToUpdate.add(entityId);
    }

    @Override
    public void update(float tpf) {
        if (doors.applyChanges()) {
            for (Entity entity : doors.getAddedEntities()) {
                addDoor(entity);
            }
        }

        // update all doors whose state has been changed
        for (EntityId entityId : doorsToUpdate) {
            Transform t = doors.getEntity(entityId).get(Transform.class);
            Quaternion currentRotation = t.getRotation();
            Quaternion finalRotation = finalRot.get(entityId);

            currentRotation.slerp(finalRotation, tpf / FACTOR);
            currentRotation.normalizeLocal();

            // if we are near the final rotation we want
            // the current door not being updated anymore
            if (isCloseEnough(currentRotation, finalRotation)) {
                doorsToRemoveFromUpdate.add(entityId);
                currentRotation.set(finalRotation);
                currentRotation.normalizeLocal();
            }
            // apply new rotation
            doors.getEntity(entityId).set(new Transform(t.getTranslation(), currentRotation.clone(), t.getScale()));
        }

        doorsToUpdate.removeAll(doorsToRemoveFromUpdate);
        doorsToRemoveFromUpdate.clear();
    }

    private boolean isCloseEnough(Quaternion q1, Quaternion q2) {
        float[] angles = new float[3];
        q1.toAngles(angles);
        float v1 = angles[1];

        q2.toAngles(angles);
        float v2 = angles[1];

        v1 *= FastMath.RAD_TO_DEG;
        v2 *= FastMath.RAD_TO_DEG;

        // return true if diff is 0.1 degrees
        return v1 >= v2 ? (v1-v2 <= 0.1f) : (v2-v1 <= 0.1f);
    }

    private void addDoor(Entity entity) {
        Quaternion currentRot = entity.get(Transform.class).getRotation().clone();
        initRotations.put(entity.getId(), currentRot);
    }

    @Override
    public void cleanup() {
        this.doors.release();
        this.doors.clear();
        this.doors = null;

        this.doorsToUpdate.clear();
        super.cleanup();
    }

}
