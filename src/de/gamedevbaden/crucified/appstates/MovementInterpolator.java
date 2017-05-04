package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.OnMovement;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.HashMap;

/**
 * This class is used on client side to interpolate between positions of dynamic objects
 * <p>
 * Created by Domenic on 26.04.2017.
 */
public class MovementInterpolator extends AbstractAppState {

    private EntitySet movingEntities;
    private ModelViewAppState modelViewAppState;
    private HashMap<EntityId, TransformComparator> transforms = new HashMap<>();


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        movingEntities = entityData.getEntities(OnMovement.class, Transform.class, Model.class);

        super.initialize(stateManager, app);
    }


    @Override
    public void update(float tpf) {

        if (movingEntities.applyChanges()) {

            for (Entity entity : movingEntities.getAddedEntities()) {
                Transform oldTransform = modelViewAppState.getOldTransform(entity.getId());
                Transform newTransform = entity.get(Transform.class);

                transforms.put(entity.getId(), new TransformComparator(oldTransform.getTranslation(), oldTransform.getRotation(), newTransform.getTranslation(), newTransform.getRotation()));
            }

            for (Entity entity : movingEntities.getChangedEntities()) {
                TransformComparator tc = transforms.get(entity.getId());

                Transform oldTransform = modelViewAppState.getOldTransform(entity.getId());
                Transform newTransform = entity.get(Transform.class); // the updated transform is the new one

                tc.setOldTranslation(oldTransform.getTranslation());
                tc.setOldRotation(oldTransform.getRotation());

                tc.setNewTranslation(newTransform.getTranslation());
                tc.setNewRotation(newTransform.getRotation());
            }

            for (Entity entity : movingEntities.getRemovedEntities()) {
                transforms.remove(entity.getId());
            }

        }

        // interpolate for all moving entities
        for (Entity entity : movingEntities) {

            Spatial model = modelViewAppState.getSpatial(entity.getId());
            TransformComparator tc = transforms.get(entity.getId());

            Vector3f oldTranslation = tc.getOldTranslation();
            Vector3f newTranslation = tc.getNewTranslation();

            Quaternion oldRotation = tc.getOldRotation();
            Quaternion newRotation = tc.getNewRotation();

            Vector3f interpolatedTranslation = new Vector3f(oldTranslation).interpolateLocal(newTranslation, tpf / 0.06f);
            Quaternion interpolatedRotation = oldRotation.slerp(oldRotation, newRotation, tpf / 0.06f);

            // apply interpolation to model
            model.setLocalTranslation(interpolatedTranslation);
            model.setLocalRotation(interpolatedRotation);

            // save interpolated values as "old" values, so they can be used in next frame
            tc.setOldTranslation(interpolatedTranslation);
            tc.setOldRotation(interpolatedRotation);

        }

    }

    @Override
    public void cleanup() {
        movingEntities.release();
        movingEntities = null;

        transforms.clear();
        transforms = null;
        modelViewAppState = null;

        super.cleanup();
    }

    /**
     * This class holds old and new Translation as well as Rotation for interpolation.
     */
    private class TransformComparator {

        private Vector3f oldTranslation;
        private Quaternion oldRotation;

        private Vector3f newTranslation;
        private Quaternion newRotation;

        TransformComparator(Vector3f oldTranslation, Quaternion oldRotation, Vector3f newTranslation, Quaternion newRotation) {
            this.oldTranslation = oldTranslation;
            this.oldRotation = oldRotation;
            this.newTranslation = newTranslation;
            this.newRotation = newRotation;
        }

        Vector3f getOldTranslation() {
            return oldTranslation;
        }

        void setOldTranslation(Vector3f oldTranslation) {
            this.oldTranslation = oldTranslation;
        }

        Quaternion getOldRotation() {
            return oldRotation;
        }

        void setOldRotation(Quaternion oldRotation) {
            this.oldRotation = oldRotation;
        }

        Vector3f getNewTranslation() {
            return newTranslation;
        }

        void setNewTranslation(Vector3f newTranslation) {
            this.newTranslation = newTranslation;
        }

        Quaternion getNewRotation() {
            return newRotation;
        }

        void setNewRotation(Quaternion newRotation) {
            this.newRotation = newRotation;
        }
    }
}
