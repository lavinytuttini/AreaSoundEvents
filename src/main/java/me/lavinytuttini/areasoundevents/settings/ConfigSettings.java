package me.lavinytuttini.areasoundevents.settings;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.data.config.MainSettings;
import me.lavinytuttini.areasoundevents.data.config.DefaultSettings;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import static org.bukkit.Bukkit.getLogger;

public class ConfigSettings {
    private static ConfigSettings instance;
    private final AreaSoundEvents areaSoundEvents;
    private final MainSettings mainSettings = new MainSettings();
    private final DefaultSettings defaultSettings = new DefaultSettings();
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = new DefaultSubcommandPermissions();

    public ConfigSettings(AreaSoundEvents areaSoundEvents) {
        instance = this;
        this.areaSoundEvents = areaSoundEvents;
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

        if (!configFile.exists()) {
            areaSoundEvents.saveResource("config.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        loadMainSettings(config);
        loadDefaultSettings(config);
        loadDefaultSubcommandPermissions(config);
    }

    private void loadMainSettings(FileConfiguration config) {
        setConfigValue(config, "debug-mode", Boolean.class, Boolean::parseBoolean, mainSettings::setDebugMode, true);
        setConfigValue(config, "silent-mode", Boolean.class, Boolean::parseBoolean, mainSettings::setSilentMode, false);
        setConfigValue(config, "language", String.class, Function.identity(), mainSettings::setLanguage, "en_EN");
    }

    private void loadDefaultSettings(FileConfiguration config) {
        String pathDefaultSettings = "default-settings.";
        setConfigValue(config, pathDefaultSettings + "default-sound-volume", Float.class, Float::parseFloat, defaultSettings::setDefaultSoundVolume, 1.0f);
        setConfigValue(config, pathDefaultSettings + "default-sound-pitch", Float.class, Float::parseFloat, defaultSettings::setDefaultSoundPitch, 1.0f);
        setConfigValue(config, pathDefaultSettings + "default-enter-cooldown", Integer.class, Integer::parseInt, defaultSettings::setDefaultEnterCooldown, 0);
        setConfigValue(config, pathDefaultSettings + "default-leave-cooldown", Integer.class, Integer::parseInt, defaultSettings::setDefaultLeaveCooldown, 0);
        setConfigValue(config, pathDefaultSettings + "default-sound-category", SoundCategory.class, SoundCategory::valueOf, defaultSettings::setDefaultSoundCategory, SoundCategory.MASTER);
        setConfigValue(config, pathDefaultSettings + "default-list-page-size", Integer.class, Integer::parseInt, defaultSettings::setDefaultListPageSize, 2);
        setConfigValue(config, pathDefaultSettings + "default-loop-sound", Boolean.class, Boolean::parseBoolean, defaultSettings::setDefaultLoopSound, false);
        setConfigValue(config, pathDefaultSettings + "default-loop-time", Integer.class, Integer::parseInt, defaultSettings::setDefaultSoundLoopTime, 60);
    }

    private void loadDefaultSubcommandPermissions(FileConfiguration config) {
        String pathDefaultSubcommandPermissions = "default-settings.default-subcommand-permissions.";
        setConfigValue(config, pathDefaultSubcommandPermissions + "subcommand-create", String.class, Function.identity(), defaultSubcommandPermissions::setSubcommandCreate, "areasoundevents.create");
        setConfigValue(config, pathDefaultSubcommandPermissions + "subcommand-remove", String.class, Function.identity(), defaultSubcommandPermissions::setSubcommandRemove, "areasoundevents.remove");
        setConfigValue(config, pathDefaultSubcommandPermissions + "subcommand-reload", String.class, Function.identity(), defaultSubcommandPermissions::setSubcommandReload, "areasoundevents.reload");
        setConfigValue(config, pathDefaultSubcommandPermissions + "subcommand-list", String.class, Function.identity(), defaultSubcommandPermissions::setSubcommandList, "areasoundevents.list");
        setConfigValue(config, pathDefaultSubcommandPermissions + "subcommand-help", String.class, Function.identity(), defaultSubcommandPermissions::setSubcommandHelp, "areasoundevents.help");
        setConfigValue(config, pathDefaultSubcommandPermissions + "subcommand-save", String.class, Function.identity(), defaultSubcommandPermissions::setSubcommandSave, "areasoundevents.save");
        setConfigValue(config, pathDefaultSubcommandPermissions + "subcommand-modify", String.class, Function.identity(), defaultSubcommandPermissions::setSubcommandModify, "areasoundevents.modify");
    }

    private <T> void setConfigValue(FileConfiguration config, String path, Class<T> type, Function<String, T> converter, Consumer<T> setter, T defaultValue) {
        if (!config.isSet(path)) {
            Bukkit.getConsoleSender().sendMessage(AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage("'" + path + "' is not set in [config.yml]. Using default value."));
            setter.accept(defaultValue);
            return;
        }

        try {
            T value = converter.apply(config.getString(path));
            setter.accept(value);
        } catch (IllegalArgumentException e) {
            Bukkit.getConsoleSender().sendMessage(AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage("Failed to convert value for path '" + path + "' to type " + type.getSimpleName() + ". Using default value."));
            setter.accept(defaultValue);
        }
    }

    public void reload(Player player) {
        LocalizationManager localization = LocalizationManager.getInstance();

        try {
            this.loadConfig();
            player.sendMessage(ChatColor.GREEN + localization.getString("config_settings_reloaded"));
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
            player.sendMessage(ChatColor.RED + localization.getString("config_settings_error_reloading"));
        }
    }

    public static ConfigSettings getInstance() {
        return instance;
    }
}
