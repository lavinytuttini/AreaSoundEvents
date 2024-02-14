package me.lavinytuttini.areasoundevents.data.config;

public class MainSettings {
    private boolean debugMode;
    private boolean silentMode;
    private String language;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isSilentMode() {
        return !silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
