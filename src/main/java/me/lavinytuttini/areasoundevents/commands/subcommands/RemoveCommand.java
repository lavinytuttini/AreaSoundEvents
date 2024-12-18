package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance(AreaSoundEvents.getInstance());
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
            PlayerMessage.to(player)
                    .appendLine(localization.getString("commands_common_missed_arguments"), ChatColor.RED)
                    .appendNewLine()
                    .append("/areasoundevents ", ChatColor.YELLOW)
                    .append(this.getSyntax())
                    .send();
            return;
        }

        String nameRegion = args[1];
        regionsSettings.removeRegion(player, nameRegion);
    }
}
