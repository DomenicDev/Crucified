package de.gamedevbaden.crucified.controls;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.es.Entity;
import de.gamedevbaden.crucified.enums.ObjectCategory;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;

/**
 * Created by Domenic on 30.04.2017.
 */
public class CharacterAnimControl extends AbstractControl implements AnimEventListener {

    private Entity playerEntity;

    private AnimControl animControl;
    private AnimChannel lowerBody, upperBody, rightUpperBody, leftUpperBody;

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    public void setPlayerEntity(Entity playerEntity) {
        this.playerEntity = playerEntity;
    }

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

            AnimationChannel.LeftUpperBody.setAnimChannel(leftUpperBody);
            AnimationChannel.RightUpperBody.setAnimChannel(rightUpperBody);
            AnimationChannel.LowerBody.setAnimChannel(lowerBody);
            AnimationChannel.UpperBody.setAnimChannel(upperBody);

            // init animation
            setAnimation(Animation.Idle, AnimationChannel.values());

            setEnabled(true);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (this.playerEntity == null) {
            return;
        }

        CharacterMovementState movementState = this.playerEntity.get(CharacterMovementState.class);
        //      CharacterEquipmentState equipmentState = this.playerEntity.get(CharacterEquipmentState.class);

        int state = movementState.getMovementState();

