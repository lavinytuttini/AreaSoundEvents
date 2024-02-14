package me.lavinytuttini.areasoundevents.utils;

import org.bukkit.ChatColor;

import java.util.Objects;

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
        return Objects.requireNonNullElse(prefixPlayerMessage, prefixConsole);

    }

    public static void setPrefixPlayerMessage(String prefixPlayerMessage) {
        Prefix.prefixPlayerMessage = ChatColor.translateAlternateColorCodes( '&', prefixPlayerMessage);
    }
}
