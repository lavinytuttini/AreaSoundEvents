package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Map;

public class CreateCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance();

    @Override
    public String getName() { return "create"; }

    @Override
    public String getDescription() { return "Create a new event region"; }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "create &a<region-name> <sound-name> &9[source] [volume] [pitch] [minVolume]");
    }

    @Override
    public String getPermission() {
        return "areasoundevents.create";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args == null || args.length <= 2) {
            player.sendMessage(ChatColor.RED + "Sorry, you missed some arguments");
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
            return;
        }

        String regionName = args[1].toLowerCase();
        String soundName = args[2].toLowerCase();
        SoundCategory source = (args.length >= 4) ? Utils.processSoundCategoryArgument(args[3]) : SoundCategory.MUSIC; // config.yml -> default-sound-category
        float volume = (args.length >= 5) ? Utils.parseFloatArgument(args[4]) : 1.0f; // config.yml -> default-sound-volume
        float pitch = (args.length == 6) ? Utils.parseFloatArgument(args[5]) : 1.0f; // config.yml -> default-sound-pitch

        Map<String, RegionData> regionDataMap = regionsSettings.getRegionDataMap();

        if (regionDataMap.containsKey(regionName)) {
            player.sendMessage(ChatColor.RED + "A region with the name '" + regionName + "' already exists.");
        } else {
            RegionData regionData = new RegionData(regionName, soundName, source, volume, pitch);
            regionDataMap.put(regionData.getName(), regionData);
            regionsSettings.setRegionDataMap(regionDataMap);

            player.sendMessage(ChatColor.GREEN + "You have created a new area sound event");
        }
    }
}
