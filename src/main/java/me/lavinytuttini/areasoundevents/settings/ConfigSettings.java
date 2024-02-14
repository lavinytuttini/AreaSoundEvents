package me.lavinytuttini.areasoundevents.settings;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.data.config.MainSettings;
import me.lavinytuttini.areasoundevents.data.config.DefaultSettings;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.utils.Prefix;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Consumer;

import java.io.*;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class ConfigSettings {
    private static ConfigSettings instance;
    private final AreaSoundEvents areaSoundEvents;
    private final MainSettings mainSettings;
    private final DefaultSettings defaultSettings;
    private final DefaultSubcommandPermissions defaultSubcommandPermissions;
    private final String prefixConsole;

    public ConfigSettings(AreaSoundEvents areaSoundEvents) {
        instance = this;
        this.areaSoundEvents = areaSoundEvents;
        this.mainSettings = new MainSettings();
        this.defaultSettings = new DefaultSettings();
        this.defaultSubcommandPermissions = new DefaultSubcommandPermissions();
        this.prefixConsole = Prefix.getPrefixConsole();
    }

    public MainSettings getMainSettings() {
        return mainSettings;
    }

    public DefaultSettings getDefaultSettings() {
        return defaultSettings;
    }

    public DefaultSubcommandPermissions getDefaultSubcommandPermissions() {
        return defaultSubcommandPermissions;
    }

    public void loadConfig() throws IOException {
        File configFile = new File(areaSoundEvents.getDataFolder(), "config.yml");

        try {
            if (!configFile.exists()) {
                areaSoundEvents.saveResource("config.yml", false);
                getLogger().info( prefixConsole + "Config file not found, created new config.yml.");
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            loadMainSettings(config);
            loadDefaultSettings(config);
            loadDefaultSubcommandPermissions(config);

            getLogger().info(prefixConsole + "Config settings loaded successfully");
        } catch (Exception e) {
            getLogger().severe(prefixConsole + "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void loadMainSettings(FileConfiguration config) {
        loadSetting(config, "debug-mode", Boolean.class, mainSettings::setDebugMode, true);
        loadSetting(config, "silent-mode", Boolean.class, mainSettings::setSilentMode, false);
        loadSetting(config, "language", String.class, mainSettings::setLanguage, "en_EN");
    }

    private void loadDefaultSettings(FileConfiguration config) {
        String pathDefaultSettings = "default-settings.";
        loadSetting(config, pathDefaultSettings + "default-sound-volume", Float.class, defaultSettings::setDefaultSoundVolume, 1.0f);
        loadSetting(config, pathDefaultSettings + "default-sound-pitch", Float.class, defaultSettings::setDefaultSoundPitch, 1.0f);
        loadSetting(config, pathDefaultSettings + "default-enter-cooldown", Integer.class, defaultSettings::setDefaultEnterCooldown, 0);
        loadSetting(config, pathDefaultSettings + "default-leave-cooldown", Integer.class, defaultSettings::setDefaultLeaveCooldown, 0);
        loadSetting(config, pathDefaultSettings + "default-sound-category", SoundCategory.class, defaultSettings::setDefaultSoundCategory, SoundCategory.MASTER);
        loadSetting(config, pathDefaultSettings + "default-list-page-size", Integer.class, defaultSettings::setDefaultListPageSize, 2);
        loadSetting(config, pathDefaultSettings + "default-loop-sound", Boolean.class, defaultSettings::setDefaultLoopSound, false);
        loadSetting(config, pathDefaultSettings + "default-loop-time", Integer.class, defaultSettings::setDefaultSoundLoopTime, 60);
    }

    private void loadDefaultSubcommandPermissions(FileConfiguration config) {
        String pathDefaultSubcommandPermissions = "default-settings.default-subcommand-permissions.";
        loadSetting(config, pathDefaultSubcommandPermissions + "subcommand-create", String.class, defaultSubcommandPermissions::setSubcommandCreate, "areasoundevents.create");
        loadSetting(config, pathDefaultSubcommandPermissions + "subcommand-remove", String.class, defaultSubcommandPermissions::setSubcommandRemove, "areasoundevents.remove");
        loadSetting(config, pathDefaultSubcommandPermissions + "subcommand-reload", String.class, defaultSubcommandPermissions::setSubcommandReload, "areasoundevents.reload");
        loadSetting(config, pathDefaultSubcommandPermissions + "subcommand-list", String.class, defaultSubcommandPermissions::setSubcommandList, "areasoundevents.list");
        loadSetting(config, pathDefaultSubcommandPermissions + "subcommand-help", String.class, defaultSubcommandPermissions::setSubcommandHelp, "areasoundevents.help");
        loadSetting(config, pathDefaultSubcommandPermissions + "subcommand-save", String.class, defaultSubcommandPermissions::setSubcommandSave, "areasoundevents.save");
        loadSetting(config, pathDefaultSubcommandPermissions + "subcommand-modify", String.class, defaultSubcommandPermissions::setSubcommandModify, "areasoundevents.modify");
    }

    private <T, E extends Enum<E>> void loadSetting(FileConfiguration config, String path, Class<T> type, Consumer<T> setter, T defaultValue) {
        if (!config.isSet(path)) {
            logMissingPath(path);
            setter.accept(defaultValue);
            return;
        }

        try {
            T value;
            if (type.isEnum()) {
                @SuppressWarnings("unchecked")
                Class<E> enumType = (Class<E>) type;
                value = type.cast(Enum.valueOf(enumType, Objects.requireNonNull(config.getString(path))));
            } else {
                value = convertValue(config.getString(path), type);
            }
            setter.accept(value);
        } catch (IllegalArgumentException e) {
            logConversionFailure(path, type.getSimpleName());
            setter.accept(defaultValue);
        }
    }

    private <T> T convertValue(String value, Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return type.cast(value);
        } else if (Boolean.class.isAssignableFrom(type)) {
            return type.cast(Boolean.parseBoolean(value));
        } else if (Float.class.isAssignableFrom(type)) {
            return type.cast(Float.parseFloat(value));
        } else if (Integer.class.isAssignableFrom(type)) {
            return type.cast(Integer.parseInt(value));
        } else {
            throw new IllegalArgumentException(prefixConsole + "Unsupported type: " + type.getSimpleName());
        }
    }

    private void logMissingPath(String path) {
        getLogger().info(prefixConsole + "'" + path + "' is not set in [config.yml]. Using default value.");
    }

    private void logConversionFailure(String path, String typeName) {
        getLogger().warning(prefixConsole + "Failed to convert value for path '" + path + "' to type " + typeName + ". Using default value.");
    }

    public static ConfigSettings getInstance() {
        return instance;
    }
}
