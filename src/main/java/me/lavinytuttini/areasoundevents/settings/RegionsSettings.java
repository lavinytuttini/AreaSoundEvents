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

import static org.bukkit.Bukkit.getLogger;

public class RegionsSettings {
    private static RegionsSettings instance;
    private final AreaSoundEvents areaSoundEvents;
    private final ConfigSettings configSettings = ConfigSettings.getInstance();
    private final LocalizationManager localization = LocalizationManager.getInstance();
    private final String fileName = "regions.yml";
    private File file;
    public Map<String, RegionData> regionDataMap = new HashMap<>();

    public RegionData regionDataMap(String regionId) {
        return regionDataMap.get(regionId);
    }

    public Map<String, RegionData> getRegionDataMap() {
        return regionDataMap;
    }

    public void setRegionDataMap(Map<String, RegionData> regionDataMap) {
        this.regionDataMap = regionDataMap;
    }

    public RegionsSettings(AreaSoundEvents areaSoundEvents) {
        instance = this;
        this.areaSoundEvents = areaSoundEvents;
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException {

        file = new File(areaSoundEvents.getDataFolder(), fileName);

        regionDataMap.clear();

        if (!file.exists()) {
            areaSoundEvents.saveResource(fileName, false);
        }

        try (FileReader reader = new FileReader(file)) {
            Yaml yaml = new Yaml();
            Map<String, RegionData> dataMap = yaml.load(reader);


            if (dataMap != null && dataMap.containsKey("regions")) {
                Map<String, Map<String, Object>> regionsMap = (Map<String, Map<String, Object>>) dataMap.get("regions");

                if (dataMap.get("regions") == null || regionsMap.isEmpty()) {
                    Bukkit.getConsoleSender().sendMessage( AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage( "There are no regions within regions.yml. You should create one."));
                    return;
                }

                for (Map.Entry<String, Map<String, Object>> entry : regionsMap.entrySet()) {
                    // String naming = entry.getValue().get("name").toString(); // TODO: Avoid using 'name' prop., get and set name from key
                    Map<String, Object> regionProperties = entry.getValue();
                    processRegionProperties(entry.getKey(), regionProperties);
                }
            } else {
                getLogger().warning(AreaSoundEvents.getPrefix() + "Missing 'regions' key in the regions.yml file.");
            }
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
        }
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
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(dumperOptions);
        representer.addClassTag(RegionData.class, Tag.MAP);

        if (!file.exists()) {
            areaSoundEvents.saveResource(fileName, false);
        }

        Yaml yaml = new Yaml(representer, dumperOptions);

// TODO: Avoid using 'name' prop., get and set name from key
//        Map<String, Map<String, Object>> modifiedRegionDataMap = new HashMap<>();
//
//        // Iterate over the regions and create modified data
//        for (Map.Entry<String, RegionData> entry : regionDataMap.entrySet()) {
//            String regionName = entry.getKey();
//            RegionData regionData = entry.getValue();
//
//            // Create a new map excluding the "name" field
//            Map<String, Object> modifiedData = new HashMap<>();
//            modifiedData.put("source", regionData.getSource());
//            modifiedData.put("volume", regionData.getVolume());
//            modifiedData.put("pitch", regionData.getPitch());
//
//            // Add modified region data to the new map
//            modifiedRegionDataMap.put(regionName, modifiedData);
//        }

        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(Collections.singletonMap("regions", regionDataMap), writer);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + localization.getString("region_settings_successful_save"));
            }
            Bukkit.getConsoleSender().sendMessage(AreaSoundEvents.getPrefix() + MessageManager.getColoredMessage("&aThe configuration was saved correctly"));
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
        }
    }

    public void reload(Player player) {
        try {
            this.load();
            player.sendMessage(ChatColor.GREEN + localization.getString("region_settings_successful_reload"));
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
            player.sendMessage(ChatColor.RED + localization.getString("region_settings_successful_error_reload"));
        }
    }

    public void modify(Player player, RegionData regionData, String regionName) {
        Map<String, RegionData> regionDataMap = this.getRegionDataMap();
        regionDataMap.remove(regionName);
        regionDataMap.put(regionData.getName(), regionData);
        this.setRegionDataMap(regionDataMap);
        player.sendMessage(ChatColor.GREEN + localization.getString("region_settings_successful_modified", regionName));
    }

    public static RegionsSettings getInstance() {
        return instance;
    }
}
