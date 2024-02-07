package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class RemoveCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance();

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Delete the specified region";
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "remove &a<region-name>");
    }

    @Override
    public String getPermission() {
        return "areasoundevents.remove";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length >= 1) {
            String nameRegion = args[1];
            RegionData regionData = RegionsSettings.getInstance().regionDataMap(nameRegion);

            if (regionData == null) {
                player.sendMessage(ChatColor.RED + "Sorry, '" + nameRegion + "' region does not exist");
            } else {
                Map<String, RegionData> regionDataMap = regionsSettings.getRegionDataMap();
                regionDataMap.remove(nameRegion, regionData);
                regionsSettings.setRegionDataMap(regionDataMap);
                player.sendMessage(ChatColor.GREEN + "The '" + nameRegion + "' region has been removed");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Sorry, you must specify a 'region-name'");
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }
}
