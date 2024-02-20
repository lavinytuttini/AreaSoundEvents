package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class StopCommand extends SubCommand {
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_stop_description");
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "stop");
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandCreate();
        return !permission.isEmpty() ? permission : "areasoundevents.stop";
    }

    @Override
    public List<String> getContext(String[] args) {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            player.stopAllSounds();
            PlayerMessage.to(player).appendLine(localization.getString("information_player_stop_all_sounds"), ChatColor.GREEN).send();
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
