package de.gamedevbaden.crucified.appstates;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.PlayerControl;

import java.util.HashMap;

/**
 * Created by Domenic on 12.04.2017.
 */
public class PlayerAnimationAppState extends AbstractAppState {

    private VisualizationAppState visualizationAppState;
    private HashMap<EntityId, PlayerAnimator> animators;

    private enum Animation {

        Idle("idle", 240),
        Walk("walk", 300),
        Run("run", 250);

        Animation(String animName, float blendTime) {
            this.animName = animName;
            this.blendTime = blendTime;
        }

        private String animName;
        private float blendTime;

        public String getAnimName() {
            return animName;
        }

        public float getBlendTime() {
            return blendTime;
        }
    }

    private enum AnimationChannel {

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

    }

    private EntitySet animatedPlayers;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.visualizationAppState = stateManager.getState(VisualizationAppState.class);
        this.animators = new HashMap<>();

        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        animatedPlayers = entityData.getEntities(PlayerControl.class, Model.class);

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (animatedPlayers.applyChanges()) {
            System.out.println("hdhsd");

            for (Entity entity : animatedPlayers.getAddedEntities()) {
                Spatial playerModel = visualizationAppState.getSpatial(entity.getId());
                AnimControl control = playerModel.getControl(AnimControl.class);
                PlayerAnimator animator = new PlayerAnimator(control);
                control.addListener(new PlayerAnimListener(animator, entity.get(PlayerControl.class)));
                animators.put(entity.getId(), animator);
            }

            for (Entity entity : animatedPlayers.getChangedEntities()) {
          //      ((PlayerAnimListener)animators.get(entity.getId()).getAnimControl().getAn)
            }
        }

