package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class Utils {
    private static ServerVersion serverVersion = null;

    public static boolean isServerVersionNewerThan(ServerVersion serverVersion) {
        ServerVersion version = AreaSoundEvents.serverVersion;
        return version != null && version.isGreaterThanOrEqualTo(serverVersion);
    }

    public static String getPrefix() {
        return MessageManager.getColoredMessage("&8[&bAreaSoundEvents&8] ");
    }

    public static ServerVersion getServerVersion() {
        if (serverVersion != null) {
            return serverVersion;
        }

        String bukkitName = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = ServerVersion.valueOf(bukkitName.replace("org.bukkit.craftbukkit.", ""));
        return serverVersion;
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
            logParsingException("Float", argument, e, defaultValue);
            return defaultValue;
        }

        return defaultValue;
    }

    public static int parseIntegerArgument(String argument, int defaultValue) {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            logParsingException("Integer", argument, e, defaultValue);
            return defaultValue;
        }
    }

    public static SoundCategory processSoundCategoryArgument(String argument, SoundCategory defaultValue) {
        try {
            return SoundCategory.valueOf(argument.toUpperCase());
        } catch (IllegalArgumentException e) {
            logParsingException("Sound Category", argument, e, defaultValue);
            return defaultValue;
        }
    }

    public static boolean parseBooleanArgument(String argument, boolean defaultValue) {
        if (argument.equalsIgnoreCase("true")) {
            return true;
        } else if (argument.equalsIgnoreCase("false")) {
            return false;
        }

        return defaultValue;
    }

    public static boolean getBooleanProperty(Map<String, Object> properties, String key, boolean defaultValue) {
        return getProperty(properties, key, Boolean.class, defaultValue);
    }

    public static Integer getIntegerProperty(Map<String, Object> properties, String key, int defaultValue) {
        return getProperty(properties, key, Integer.class, defaultValue);
    }

    public static <T extends Enum<T>> T getEnumProperty(Map<String, Object> properties, String key, Class<T> enumClass, T defaultValue) {
        return getProperty(properties, key, enumClass, defaultValue);
    }

    public static float getFloatProperty(Map<String, Object> properties, String key, float defaultValue) {
        return getProperty(properties, key, Float.class, defaultValue);
    }

    private static <T> T getProperty(Map<String, Object> properties, String key, Class<T> type, T defaultValue) {
        Object value = properties.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            logValueException(type.getSimpleName(), key, defaultValue);
            return defaultValue;
        }
    }

    private static void logParsingException(String type, String argument, @Nullable Exception e, Object defaultValue) {
        getLogger().warning(AreaSoundEvents.prefix + "Failed to parse " + type + " argument '" + argument + "'. " + Objects.requireNonNull(e).getMessage());
        getLogger().info(AreaSoundEvents.prefix + "It will be set with a default value: " + defaultValue);
    }

    private static void logValueException(String valueType, String key, Object defaultValue) {
        getLogger().warning(AreaSoundEvents.prefix + "'" + key + "' has an invalid or missing value. Expected " + valueType + ".");
        getLogger().info(AreaSoundEvents.prefix + "'" + key + "' will be set with a default value: " + defaultValue);
    }
}
