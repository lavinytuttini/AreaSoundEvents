package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Prefix;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();
    private final String prefixPlayerMessage = Prefix.getPrefixPlayerMessage();

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_reload_description");
    }

    @Override
    public String getSyntax() {
        return "reload";
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandHelp();
        return (!permission.isEmpty()) ? permission : "areasoundevents.reload";
    }

    @Override
    public List<String> getContext(String[] args) {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            ConfigSettings.getInstance().reload(player);
            RegionsSettings.getInstance(AreaSoundEvents.getInstance()).reload(player);
            localization.reload();
        } else {
            player.sendMessage(prefixPlayerMessage + ChatColor.RED + localization.getString("commands_common_arguments_not_needed"));
            player.sendMessage(prefixPlayerMessage + ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }
}
