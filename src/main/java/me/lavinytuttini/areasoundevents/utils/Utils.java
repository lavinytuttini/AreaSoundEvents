package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;

import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class Utils {

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

    public static Float parseFloatArgument(String arg, float defaultValue) {
        try {
            float floatArg = Float.parseFloat(arg);
            if (floatArg >= 0 && floatArg <= 1) {
                return floatArg;
            } else {
                getLogger().warning("Float argument out of range (0~1).");
            }
        } catch (NumberFormatException e) {
            getLogger().warning(AreaSoundEvents.prefix + "Failed to parse float argument '" + arg + "'. " + e.getMessage());
            getLogger().info(AreaSoundEvents.prefix + "It will be set with a default value: " + defaultValue);
            return defaultValue;
        }

        return defaultValue;
    }

    public static SoundCategory processSoundCategoryArgument(String value, SoundCategory defaultValue) {
        for (SoundCategory soundCategory : SoundCategory.values()) {
            if (soundCategory.name().equals(value.toUpperCase())) {
                return soundCategory;
            }
        }

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

    public static  <T extends Enum<T>> T getEnumProperty(Map<String, Object> properties, String key, Class<T> enumClass, T defaultValue) {
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
