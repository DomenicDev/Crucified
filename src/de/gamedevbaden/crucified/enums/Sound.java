package de.gamedevbaden.crucified.enums;

import com.jme3.audio.AudioData;

/**
 * Created by Domenic on 09.05.2017.
 */
public enum Sound {

    Miss("Sounds/miss.WAV", 3, AudioData.DataType.Buffer);

    private String audioPath;
    private float duration;
    private AudioData.DataType dataType;

    Sound(String audioPath, float duration, AudioData.DataType dataType) {
        this.audioPath = audioPath;
        this.duration = duration;
        this.dataType = dataType;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public float getDuration() {
        return duration;
    }

    public float getDurationInMillis() {
        return duration * 1000;
    }

    public AudioData.DataType getDataType() {
        return dataType;
    }
}
