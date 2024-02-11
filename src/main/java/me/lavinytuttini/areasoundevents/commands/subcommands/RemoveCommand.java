package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance();
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_remove_description");
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "remove &a<region-name>");
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandHelp();
        return (!permission.isEmpty()) ? permission : "areasoundevents.remove";
    }

    @Override
    public List<String> getContext(String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2) {
            for (RegionData region : regionsSettings.getRegionDataMap().values()) {
                suggestions.add(region.getName());
            }
        }

        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args == null || args.length < 2) {
            player.sendMessage(ChatColor.RED + localization.getString("commands_common_missed_arguments"));
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
            return;
        }

        String nameRegion = args[1];
        RegionData regionData = RegionsSettings.getInstance().regionDataMap(nameRegion);

        if (regionData == null) {
            player.sendMessage(ChatColor.RED + localization.getString("commands_common_region_no_exists", nameRegion));
        } else {
            Map<String, RegionData> regionDataMap = regionsSettings.getRegionDataMap();
            regionDataMap.remove(nameRegion, regionData);
            regionsSettings.setRegionDataMap(regionDataMap);
            player.sendMessage(ChatColor.GREEN + localization.getString("commands_remove_removed_region", nameRegion));
        }
    }
}
