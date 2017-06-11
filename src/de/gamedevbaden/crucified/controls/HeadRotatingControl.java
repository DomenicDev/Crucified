package de.gamedevbaden.crucified.controls;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * A control which sets the head bone rotation according to the set view direction.
 * <p>
 * Created by Domenic on 09.06.2017.
 */
public class HeadRotatingControl extends AbstractControl {

    private Vector3f compVector = new Vector3f();
    private Vector3f oldViewDir = new Vector3f();
    private Vector3f viewDirection = new Vector3f(Vector3f.UNIT_X);

    private Quaternion headRotation = new Quaternion();
    private Quaternion finalHeadRotation = new Quaternion();

    private float[] initAngles = new float[3];

    private Bone headBone;

    public HeadRotatingControl() {
    }

    /**
     * Applies the specified viewDirection as new facing direction for the head
     *
     * @param viewDirection the direction the head shall face
     */
    public void setViewDirection(Vector3f viewDirection) {
        this.viewDirection.set(viewDirection).normalizeLocal();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            // setup
            SkeletonControl skeletonControl = spatial.getControl(SkeletonControl.class);
            this.headBone = skeletonControl.getSkeleton().getBone("Neck1");
            this.headBone.setUserControl(true);
            this.headBone.getLocalRotation().toAngles(initAngles);
            this.headRotation.set(headBone.getLocalRotation());
        } else {
            // cleanup
            if (headBone != null) {
                headBone.setUserControl(false);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (headBone != null) {
            headBone.setUserControl(enabled);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        // if true --> viewDirection has been updated, so we need to set a new rotation for the head
        if (!oldViewDir.equals(viewDirection)) {

            // note: we not to operate with a vector here instead of a quaternion
            // we compute the new angle with the new view direction and the compVector
            // the compVector has the same coordinates as the viewDirection
            // except that its y-value is set to zero
            // this  could have of cause been done differently (and better) too probably
            // the following "illustration" shows how the vectors are used generally


            //       ^ (viewDirection)                  ^ (y)
            //      /                                   |
            //     /                                    |_ _ _ _> (x)
            //    /   <<-- angle                       /
            //    -----------> (compVector)          / (z)

            compVector.set(viewDirection.getX(), 0, viewDirection.getZ());

            // get angle between compVector and viewDirection vector
            float angle = viewDirection.angleBetween(compVector);

            // The angle will always be positive
            // so looking up or down wouldn't make any difference
            // That's why we need to negate it when the player is looking up
            // otherwise the head bone rotation will "go" back again

            Vector3f vec = viewDirection.subtract(spatial.getLocalRotation().getRotationColumn(2));
            if (vec.getY() > 0) {
                angle *= -1; // if the character is looking up we negate the angle
            }

            // we now compute the new head bone rotation
            // therefore we need to include the init x-rotation
            finalHeadRotation.fromAngles(initAngles[0] + angle, 0, 0);

            // we update our oldViewDirection vector to avoid that all this
            // is computed more than necessary
            oldViewDir.set(viewDirection);
        }

        // interpolate to new head rotation
        headRotation.slerp(finalHeadRotation, tpf / 0.05f);

        // before we set the new user transform we need to normalize the head rotation
        // otherwise the head will "jump" away from his body when the
        // player movements are too fast
        headRotation.normalizeLocal();

        // finally apply new user transforms
        headBone.setUserTransforms(Vector3f.ZERO, headRotation, Vector3f.UNIT_XYZ);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
