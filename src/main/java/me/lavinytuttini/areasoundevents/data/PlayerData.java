package me.lavinytuttini.areasoundevents.data;

import org.bukkit.SoundCategory;

public class PlayerData {
    private String sound;
    private SoundCategory source;

    private String region;

    public PlayerData(String sound, SoundCategory source, String region) {
        this.sound = sound;
        this.source = source;
        this.region = region;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
