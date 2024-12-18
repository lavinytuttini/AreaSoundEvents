package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;

import javax.annotation.Nullable;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;

public class Utils {
    private static ServerVersion serverVersion = null;
    private static final String prefixConsole = Prefix.getPrefixConsole();

    public static boolean isServerVersionNewerThan(ServerVersion serverVersion) {
        ServerVersion version = AreaSoundEvents.getServerVersion();
        return version != null && version.isGreaterThanOrEqualTo(serverVersion);
    }

    public static ServerVersion getServerVersion() {
        if (serverVersion != null) {
            return serverVersion;
        }

        String bukkitVersion = Bukkit.getBukkitVersion();
        getLogger().info(prefixConsole + "Bukkit version detected: " + bukkitVersion);

        serverVersion = ServerVersion.fromString(bukkitVersion);

        if (serverVersion == ServerVersion.UNKNOWN) {
            getLogger().severe(prefixConsole + "The detected server version (" + bukkitVersion + ") is not supported by this plugin");
        } else {
            getLogger().info(prefixConsole + "Detected server version compatibility");
        }

        return serverVersion;
    }

    public static Float parseFloatArgument(String argument, float defaultValue) {
        try {
            float floatArg = Float.parseFloat(argument);
            if (floatArg >= 0 && floatArg <= 1) {
                return floatArg;
            } else {
                getLogger().warning(prefixConsole + "Float argument out of range (0~1)");
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
        return getProperty(properties, key, Boolean.class, defaultValue, Boolean.class);
    }

    public static Integer getIntegerProperty(Map<String, Object> properties, String key, int defaultValue) {
        return getProperty(properties, key, Integer.class, defaultValue, Number.class);
    }

    public static float getFloatProperty(Map<String, Object> properties, String key, float defaultValue) {
        return getProperty(properties, key, Float.class, defaultValue, Number.class);
    }

    public static <T extends Enum<T>> T getEnumProperty(Map<String, Object> properties, String key, Class<T> enumClass, T defaultValue) {
        return getProperty(properties, key, enumClass, defaultValue, String.class);
    }

    public static <T> T getProperty(Map<String, Object> properties, String key, Class<T> type, T defaultValue, Class<?> valueType) {
        Object value = properties.get(key);
        if (value != null) {
            if (type == Boolean.class && valueType == Boolean.class && value instanceof Boolean) {
                return type.cast(value);
            } else if (type == Integer.class && valueType == Number.class && value instanceof Number) {
                return type.cast(((Number) value).intValue());
            } else if (type == Float.class && valueType == Number.class && value instanceof Number) {
                return type.cast(((Number) value).floatValue());
            } else if (Enum.class.isAssignableFrom(type) && valueType == String.class && value instanceof String) {
                @SuppressWarnings("unchecked")
                Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) type;
                try {
                    return type.cast(Enum.valueOf(enumType.asSubclass(Enum.class), (String) value));
                } catch (IllegalArgumentException e) {
                    logValueException(type.getSimpleName(), key, defaultValue);
                    return defaultValue;
                }
            }
        }
        logValueException(type.getSimpleName(), key, defaultValue);
        return defaultValue;
    }

    private static void logParsingException(String type, String argument, @Nullable Exception e, Object defaultValue) {
        getLogger().warning(prefixConsole + "Failed to parse " + type + " argument '" + argument + "'. " + Objects.requireNonNull(e).getMessage());
        getLogger().info(prefixConsole + "It will be set with a default value: " + defaultValue);
    }

    private static void logValueException(String valueType, String key, Object defaultValue) {
        getLogger().warning(prefixConsole + "'" + key + "' has an invalid or missing value. Expected " + valueType);
        getLogger().info(prefixConsole + "'" + key + "' will be set with a default value: " + defaultValue);
    }
}
