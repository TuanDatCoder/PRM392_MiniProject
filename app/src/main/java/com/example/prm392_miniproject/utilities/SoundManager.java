package com.example.prm392_miniproject.utilities;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager {
    private SoundPool soundPool;
    private HashMap<String, Integer> soundMap;
    private Context context;

    public SoundManager(Context context) {
        this.context = context;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap = new HashMap<>();
    }

    public void loadSound(String soundName, int resourceId) {
        int soundId = soundPool.load(context, resourceId, 1);
        soundMap.put(soundName, soundId);
    }

    public void playSound(String soundName) {
        Integer soundId = soundMap.get(soundName);
        if (soundId != null) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    public void stopAllSounds() {
        for (Integer soundId : soundMap.values()) {
            soundPool.stop(soundId);
        }
    }

    public void release() {
        soundPool.release();
    }
}
