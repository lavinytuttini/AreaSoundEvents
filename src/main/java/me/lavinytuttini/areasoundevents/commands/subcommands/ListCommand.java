package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance();
    private final Pagination pagination = Pagination.getInstance();
    private final List<String> messages = new ArrayList<>();

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Shows all configured regions";
    }

    @Override
    public String getSyntax() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "areasoundevents.list";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            Map<String, RegionData> regionDataMap = regionsSettings.getRegionDataMap();

            messages.clear();
            if (regionDataMap.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "There are no region entries in the map.");
            } else {
                for (Map.Entry<String, RegionData> entry : regionDataMap.entrySet()) {
                    messages.add(getMessage(entry));
                }

                pagination.init(player, messages);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Sorry, you do not need any other argument");
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }

    private static String getMessage(Map.Entry<String, RegionData> entry) {
        String regionName = entry.getKey();
        RegionData regionData = entry.getValue();

        String soundName = regionData.getSound();
        SoundCategory source = regionData.getSource();
        float volume = regionData.getVolume();
        float pitch = regionData.getPitch();

        return ChatColor.GREEN + "Region: " + ChatColor.GOLD + regionName + "\n" +
                ChatColor.GREEN + " | Sound: " + ChatColor.WHITE + soundName + "\n" +
                ChatColor.GREEN + " | Source: " + ChatColor.WHITE + source + "\n" +
                ChatColor.GREEN + " | Volume: " + ChatColor.WHITE + volume + "\n" +
                ChatColor.GREEN + " | Pitch: " + ChatColor.WHITE + pitch;
    }
}
