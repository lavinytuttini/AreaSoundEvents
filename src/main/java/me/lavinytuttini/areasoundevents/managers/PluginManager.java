package me.lavinytuttini.areasoundevents.managers;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getLogger;

public class PluginManager {
    public static <T extends Plugin> T setPlugin(String pluginName, Class<T> pluginType) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (!(pluginType.isInstance(plugin))) {
            getLogger().severe(AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage(pluginName + " not found or is not of the correct type! Disabling plugin."));
            Bukkit.getPluginManager().disablePlugin(AreaSoundEvents.getInstance());
            return null;
        }

        Bukkit.getConsoleSender().sendMessage(AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage("Plugin '" + pluginName + "' is present."));
        return pluginType.cast(plugin);
    }
}
