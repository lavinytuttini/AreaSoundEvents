package me.lavinytuttini.areasoundevents.managers;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.utils.Prefix;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getPluginManager;

public class PluginManager {
    private static final String prefixConsole = Prefix.getPrefixConsole();

    public static <T extends Plugin> T setPlugin(String pluginName, Class<T> pluginType) {
        Plugin plugin = getPluginManager().getPlugin(pluginName);

        if (!(pluginType.isInstance(plugin))) {
            getLogger().severe(prefixConsole + pluginName + " not found or is not of the correct type! Disabling plugin.");
            getPluginManager().disablePlugin(AreaSoundEvents.getInstance());
            return null;
        }

        getLogger().info(prefixConsole + "Plugin '" + pluginName + "' is present.");
        return pluginType.cast(plugin);
    }
}
