package de.gamedevbaden.crucified.controls.animation;

/**
 * Created by Domenic on 04.06.2017.
 */
public class Animation {

    private String animName;
    private float blendTime;

    public Animation() {
    }

    public Animation(String animName) {
        this.animName = animName;
    }

    public Animation(String animName, float blendTime) {
        this.animName = animName;
        this.blendTime = blendTime;
    }

    public String getAnimName() {
        return animName;
    }

    public void setAnimName(String animName) {
        this.animName = animName;
    }

    public float getBlendTime() {
        return blendTime;
    }

    public void setBlendTime(float blendTime) {
        this.blendTime = blendTime;
    }
}
