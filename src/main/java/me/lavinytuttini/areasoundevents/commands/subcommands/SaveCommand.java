package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveCommand extends SubCommand {
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_save_description");
    }

    @Override
    public String getSyntax() {
        return "save";
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandHelp();
        return (!permission.isEmpty()) ? permission : "areasoundevents.save";
    }

    @Override
    public List<String> getContext(String[] args) {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            RegionsSettings.getInstance(AreaSoundEvents.getInstance()).save(player);
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
