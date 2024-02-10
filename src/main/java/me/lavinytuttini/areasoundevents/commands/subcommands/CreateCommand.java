package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSettings;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Map;

public class CreateCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance();
    private final DefaultSettings defaultSettings = ConfigSettings.getInstance().getDefaultSettings();
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() { return "create"; }

    @Override
    public String getDescription() { return localization.getString("commands_create_description"); }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "create &a<region-name> <sound-name> &9[source] [volume] [pitch] [minVolume]");
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandCreate();
        return (!permission.isEmpty()) ? permission : "areasoundevents.create";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args == null || args.length < 3) {
            player.sendMessage(ChatColor.RED + localization.getString("commands_common_missed_arguments"));
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
            return;
        }

        String regionName = args[1].toLowerCase();
        String soundName = args[2].toLowerCase();
        SoundCategory source = defaultSettings.getDefaultSoundCategory();
        float volume = defaultSettings.getDefaultSoundVolume();
        float pitch = defaultSettings.getDefaultSoundPitch();

        if (args.length >= 4) {
            source = Utils.processSoundCategoryArgument(args[3]);
        }
        if (args.length >= 5) {
            volume = Utils.parseFloatArgument(args[4]);
        }
        if (args.length == 6) {
            pitch = Utils.parseFloatArgument(args[5]);
        }

        Map<String, RegionData> regionDataMap = regionsSettings.getRegionDataMap();

        if (regionDataMap.containsKey(regionName)) {
            player.sendMessage(ChatColor.RED + localization.getString("commands_create_region_exists", regionName));
        } else {
            RegionData regionData = new RegionData(regionName, soundName, source, volume, pitch);
            regionDataMap.put(regionData.getName(), regionData);
            regionsSettings.setRegionDataMap(regionDataMap);

            player.sendMessage(ChatColor.GREEN + localization.getString("commands_create_created_region"));
        }
    }
}
