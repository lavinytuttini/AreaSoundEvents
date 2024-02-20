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

public class PlayCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance(AreaSoundEvents.getInstance());
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_play_description");
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "play &a<region-name>");
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandCreate();
        return !permission.isEmpty() ? permission : "areasoundevents.play";
    }

    @Override
    public List<String> getContext(String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(regionsSettings.getRegionDataMap().keySet());
        }

        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 2) {
            RegionData regionData = RegionsSettings.getInstance(AreaSoundEvents.getInstance()).regionDataMap(args[1]);

            if (regionData != null) {
                player.stopAllSounds();
                player.playSound(player.getLocation(), regionData.getSound(), regionData.getSource(), regionData.getVolume(), regionData.getPitch());
                PlayerMessage.to(player).appendLineFormatted(localization.getString("information_player_enters_region_sound"), ChatColor.GREEN, regionData.getSound()).send();
            } else {
                PlayerMessage.to(player).appendLineFormatted(localization.getString("region_settings_common_region_no_exists"), ChatColor.RED, args[1]).send();
            }
        } else {
            PlayerMessage.to(player)
                    .appendLine(localization.getString("commands_common_arguments_not_needed"), ChatColor.RED)
                    .appendNewLine()
                    .append("/areasoundevents ", ChatColor.YELLOW)
                    .append(this.getSyntax())
                    .send();
        }
    }
}
