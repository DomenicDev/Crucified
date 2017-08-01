package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.OpenedClosedState;
import de.gamedevbaden.crucified.es.components.PhysicsRigidBody;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;
import java.util.Map;

/**
 * This state watches door entities and changes their rotation (Transform component) depending on their
 * {@link OpenedClosedState} component.
 *
 * @author Domenic
 */
public class DoorAppState extends AbstractAppState {

    private static final Quaternion FINAL_OPENED_ROTATION = new Quaternion().fromAngles(new float[] {0, 90* FastMath.DEG_TO_RAD, 0} );
    private static final Quaternion FINAL_CLOSED_ROTATION = new Quaternion().fromAngles(0, 0, 0);
    private static final float FACTOR = 0.05f;

    private EntitySet doors;
    private HashMap<EntityId, Boolean> doorsToUpdate = new HashMap<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.doors = entityData.getEntities(Transform.class, OpenedClosedState.class, PhysicsRigidBody.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (doors.applyChanges()) {
            for (Entity entity : doors.getChangedEntities()) {
                OpenedClosedState state = entity.get(OpenedClosedState.class);
                doorsToUpdate.put(entity.getId(), state.isOpened());
            }
        }

        for (Map.Entry<EntityId, Boolean> e : doorsToUpdate.entrySet()) {
            Transform t = doors.getEntity(e.getKey()).get(Transform.class);
            Quaternion rot = t.getRotation();
            if (e.getValue()) {
                rot.slerp(FINAL_OPENED_ROTATION, FACTOR);
                rot.normalizeLocal();
            } else {
                rot.slerp(FINAL_CLOSED_ROTATION, FACTOR);
                rot.normalizeLocal();
            }
            // apply new rotation
            doors.getEntity(e.getKey()).set(new Transform(t.getTranslation(), rot, t.getScale()));
        }

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
