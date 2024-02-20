package me.lavinytuttini.areasoundevents.managers;

import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import me.lavinytuttini.areasoundevents.utils.Prefix;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;

import static org.bukkit.Bukkit.getLogger;

public class ConfigManager {
    private static ConfigManager instance;
    private static final String prefixConsole = Prefix.getPrefixConsole();
    private static LocalizationManager localization;

    private ConfigManager() {
        instance = this;
        localization = LocalizationManager.getInstance();
    }

    public void reload(Player player) {
        try {
            ConfigSettings.getInstance().loadConfig();
            PlayerMessage.to(player).appendLine(localization.getString("config_settings_reloaded"), ChatColor.GREEN).send();
        } catch (IOException e) {
            getLogger().severe(prefixConsole + "Error reloading config settings: " + e.getMessage());
            PlayerMessage.to(player).appendLine(localization.getString("config_settings_error_reloading"), ChatColor.RED).send();
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }

        return instance;
    }
}
