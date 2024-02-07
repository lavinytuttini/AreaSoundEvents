package me.lavinytuttini.areasoundevents.settings;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.data.RegionData;
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
    private final AreaSoundEvents areaSoundEvents = AreaSoundEvents.getInstance();
    private final String fileName = "regions.yml";
    private static RegionsSettings instance = new RegionsSettings();
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

    @SuppressWarnings("unchecked")
    public void load() throws IOException {
        file = new File(areaSoundEvents.getDataFolder(), fileName);

        if (!file.exists()) {
            areaSoundEvents.saveResource(fileName, false);
        }

        regionDataMap.clear();

        try (FileReader reader = new FileReader(file)) {
            Yaml yaml = new Yaml();
            Map<String, RegionData> dataMap = yaml.load(reader);

            if (dataMap.containsKey("regions")) {
                Map<String, Map<String, Object>> regionsMap = (Map<String, Map<String, Object>>) dataMap.get("regions");
                for (Map.Entry<String, Map<String, Object>> entry : regionsMap.entrySet()) {
                    String regionName = entry.getKey();
                    String naming = entry.getValue().get("name").toString(); // TODO: Avoid using 'name' prop., get and set name from key
                    Map<String, Object> regionProperties = entry.getValue();
                    processRegionProperties(regionName, regionProperties);
                }
            } else {
                getLogger().warning("Missing 'regions' key in the YAML file.");
            }
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
        }
    }

    private void processRegionProperties(String regionName, Map<String, Object> properties) {
        String sound = Utils.getStringProperty(properties, "sound");
        SoundCategory source = Utils.getEnumProperty(properties, "source", SoundCategory.class, SoundCategory.MUSIC);
        float volume = Utils.getFloatProperty(properties, "volume", 1.0f);
        float pitch = Utils.getFloatProperty(properties, "pitch", 1.0f);

        RegionData regionData = new RegionData(regionName, sound, source, volume, pitch);

        regionDataMap.put(regionName, regionData);

        getLogger().info(AreaSoundEvents.prefix + "Region '" + regionName + "' has been correctly processed.");
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

        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(Collections.singletonMap("regions", regionDataMap), writer);
            if (player != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThe configuration was saved correctly"));
            }
            Bukkit.getConsoleSender().sendMessage(AreaSoundEvents.prefix + MessageManager.getColoredMessage("&aThe configuration was saved correctly"));
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
        }
    }

    public void reload(Player player) {
        try {
            this.load();
            player.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully.");
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
            player.sendMessage(ChatColor.RED + "Error reloading configuration. Check server logs for details.");
        }
    }

    public void modify(Player player, RegionData regionData, String regionName) {
        Map<String, RegionData> regionDataMap = this.getRegionDataMap();
        regionDataMap.remove(regionName);
        regionDataMap.put(regionData.getName(), regionData);
        this.setRegionDataMap(regionDataMap);
        player.sendMessage(ChatColor.GREEN + "Region '" + regionName + "' has been modified");
    }

    public static RegionsSettings getInstance() {
        if (instance == null) {
            instance = new RegionsSettings();
        }

        return instance;
    }
}
