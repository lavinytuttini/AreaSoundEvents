package me.lavinytuttini.areasoundevents.data;

import org.bukkit.SoundCategory;

public class RegionData {
    private String name;
    private String sound;
    private SoundCategory source;
    private float volume;
    private float pitch;

    public RegionData(String name, String sound, SoundCategory source, float volume, float pitch) {
        this.name = name;
        this.sound = sound;
        this.source = source;
        this.volume = volume;
        this.pitch = pitch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public SoundCategory getSource() {
        return source;
    }

    public void setSource(SoundCategory source) {
        this.source = source;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}