package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class Utils {
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String deColor(String string) {
        return ChatColor.stripColor(color(string));
    }

    public static void playerMessage(Player player, String... strings) {
        for (String  string: strings) {
            player.sendMessage(color(string));
        }
    }

    public static boolean isServerVersionNewerThan(ServerVersion serverVersion) {
        ServerVersion version = AreaSoundEvents.serverVersion;
        return ServerVersion.serverVersionGreaterEqualThan(version, serverVersion);
    }

    public static String getPrefix() {
        return MessageManager.getColoredMessage("&8[&bAreaSoundEvents&8] ");
    }

    public static ServerVersion getServerVersion() {
        String bukkitName = Bukkit.getServer().getClass().getPackage().getName();
        return ServerVersion.valueOf(bukkitName.replace("org.bukkit.craftbukkit.", ""));
    }

    public static Float parseFloatArgument(String argument, float defaultValue) {
        try {
            float floatArg = Float.parseFloat(argument);
            if (floatArg >= 0 && floatArg <= 1) {
                return floatArg;
            } else {
                getLogger().warning("Float argument out of range (0~1).");
            }
        } catch (NumberFormatException e) {
            getLogger().warning(AreaSoundEvents.prefix + "Failed to parse float argument '" + argument + "'. " + e.getMessage());
            getLogger().info(AreaSoundEvents.prefix + "It will be set with a default value: " + defaultValue);
            return defaultValue;
        }

        return defaultValue;
    }

    public static int parseIntegerArgument(String argument, int defaultValue) {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            getLogger().warning(AreaSoundEvents.prefix + "Failed to parse int argument '" + argument + "'. " + e.getMessage());
            getLogger().info(AreaSoundEvents.prefix + "It will be set with a default value: " + defaultValue);
            return defaultValue;
        }
    }

    public static SoundCategory processSoundCategoryArgument(String argument, SoundCategory defaultValue) {
        for (SoundCategory soundCategory : SoundCategory.values()) {
            if (soundCategory.name().equals(argument.toUpperCase())) {
                return soundCategory;
            }
        }

        getLogger().warning(AreaSoundEvents.prefix + "Failed to get sound category argument '" + argument + "'.");
        getLogger().info(AreaSoundEvents.prefix + "It will be set with a default value: " + defaultValue);

        return defaultValue;
    }

    public static boolean parseBooleanArgument(String argument, boolean defaultValue) {
        if (argument.equalsIgnoreCase("true")) {
            return true;
        } else if (argument.equalsIgnoreCase("false")) {
            return false;
        } else {
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(Map<String, Object> properties, String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        getLogger().info(AreaSoundEvents.prefix + "'" + key + "' has an invalid or missing value. Expected Boolean.");
        getLogger().info(AreaSoundEvents.prefix + "'" + key + "' will be set with a default value: " + defaultValue);
        return defaultValue;
    }

    public static Integer getIntegerProperty(Map<String, Object> properties, String key, int defaultValue) {
        Object value = properties.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }

        getLogger().info(AreaSoundEvents.prefix + "'" + key + "' has an invalid or missing value. Expected Integer.");
        getLogger().info(AreaSoundEvents.prefix + "'" + key + "' will be set with a default value: " + defaultValue);
        return defaultValue;
    }

    public static String getStringProperty(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new IllegalArgumentException(AreaSoundEvents.prefix + "'" + key + "' has an invalid value. Expected String");
        }
    }

    public static <T extends Enum<T>> T getEnumProperty(Map<String, Object> properties, String key, Class<T> enumClass, T defaultValue) {
        Object value = properties.get(key);
        if (value instanceof String) {
            try {
                return Enum.valueOf(enumClass, ((String) value).toUpperCase());
            } catch (IllegalArgumentException e) {
                getLogger().info(AreaSoundEvents.prefix + "'" + key + "' has an invalid or missing value. Expected enum of type " + enumClass.getSimpleName() + ".");
                getLogger().info(AreaSoundEvents.prefix + "'" + key + "' will be set with a default value: " + defaultValue);
                return defaultValue;
            }
        } else {
            getLogger().info(AreaSoundEvents.prefix + "'" + key + "' has an invalid or missing value. Expected enum of type " + enumClass.getSimpleName() + ".");
            getLogger().info(AreaSoundEvents.prefix + "'" + key + "' will be set with a default value: " + defaultValue);
            return defaultValue;
        }
    }

    public static float getFloatProperty(Map<String, Object> properties, String key, float defaultValue) {
        Object value = properties.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else {
            getLogger().info(AreaSoundEvents.prefix + "'" + key + "' has an invalid or missing value. Expected numeric value.");
            getLogger().info(AreaSoundEvents.prefix + "'" + key + "' will be set with a default value: " + defaultValue);
            return defaultValue;
        }
    }
}
