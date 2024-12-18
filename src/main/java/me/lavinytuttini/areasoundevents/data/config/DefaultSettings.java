package me.lavinytuttini.areasoundevents.data.config;

import org.bukkit.SoundCategory;

public class DefaultSettings {
    private SoundCategory defaultSoundCategory;
    private float defaultSoundVolume;
    private float defaultSoundPitch;
    private int defaultEnterCooldown;
    private int defaultLeaveCooldown;
    private int defaultListPageSize;
    private boolean defaultLoopSound;
    private int defaultSoundLoopTime;
    private long defaultDelayPlaySound;

    public SoundCategory getDefaultSoundCategory() {
        return defaultSoundCategory;
    }

    public void setDefaultSoundCategory(SoundCategory defaultSoundCategory) {
        this.defaultSoundCategory = defaultSoundCategory;
    }

    public float getDefaultSoundVolume() {
        return defaultSoundVolume;
    }

    public void setDefaultSoundVolume(float defaultSoundVolume) {
        this.defaultSoundVolume = defaultSoundVolume;
    }

    public float getDefaultSoundPitch() {
        return defaultSoundPitch;
    }

    public void setDefaultSoundPitch(float defaultSoundPitch) {
        this.defaultSoundPitch = defaultSoundPitch;
    }

    public int getDefaultEnterCooldown() {
        return defaultEnterCooldown;
    }

    public void setDefaultEnterCooldown(int defaultEnterCooldown) {
        this.defaultEnterCooldown = defaultEnterCooldown;
    }

    public int getDefaultLeaveCooldown() {
        return defaultLeaveCooldown;
    }

    public void setDefaultLeaveCooldown(int defaultLeaveCooldown) {
        this.defaultLeaveCooldown = defaultLeaveCooldown;
    }

    public int getDefaultListPageSize() {
        return defaultListPageSize;
    }

    public void setDefaultListPageSize(int defaultListPageSize) {
        this.defaultListPageSize = defaultListPageSize;
    }

    public boolean isDefaultLoopSound() {
        return defaultLoopSound;
    }

    public void setDefaultLoopSound(boolean defaultLoopSound) {
        this.defaultLoopSound = defaultLoopSound;
    }

    public int getDefaultSoundLoopTime() {
        return defaultSoundLoopTime;
    }

    public void setDefaultSoundLoopTime(int defaultSoundLoopTime) {
        this.defaultSoundLoopTime = defaultSoundLoopTime;
    }

    public long getDefaultDelayPlaySound() {
        return defaultDelayPlaySound;
    }

    public void setDefaultDelayPlaySound(long defaultDelayPlaySound) {
        this.defaultDelayPlaySound = defaultDelayPlaySound;
    }
}