        if (state == CharacterMovementState.MOVING_FORWARD || state == CharacterMovementState.MOVING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) {
            setAnimation(Animation.Walk, AnimationChannel.LowerBody, AnimationChannel.UpperBody);
        } else if (state == CharacterMovementState.MOVING_BACK || state == CharacterMovementState.MOVING_BACK_LEFT || state == CharacterMovementState.MOVING_BACK_RIGHT) {
            setAnimation(Animation.WalkBack, AnimationChannel.LowerBody, AnimationChannel.UpperBody);
        } else if (state == CharacterMovementState.MOVING_LEFT) {
            setAnimation(Animation.SideLeft, AnimationChannel.LowerBody);
            setAnimation(Animation.Walk, AnimationChannel.UpperBody); // for side anim we want the walk anim for upper channel
        } else if (state == CharacterMovementState.MOVING_RIGHT) {
            setAnimation(Animation.SideRight, AnimationChannel.LowerBody);
            setAnimation(Animation.Walk, AnimationChannel.UpperBody);
        } else if (state == CharacterMovementState.RUNNING_FORWARD || state == CharacterMovementState.RUNNING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) {
            setAnimation(Animation.Run, AnimationChannel.LowerBody, AnimationChannel.UpperBody);
        } else if (state == CharacterMovementState.RUNNING_BACK || state == CharacterMovementState.RUNNING_BACK_LEFT || state == CharacterMovementState.RUNNING_BACK_RIGHT) {
            setAnimation(Animation.Runback, AnimationChannel.LowerBody, AnimationChannel.UpperBody);
        } else if (state == CharacterMovementState.IDLE) {
            setAnimation(Animation.Idle, AnimationChannel.LowerBody, AnimationChannel.UpperBody);
        }

//        ObjectCategory equippedItem = equipmentState.getEquippedObject();
//        Animation equipAnimation = getEquipAnimation(equippedItem);
//        if (equipAnimation != null) {
//            // we assume that only the right arm has equipped animations
//            setAnimation(equipAnimation, CharacterChannel.RightUpperBody);
//            setAnimation(CharacterChannel.UpperBody.getLastPlayedAnim(), CharacterChannel.LeftUpperBody);
//        } else {
//            // no equipped item so set the same animation for the arms the upper body has
//            setAnimation(CharacterChannel.UpperBody.getLastPlayedAnim(), CharacterChannel.RightUpperBody, CharacterChannel.LeftUpperBody);
//        }

//        if (InputCommand.Forward.isPressed()) {
//            if (InputCommand.Shift.isPressed() && !isAnimationForChannels(Animation.Run.getAnimName(), CharacterChannel.LowerBody, CharacterChannel.UpperBody)) {
//                setAnimation(Animation.Run, CharacterChannel.UpperBody, LoopMode.DontLoop);
//                setAnimation(Animation.Run, CharacterChannel.LowerBody, LoopMode.DontLoop);
//            } else if (!InputCommand.Shift.isPressed() && !isAnimationForChannels(Animation.Walk.getAnimName(), CharacterChannel.LowerBody, CharacterChannel.UpperBody)) {
//                setAnimation(Animation.Walk, CharacterChannel.UpperBody, LoopMode.DontLoop);
//                setAnimation(Animation.Walk, CharacterChannel.LowerBody, LoopMode.DontLoop);
//            }
//        } else if (InputCommand.Backward.isPressed()) {
//            if (InputCommand.Shift.isPressed() && !isAnimationForChannels(Animation.Runback.getAnimName(), CharacterChannel.LowerBody, CharacterChannel.UpperBody)) {
//                setAnimation(Animation.Runback, CharacterChannel.UpperBody, LoopMode.DontLoop);
//                setAnimation(Animation.Runback, CharacterChannel.LowerBody, LoopMode.DontLoop);
//            } else if (!InputCommand.Shift.isPressed() && !isAnimationForChannels(Animation.WalkBack.getAnimName(), CharacterChannel.UpperBody, CharacterChannel.LowerBody)) {
//                setAnimation(Animation.WalkBack, CharacterChannel.UpperBody, LoopMode.DontLoop);
//                setAnimation(Animation.WalkBack, CharacterChannel.LowerBody, LoopMode.DontLoop);
//            }
//        } else if (InputCommand.Left.isPressed()) {
//            if (!isAnimationForChannels(Animation.SideLeft.getAnimName(), CharacterChannel.LowerBody)) {
//                setAnimation(Animation.SideLeft, CharacterChannel.LowerBody, LoopMode.DontLoop);
//            }
//            if (!isAnimationForChannels(Animation.Walk.getAnimName(), CharacterChannel.UpperBody)) {
//                setAnimation(Animation.Walk, CharacterChannel.UpperBody, LoopMode.DontLoop);
//            }
//        } else if (InputCommand.Right.isPressed()) {
//            if (!isAnimationForChannels(Animation.SideRight.getAnimName(), CharacterChannel.LowerBody)) {
//                setAnimation(Animation.SideRight, CharacterChannel.LowerBody, LoopMode.DontLoop);
//            }
//            if (!isAnimationForChannels(Animation.Walk.getAnimName(), CharacterChannel.UpperBody)) {
//                setAnimation(Animation.Walk, CharacterChannel.UpperBody, LoopMode.DontLoop);
//            }
//        } else if (!isAnimationForChannels(Animation.Idle.getAnimName(), CharacterChannel.LowerBody, CharacterChannel.UpperBody)) {
//            setAnimation(Animation.Idle, CharacterChannel.UpperBody, LoopMode.DontLoop);
//            setAnimation(Animation.Idle, CharacterChannel.LowerBody, LoopMode.DontLoop);
//        }
//
//
//        EquipmentComponent rightHand = equipState.getEquipedItem(EquipmentLocation.RightHand);
//        EquipmentComponent leftHand = equipState.getEquipedItem(EquipmentLocation.LeftHand);
//
//        if (rightHand != null && !isAnimationForChannels(rightHand.getEquipAnimation().getAnimName(), CharacterChannel.RightUpperBody)) {
//            setAnimation(rightHand.getEquipAnimation(), CharacterChannel.RightUpperBody, LoopMode.DontLoop);
//        } else if (rightHand == null && !isAnimationForChannels(CharacterChannel.UpperBody.getLastPlayedAnim().getAnimName(), CharacterChannel.RightUpperBody)) {
//            setAnimation(CharacterChannel.UpperBody.getLastPlayedAnim(), LoopMode.DontLoop, CharacterChannel.RightUpperBody);
//        }
//        if (leftHand != null && !isAnimationForChannels(leftHand.getEquipAnimation().getAnimName(), CharacterChannel.LeftUpperBody)) {
//            setAnimation(leftHand.getEquipAnimation(), CharacterChannel.LeftUpperBody, LoopMode.DontLoop);
//        } else if (leftHand == null && !isAnimationForChannels(CharacterChannel.UpperBody.getLastPlayedAnim().getAnimName(), CharacterChannel.LeftUpperBody)) {
//            setAnimation(CharacterChannel.UpperBody.getLastPlayedAnim(), LoopMode.DontLoop, CharacterChannel.LeftUpperBody);
//        }
//
//    }
    }

    private Animation getEquipAnimation(ObjectCategory equippedItem) {
        switch (equippedItem) {
            case Torch:
                return Animation.HoldTorch;
            default:
                break;
        }
        return null;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    /**
     * Will play the given Animation for the supplied channels.
     * If the given Animation is already playing then nothing will happen
     *
     * @param animation the animation which shall be played
     * @param channels  which channels are affected by the animation
     */
    public void setAnimation(Animation animation, AnimationChannel... channels) {
        if (animation == null || channels == null || isAnimationForChannels(animation.getAnimName(), channels)) {
            return;
        }
        for (AnimationChannel channel : channels) {
            if (channel != null && channel.getAnimChannel() != null) {
                channel.getAnimChannel().setAnim(animation.getAnimName(), animation.getBlendTime());
                channel.getAnimChannel().setLoopMode(LoopMode.DontLoop);
                channel.setLastPlayedAnim(animation);
            }
        }
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {

        CharacterMovementState movementState = playerEntity.get(CharacterMovementState.class);

        int state = movementState.getMovementState();

        if ((state == CharacterMovementState.RUNNING_FORWARD || state == CharacterMovementState.RUNNING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) && animName.equals(Animation.Run.getAnimName())) {
            channel.setAnim(Animation.Run.getAnimName());
        } else if ((state == CharacterMovementState.MOVING_FORWARD || state == CharacterMovementState.MOVING_FORWARD_LEFT || state == CharacterMovementState.MOVING_FORWARD_RIGHT) && animName.equals(Animation.Walk.getAnimName())) {
            channel.setAnim(Animation.Walk.getAnimName());
        } else if ((state == CharacterMovementState.MOVING_BACK || state == CharacterMovementState.MOVING_BACK_LEFT || state == CharacterMovementState.MOVING_BACK_RIGHT) && animName.equals(Animation.WalkBack.getAnimName())) {
            channel.setAnim(Animation.WalkBack.getAnimName());
        } else if ((state == CharacterMovementState.RUNNING_BACK || state == CharacterMovementState.RUNNING_BACK_LEFT || state == CharacterMovementState.MOVING_BACK_RIGHT) && animName.equals(Animation.Runback.getAnimName())) {
            channel.setAnim(Animation.Runback.getAnimName());
        } else if (state == CharacterMovementState.MOVING_LEFT) {
            if (animName.equals(Animation.SideLeft.getAnimName())) {
                channel.setAnim(Animation.SideLeft.getAnimName());
            } else if (animName.equals(Animation.Walk.getAnimName())) {
                channel.setAnim(Animation.Walk.getAnimName());
            }
        } else if (state == CharacterMovementState.MOVING_RIGHT && animName.equals(Animation.SideRight.getAnimName())) {
            channel.setAnim(Animation.SideRight.getAnimName());
            setAnimation(Animation.Walk, AnimationChannel.UpperBody);
        }


    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

    }

    private void forceAnim(Animation animation, AnimationChannel... channels) {
        for (AnimationChannel channel : channels) {
            channel.getAnimChannel().setAnim(animation.getAnimName(), animation.getBlendTime());
            channel.getAnimChannel().setLoopMode(LoopMode.DontLoop);
            channel.setLastPlayedAnim(animation);
        }

    }

    /**
     * Go through the given channels and look if they execute the same animation
     *
     * @param animName - the name of the animation to look for
     * @param channels - which channels to look for
     * @return true if all given channels execute the given animation
     */
    private boolean isAnimationForChannels(String animName, AnimationChannel... channels) {
        if (animName == null) {
            return false;
        }
        for (AnimationChannel channel : channels) {
            if (channel == null || !animName.equals(channel.getAnimChannel().getAnimationName())) {
                return false;
            }
        }
        return true;
    }

    public enum Animation {

        Idle("idle", 700),
        Walk("walk", 400),
        WalkBack("walkback", 300),
        Run("run", 350),
        Runback("runback", 300),
        SideLeft("side", 300),
        SideRight("sideback", 300),
        HoldTorch("HoldTorch", 100f);

        private String animName;
        private float blendTime;

        Animation(String animName, float blendTime) {
            this.animName = animName;
            this.blendTime = blendTime;
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

    public enum AnimationChannel {

        /**
         * Contains both legs and feet
         */
        LowerBody,

        /**
         * Right shoulder and arm
         */
        RightUpperBody,

        /**
         * Left shoulder and arm
         */
        LeftUpperBody,

        /**
         * Spines, neck and head (without arms)
         */
        UpperBody;

        /**
         * The animation which was last set on that channel.
         * Doesn't tell you if it is playing right now!
         */
        private Animation lastPlayedAnim;

        /**
         * The "real" AnimChannel this enum relies on
         */
        private AnimChannel animChannel;

        /**
         * @return the lastPlayedAnim
         */
        public Animation getLastPlayedAnim() {
            return lastPlayedAnim;
        }

        /**
         * @param lastPlayedAnim the lastPlayedAnim to set
         */
        public void setLastPlayedAnim(Animation lastPlayedAnim) {
            this.lastPlayedAnim = lastPlayedAnim;
        }

        /**
         * @return the animChannel
         */
        public AnimChannel getAnimChannel() {
            return animChannel;
        }

        /**
         * @param animChannel the animChannel to set
         */
        public void setAnimChannel(AnimChannel animChannel) {
            this.animChannel = animChannel;
        }
    }

}
