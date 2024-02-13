package me.lavinytuttini.areasoundevents.settings;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.managers.MessageManager;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.*;

public class RegionsSettings {
    private static RegionsSettings instance;
    private final AreaSoundEvents areaSoundEvents;
    private final ConfigSettings configSettings;
    private final LocalizationManager localization;
    private final String fileName;
    private final File file;
    private final Map<String, RegionData> regionDataMap;

    public RegionsSettings(AreaSoundEvents areaSoundEvents) {
        this.areaSoundEvents = areaSoundEvents;
        this.configSettings = ConfigSettings.getInstance();
        this.localization = LocalizationManager.getInstance();
        this.fileName = "regions.yml";
        this.file = new File(areaSoundEvents.getDataFolder(), fileName);
        this.regionDataMap = new HashMap<>();
        instance = this;
    }

    public static RegionsSettings getInstance(AreaSoundEvents areaSoundEvents) {
        if (instance == null) {
            instance = new RegionsSettings(areaSoundEvents);
        }
        return instance;
    }

    public Map<String, RegionData> getRegionDataMap() {
        return Collections.unmodifiableMap(regionDataMap);
    }

    public RegionData regionDataMap(String regionId) {
        return regionDataMap.get(regionId);
    }

    public void addRegion(String regionId, RegionData regionData) {
        regionDataMap.put(regionId, regionData);
    }

    public void updateRegion(Player player, String regionId, RegionData regionData) {
        if (regionDataMap.containsKey(regionId)) {
            regionDataMap.remove(regionId);
            regionDataMap.put(regionData.getName(), regionData);
            player.sendMessage(ChatColor.GREEN + localization.getString("region_settings_successful_modified", regionId));
        } else {
            player.sendMessage(ChatColor.RED + localization.getString("region_settings_common_region_no_exists", regionId));
        }
    }

    public void removeRegion(Player player, String regionId) {
        if (regionDataMap.containsKey(regionId)) {
            regionDataMap.remove(regionId);
            player.sendMessage(ChatColor.GREEN + localization.getString("region_settings_removed_region", regionId));
        } else {
            player.sendMessage(ChatColor.RED + localization.getString("region_settings_common_region_no_exists", regionId));
        }
    }

    public void load() {
        regionDataMap.clear();

        try (FileReader reader = new FileReader(file)) {
            Yaml yaml = new Yaml();
            Map<String, Map<String, Object>> dataMap = yaml.load(reader);
            if (dataMap != null && dataMap.containsKey("regions")) {
                Map<String, Object> regionsMap = dataMap.get("regions");
                if (regionsMap.isEmpty()) {
                    saveDefaultResource();
                    load();
                } else {
                    for (Map.Entry<String, Object> entry : regionsMap.entrySet()) {
                        String regionId = entry.getKey();
                        Object value = entry.getValue();
                        if (value instanceof Map<?, ?>) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> properties = (Map<String, Object>) value;
                            processRegionProperties(regionId, properties);
                        } else {
                            Bukkit.getLogger().warning("[AreaSoundEvents] Invalid data for region '" + regionId + "'. Expected a map but we get: " + value);
                        }
                    }
                }
            } else {
                saveDefaultResource();
                load();
            }
        } catch (FileNotFoundException e) {
            saveDefaultResource();
            load();
        } catch (IOException e) {
            Bukkit.getLogger().severe("[AreaSoundEvents] Error loading regions.yml: " + e.getMessage());
        }
    }

    private void saveDefaultResource() {
        areaSoundEvents.saveResource(fileName, true);
        Bukkit.getConsoleSender().sendMessage( AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage("Default regions.yml file saved."));
    }

    private void processRegionProperties(String regionName, Map<String, Object> properties) {
        String sound = (String) properties.get("sound");
        SoundCategory source = Utils.getEnumProperty(properties, "source", SoundCategory.class, configSettings.getDefaultSettings().getDefaultSoundCategory());
        float volume = Utils.getFloatProperty(properties, "volume", configSettings.getDefaultSettings().getDefaultSoundVolume());
        float pitch = Utils.getFloatProperty(properties, "pitch", configSettings.getDefaultSettings().getDefaultSoundPitch());
        boolean loop = Utils.getBooleanProperty(properties, "loop", configSettings.getDefaultSettings().isDefaultLoopSound());
        int loopTime = Utils.getIntegerProperty(properties, "loopTime", configSettings.getDefaultSettings().getDefaultSoundLoopTime());

        RegionData regionData = new RegionData(regionName, sound, source, volume, pitch, loop, loopTime);

        regionDataMap.put(regionName, regionData);

        Bukkit.getConsoleSender().sendMessage( AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage("Region '" + regionName + "' has been correctly processed."));
    }

    public void save(Player player) {
        try {
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setPrettyFlow(true);
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Representer representer = new Representer(dumperOptions);
            representer.addClassTag(RegionData.class, Tag.MAP);

            if (!file.exists()) {
                areaSoundEvents.saveResource(fileName, false);
            }

            Yaml yaml = new Yaml(representer, dumperOptions);
            try (FileWriter writer = new FileWriter(file)) {
                yaml.dump(Collections.singletonMap("regions", getRegionDataMap()), writer);
                if (player != null) {
                    player.sendMessage(ChatColor.GREEN + localization.getString("region_settings_successful_save"));
                }
                Bukkit.getConsoleSender().sendMessage( AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage("Region settings saved successfully."));
            }
        } catch (IOException e) {
            Objects.requireNonNull(player).sendMessage(ChatColor.RED + localization.getString("region_settings_error_save"));
            Bukkit.getLogger().severe("Error saving regions.yml: " + e.getMessage());
        }
    }

    public void reload(Player player) {
        try {
            load();
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + localization.getString("region_settings_successful_reload"));
            }
            Bukkit.getLogger().info("[AreaSoundEvents] Region settings reloaded successfully.");
        } catch (Exception e) {
            Bukkit.getLogger().severe("[AreaSoundEvents] Error reloading regions.yml: " + e.getMessage());
            if (player != null) {
                player.sendMessage(ChatColor.RED + localization.getString("region_settings_error_reload"));
            }
        }
    }
}
