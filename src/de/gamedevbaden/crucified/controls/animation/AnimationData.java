package de.gamedevbaden.crucified.controls.animation;

import com.jme3.animation.AnimChannel;

/**
 * Created by Domenic on 04.06.2017.
 */
public class AnimationData {

    private Animation[] animations;
    private AnimChannel[] channels;

    public AnimationData() {
    }

    public AnimationData(Animation[] animations, AnimChannel[] channels) {
        this.animations = animations;
        this.channels = channels;
    }

    public void setAnimations(Animation... anims) {
        this.animations = anims;
    }

    public void setAnimChannels(AnimChannel... channels) {
        this.channels = channels;
    }

    public void playAnim(Animation animation, AnimChannel... channels) {

        if (animation == null) {
            return;
        }
        playAnim(animation.getAnimName(), animation.getBlendTime(), channels);
    }

    public void playAnim(String animName, float blendTime, AnimChannel... channels) {

    }


}