            for (Entity entity : animatedPlayers) {

                PlayerControl player = entity.get(PlayerControl.class);
                PlayerAnimator animator = animators.get(entity.getId());

                if (player.isForward()) {
             //       System.out.println("forward");
                    if (player.isRunning() && !animator.isAnimationForChannels(Animation.Run.getAnimName(), AnimationChannel.LowerBody, AnimationChannel.UpperBody)) {
                        animator.setAnimation(Animation.Run, AnimationChannel.UpperBody, LoopMode.DontLoop);
                        animator.setAnimation(Animation.Run, AnimationChannel.LowerBody, LoopMode.DontLoop);
                    } else if (!player.isRunning() && !animator.isAnimationForChannels(Animation.Walk.getAnimName(), AnimationChannel.LowerBody, AnimationChannel.UpperBody)) {
                        animator.setAnimation(Animation.Walk, AnimationChannel.UpperBody, LoopMode.DontLoop);
                        animator.setAnimation(Animation.Walk, AnimationChannel.LowerBody, LoopMode.DontLoop);
                    }
             /*   } else if (player.isBackward()) {
                    if (player.isRunning() && !animator.isAnimationForChannels(Animation.Runback.getAnimName(), AnimationChannel.LowerBody, AnimationChannel.UpperBody)) {
                        animator.setAnimation(Animation.Runback, AnimationChannel.UpperBody, LoopMode.DontLoop);
                        animator.setAnimation(Animation.Runback, AnimationChannel.LowerBody, LoopMode.DontLoop);
                    } else if (!InputMapping.Shift.isPressed() && !isAnimationForChannels(Animation.WalkBack.getAnimName(), AnimationChannel.UpperBody, AnimationChannel.LowerBody)) {
                        animator.setAnimation(Animation.WalkBack, AnimationChannel.UpperBody, LoopMode.DontLoop);
                        animator.setAnimation(Animation.WalkBack, AnimationChannel.LowerBody, LoopMode.DontLoop);
                    }
                } else if (InputMapping.Left.isPressed()) {
                    if (!isAnimationForChannels(Animation.SideLeft.getAnimName(), AnimationChannel.LowerBody)) {
                        animator.setAnimation(Animation.SideLeft, AnimationChannel.LowerBody, LoopMode.DontLoop);
                    }
                    if (!isAnimationForChannels(Animation.Walk.getAnimName(), AnimationChannel.UpperBody)) {
                        animator.setAnimation(Animation.Walk, AnimationChannel.UpperBody, LoopMode.DontLoop);
                    }
                } else if (InputMapping.Right.isPressed()) {
                    if (!isAnimationForChannels(Animation.SideRight.getAnimName(), AnimationChannel.LowerBody)) {
                        animator.setAnimation(Animation.SideRight, AnimationChannel.LowerBody, LoopMode.DontLoop);
                    }
                    if (!isAnimationForChannels(Animation.Walk.getAnimName(), AnimationChannel.UpperBody)) {
                        animator.setAnimation(Animation.Walk, AnimationChannel.UpperBody, LoopMode.DontLoop);
                    }
               */ } else if (!animator.isAnimationForChannels(Animation.Idle.getAnimName(), AnimationChannel.LowerBody, AnimationChannel.UpperBody)) {
                  //  animator.setAnimation(Animation.Idle, AnimationChannel.UpperBody, LoopMode.DontLoop);
                  //  animator.setAnimation(Animation.Idle, AnimationChannel.LowerBody, LoopMode.DontLoop);


            }

        }

    }

    private class PlayerAnimListener implements AnimEventListener {

        private PlayerAnimator animator;
        private PlayerControl playerControl;

        public PlayerAnimListener(PlayerAnimator animator, PlayerControl playerControl) {
            this.animator = animator;
            this.playerControl = playerControl;
        }

        public void setAnimator(PlayerAnimator animator) {
            this.animator = animator;
        }

        public void setPlayerControl(PlayerControl playerControl) {
            this.playerControl = playerControl;
        }

        @Override
        public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {


            System.out.println(playerControl.isForward() + " " + animName);

            if (playerControl.isForward() && playerControl.isRunning() && animName.equals(Animation.Run.getAnimName())) {
                channel.setAnim(Animation.Run.getAnimName());
            } else if (playerControl.isForward() && animName.equals(Animation.Walk.getAnimName())) {
                System.out.println("if");
                channel.setAnim(Animation.Walk.getAnimName());
            } /*else if (playerControl.isBackward() && animName.equals(Animation.WalkBack.getAnimName())) {
                channel.setAnim(Animation.WalkBack.getAnimName());
            } else if (InputMapping.Backward.isPressed() && InputMapping.Shift.isPressed() && animName.equals(Animation.Runback.getAnimName())) {
                channel.setAnim(Animation.Runback.getAnimName());
            } else if (InputMapping.Left.isPressed() && animName.equals("side")) {
                channel.setAnim(Animation.SideLeft.getAnimName());
                upperBody.setAnim(Animation.Walk.getAnimName());
            } else if (InputMapping.Right.isPressed() && animName.equals("sideback")) {
                channel.setAnim(Animation.SideRight.getAnimName());
                upperBody.setAnim(Animation.Walk.getAnimName());
            } */




        }

        @Override
        public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

        }
    }





    private class PlayerAnimator {

        private AnimControl control;

        private AnimChannel lowerBody;
        private AnimChannel upperBody;
        private AnimChannel leftUpperBody;
        private AnimChannel rightUpperBody;

        public PlayerAnimator(AnimControl control) {
            this.control = control;
            lowerBody = control.createChannel();
            lowerBody.addFromRootBone("LHipJoint");
            lowerBody.addFromRootBone("RHipJoint");

            upperBody = control.createChannel();
            upperBody.addBone("Hips");
            upperBody.addBone("LowerBack");
            upperBody.addBone("Spine");
            upperBody.addBone("Spine1");
            upperBody.addBone("Neck");
            upperBody.addBone("Neck1");
            upperBody.addBone("Head");

            leftUpperBody = control.createChannel();
            leftUpperBody.addFromRootBone("LeftShoulder");

            rightUpperBody = control.createChannel();
            rightUpperBody.addFromRootBone("RightShoulder");

            setAnimation(Animation.Idle, LoopMode.DontLoop, AnimationChannel.values());
        }

        public AnimControl getAnimControl() {
            return control;
        }

        private AnimChannel getAnimChannel(AnimationChannel channel) {
            switch (channel) {
                case LeftUpperBody:return leftUpperBody;
                case LowerBody:return lowerBody;
                case RightUpperBody:return rightUpperBody;
                case UpperBody:return upperBody;
                default:return null;
            }
        }

        public void setAnimation(Animation animation, AnimationChannel channel, LoopMode loopMode) {
            if (animation == null || channel == null || loopMode == null) {
                return;
            }

            AnimChannel animChannel = getAnimChannel(channel);
            animChannel.setAnim(animation.getAnimName(), animation.getBlendTime());
            animChannel.setLoopMode(loopMode);
        }

        public void setAnimation(Animation animation, LoopMode loopMode, AnimationChannel... channels) {
            if (animation == null || channels == null || loopMode == null) {
                return;
            }
            for (AnimationChannel channel : channels) {
                if (channel != null) {
                    setAnimation(animation, channel, loopMode);
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
        public boolean isAnimationForChannels(String animName, AnimationChannel... channels) {
            if (animName == null) {
                return false;
            }
            for (AnimationChannel channel : channels) {
                if (channel == null || !animName.equals(getAnimChannel(channel).getAnimationName())) {
                    return false;
                }
            }
            return true;
        }

    }

    @Override
    public void cleanup() {
        animators.clear();
        animators = null;
        super.cleanup();
    }
}
