package de.gamedevbaden.crucified.controls;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;

/**
 * Created by Domenic on 04.06.2017.
 */
public class NewCharacterAnimControl extends AbstractControl implements AnimEventListener {

    private AnimControl animControl;
    private AnimChannel lowerBody, upperBody, rightUpperBody, leftUpperBody;

    private int state; // movement state
    private String equippedModel;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            animControl = spatial.getControl(AnimControl.class);
            upperBody = animControl.createChannel();
            lowerBody = animControl.createChannel();
            rightUpperBody = animControl.createChannel();
            leftUpperBody = animControl.createChannel();

            lowerBody.addFromRootBone("LHipJoint");
            lowerBody.addFromRootBone("RHipJoint");

            upperBody.addBone("Hips");
            upperBody.addBone("LowerBack");
            upperBody.addBone("Spine");
            upperBody.addBone("Spine1");
            upperBody.addBone("Neck");
            upperBody.addBone("Neck1");
            upperBody.addBone("Head");

            leftUpperBody.addFromRootBone("LeftShoulder");
            rightUpperBody.addFromRootBone("RightShoulder");

            animControl.addListener(this);

            // init animation
            setAnimation(CharacterAnimation.Idle, lowerBody, upperBody);

            setEnabled(true);
        }
    }

    public void setMovementState(int state) {
        this.state = state;
    }

    public void setEquippedModel(String equippedModel) {
        this.equippedModel = equippedModel;
    }

    private void setAnimation(CharacterAnimation anim, AnimChannel... channels) {
        if (anim == null || anim.getAnimName() == null || channels == null || isAnimationForChannels(anim.getAnimName(), channels)) {
            return;
        }
        for (AnimChannel channel : channels) {
            if (channel != null) {
                channel.setAnim(anim.getAnimName(), anim.getBlendTime());
                channel.setLoopMode(LoopMode.DontLoop);
            }
        }
    }


    /**
     * Go through the given channels and look if they execute the same animation
     *
     * @param animName - the name of the animation to look for
     * @param channels - which channels to look for
     * @return true if all given channels execute the given animation
     */
    private boolean isAnimationForChannels(String animName, AnimChannel... channels) {
        if (animName == null) {
            return false;
        }
        for (AnimChannel channel : channels) {
            if (channel == null || !animName.equals(channel.getAnimationName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (state == CharacterMovementState.MOVING_FORWARD || state == CharacterMovementState.MOVING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) {
            setAnimation(CharacterAnimation.Walk, lowerBody, upperBody);
        } else if (state == CharacterMovementState.MOVING_BACK || state == CharacterMovementState.MOVING_BACK_LEFT || state == CharacterMovementState.MOVING_BACK_RIGHT) {
            setAnimation(CharacterAnimation.WalkBack, lowerBody, upperBody);
        } else if (state == CharacterMovementState.MOVING_LEFT) {
            setAnimation(CharacterAnimation.SideLeft, lowerBody);
            setAnimation(CharacterAnimation.Walk, upperBody); // for side anim we want the walk anim for upper channel
        } else if (state == CharacterMovementState.MOVING_RIGHT) {
            setAnimation(CharacterAnimation.SideRight, lowerBody);
            setAnimation(CharacterAnimation.Walk, upperBody);
        } else if (state == CharacterMovementState.RUNNING_FORWARD || state == CharacterMovementState.RUNNING_FORWARD_LEFT || state == CharacterMovementState.RUNNING_FORWARD_RIGHT) {
            setAnimation(CharacterAnimation.Run, lowerBody, upperBody);
        } else if (state == CharacterMovementState.RUNNING_BACK || state == CharacterMovementState.RUNNING_BACK_LEFT || state == CharacterMovementState.RUNNING_BACK_RIGHT) {
            setAnimation(CharacterAnimation.Runback, lowerBody, upperBody);
        } else if (state == CharacterMovementState.IDLE) {
            setAnimation(CharacterAnimation.Idle, lowerBody, upperBody);

        }

        if (equippedModel != null) {
            // play equip anim
        } else {
            // apply animation for other channels
            setAnimation(CharacterAnimation.getByAnimName(upperBody.getAnimationName()), rightUpperBody, leftUpperBody);

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if ((state == CharacterMovementState.RUNNING_FORWARD || state == CharacterMovementState.RUNNING_FORWARD_LEFT || state == CharacterMovementState.RUNNING_FORWARD_RIGHT) && animName.equals(CharacterAnimation.Run.getAnimName())) {
            channel.setAnim(CharacterAnimation.Run.getAnimName());
        } else if ((state == CharacterMovementState.MOVING_FORWARD || state == CharacterMovementState.MOVING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) && animName.equals(CharacterAnimation.Walk.getAnimName())) {
            channel.setAnim(CharacterAnimation.Walk.getAnimName());
        } else if ((state == CharacterMovementState.MOVING_BACK || state == CharacterMovementState.MOVING_BACK_LEFT || state == CharacterMovementState.MOVING_BACK_RIGHT) && animName.equals(CharacterAnimation.WalkBack.getAnimName())) {
            channel.setAnim(CharacterAnimation.WalkBack.getAnimName());
        } else if ((state == CharacterMovementState.RUNNING_BACK || state == CharacterMovementState.RUNNING_BACK_LEFT || state == CharacterMovementState.RUNNING_BACK_RIGHT) && animName.equals(CharacterAnimation.Runback.getAnimName())) {
            channel.setAnim(CharacterAnimation.Runback.getAnimName());
        } else if (state == CharacterMovementState.MOVING_LEFT) {
            if (animName.equals(CharacterAnimation.SideLeft.getAnimName())) {
                channel.setAnim(CharacterAnimation.SideLeft.getAnimName());
            } else if (animName.equals(CharacterAnimation.Walk.getAnimName())) {
                channel.setAnim(CharacterAnimation.Walk.getAnimName());
            }
        } else if (state == CharacterMovementState.MOVING_RIGHT) {
            if (animName.equals(CharacterAnimation.SideRight.getAnimName())) {
                channel.setAnim(CharacterAnimation.SideRight.getAnimName());
            } else if (animName.equals(CharacterAnimation.Walk.getAnimName())) {
                channel.setAnim(CharacterAnimation.Walk.getAnimName());
            }
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

    }

    public enum CharacterAnimation {

        Idle("idle", 700),
        Walk("walk", 400),
        WalkBack("walkback", 300),
        Run("run", 350),
        Runback("runback", 300),
        SideLeft("side", 300),
        SideRight("sideback", 300),
        HoldTorch("HoldTorch", 100f),
        T_Pose("T-Pose", 50);

        private String animName;
        private float blendTime;

        CharacterAnimation(String animName, float blendTime) {
            this.animName = animName;
            this.blendTime = blendTime;
        }

        public static CharacterAnimation getByAnimName(String animName) {
            for (CharacterAnimation animation : values()) {
                if (animation.getAnimName().equals(animName)) {
                    return animation;
                }
            }
            return null;
        }

        /**
         * @return the animName
         */
        public String getAnimName() {
            return animName;
        }

        /**
         * @param animName the animName to set
         */
        public void setAnimName(String animName) {
            this.animName = animName;
        }

        /**
         * @return the blendTime
         */
        public float getBlendTime() {
            return blendTime;
        }

        /**
         * @param blendTime the blendTime to set
         */
        public void setBlendTime(float blendTime) {
            this.blendTime = blendTime;
        }
    }
}
