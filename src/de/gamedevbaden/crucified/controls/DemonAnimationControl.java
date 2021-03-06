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

public class DemonAnimationControl extends AbstractControl implements AnimEventListener {

    private int state; // movement state

    private AnimChannel wholeBody;
    private AnimChannel upperBody, lowerBody;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            // setup
            AnimControl control = spatial.getControl(AnimControl.class);
            this.wholeBody = control.createChannel();
            this.upperBody = control.createChannel();
            this.lowerBody = control.createChannel();

            this.upperBody.addBone("hips");
            this.upperBody.addFromRootBone("spine");
            this.lowerBody.addFromRootBone("thigh.R");
            this.lowerBody.addFromRootBone("thigh.L");

            control.addListener(this);
        } else {
            // cleanup
        }
    }

    public void setMovementState(int movementState) {
        this.state = movementState;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (state == CharacterMovementState.MOVING_FORWARD || state == CharacterMovementState.MOVING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) {
           setAnimation(DemonAnimation.Walk, lowerBody, upperBody);
        } else if (state == CharacterMovementState.MOVING_BACK || state == CharacterMovementState.MOVING_BACK_LEFT || state == CharacterMovementState.MOVING_BACK_RIGHT) {
            setAnimation(DemonAnimation.Walk, lowerBody, upperBody);
        } else if (state == CharacterMovementState.MOVING_LEFT) {
            setAnimation(DemonAnimation.Walk, lowerBody, upperBody);
        } else if (state == CharacterMovementState.MOVING_RIGHT) {
            setAnimation(DemonAnimation.Walk, lowerBody, upperBody);
        } else if (state == CharacterMovementState.RUNNING_FORWARD || state == CharacterMovementState.RUNNING_FORWARD_LEFT || state == CharacterMovementState.RUNNING_FORWARD_RIGHT) {
            setAnimation(DemonAnimation.Run, lowerBody, upperBody);
        } else if (state == CharacterMovementState.RUNNING_BACK || state == CharacterMovementState.RUNNING_BACK_LEFT || state == CharacterMovementState.RUNNING_BACK_RIGHT) {
            setAnimation(DemonAnimation.Run, lowerBody, upperBody);
        } else if (state == CharacterMovementState.IDLE) {
            setAnimation(DemonAnimation.Idle, lowerBody, upperBody);
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }


    private void setAnimation(DemonAnimation anim, AnimChannel... channels) {
        if (anim == null || anim.getAnimName() == null || channels == null || isAnimationForChannels(anim.getAnimName(), channels)) {
            return;
        }
        for (AnimChannel channel : channels) {
            if (channel != null) {
                channel.setAnim(anim.getAnimName(), anim.getBlendTime());
                channel.setLoopMode(LoopMode.DontLoop);
                channel.setSpeed(anim.getSpeed());
            }
        }
    }

    /*
    private void setReverseAnimation(DemonAnimation anim, AnimChannel... channels) {
        if (anim == null || anim.getAnimName() == null || channels == null || isAnimationForChannels(anim.getAnimName(), channels)) {
            return;
        }
        for (AnimChannel channel : channels) {
            if (channel != null) {
                channel.setAnim(anim.getAnimName(), anim.getBlendTime());
                channel.setLoopMode(LoopMode.DontLoop);
                channel.setSpeed(anim.getSpeed()); // we need to negate the speed, that will play it reverse
            }
        }
    }
    */


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
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if ((state == CharacterMovementState.RUNNING_FORWARD || state == CharacterMovementState.RUNNING_FORWARD_LEFT || state == CharacterMovementState.RUNNING_FORWARD_RIGHT) && animName.equals(DemonAnimation.Run.getAnimName())) {
            channel.setAnim(DemonAnimation.Run.getAnimName());
        } else if ((state == CharacterMovementState.MOVING_FORWARD || state == CharacterMovementState.MOVING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) && animName.equals(DemonAnimation.Walk.getAnimName())) {
            channel.setAnim(DemonAnimation.Walk.getAnimName());
        } else if ((state == CharacterMovementState.MOVING_BACK || state == CharacterMovementState.MOVING_BACK_LEFT || state == CharacterMovementState.MOVING_BACK_RIGHT) && animName.equals(DemonAnimation.Walk.getAnimName())) {
            channel.setAnim(DemonAnimation.Walk.getAnimName());
            //channel.setSpeed(-1);
        } else if ((state == CharacterMovementState.RUNNING_BACK || state == CharacterMovementState.RUNNING_BACK_LEFT || state == CharacterMovementState.RUNNING_BACK_RIGHT) && animName.equals(DemonAnimation.Run.getAnimName())) {
            channel.setAnim(DemonAnimation.Run.getAnimName());
            //channel.setSpeed(-1);
        } else if (state == CharacterMovementState.MOVING_LEFT) {
            channel.setAnim(DemonAnimation.Walk.getAnimName());
        } else if (state == CharacterMovementState.MOVING_RIGHT) {
           channel.setAnim(DemonAnimation.Walk.getAnimName());
        } else if (state == CharacterMovementState.IDLE) {
            channel.setAnim(DemonAnimation.Idle.getAnimName());
        }
        if (animName.equals(DemonAnimation.Walk.getAnimName())) {
            channel.setSpeed(channel.getSpeed() * DemonAnimation.Walk.getSpeed());
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

    }

    private enum DemonAnimation {

        Idle("Idle", 0.2f),
        Run("FastRun", 0.3f),
        Walk("StrongWalk", 0.3f, 2);

        DemonAnimation(String animName, float blendTime) {
            this.animName = animName;
            this.blendTime = blendTime;
            this.speed = 1f;
        }

        DemonAnimation(String animName, float blendTime, float speed) {
            this.animName = animName;
            this.blendTime = blendTime;
            this.speed = speed;
        }

        private String animName;
        private float blendTime;
        private float speed;

        public float getSpeed() {
            return speed;
        }

        String getAnimName() {
            return this.animName;
        }

        float getBlendTime() {
            return this.blendTime;
        }

    }

}
