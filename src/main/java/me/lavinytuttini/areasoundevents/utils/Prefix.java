package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import org.bukkit.ChatColor;

public class Prefix {
    private static final String prefixConsole = "[AreaSoundEvents] ";
    private static final String prefixColoredConsole = ChatColor.translateAlternateColorCodes('&',"&8[&bAreaSoundEvents&8] ");
    private static String prefixPlayerMessage;

    public static String getPrefixConsole() {
        return prefixConsole;
    }

    public static String getPrefixColoredConsole() {
        return prefixColoredConsole;
    }

    public static String getPrefixPlayerMessage() {
        if (prefixPlayerMessage != null) {
            ConfigSettings.getInstance().updatePrefixPlayerMessage(prefixPlayerMessage);
            return prefixPlayerMessage;
        }

        ConfigSettings.getInstance().updatePrefixPlayerMessage(prefixConsole);
        return prefixConsole;
    }

    public static void setPrefixPlayerMessage(String prefixPlayerMessage) {
        Prefix.prefixPlayerMessage = ChatColor.translateAlternateColorCodes( '&', prefixPlayerMessage);
    }
}
