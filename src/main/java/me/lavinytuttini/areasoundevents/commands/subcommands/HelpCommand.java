package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.CommandManager;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends SubCommand {
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();
    private final AreaSoundEvents areaSoundEvents = AreaSoundEvents.getInstance();

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_help_description");
    }

    @Override
    public String getSyntax() {
        return "help";
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandHelp();
        return (!permission.isEmpty()) ? permission : "areasoundevents.help";
    }

    @Override
    public List<String> getContext(String[] args) {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length <= 1) {
            CommandManager commandManager = new CommandManager(areaSoundEvents);
            PlayerMessage.to(player)
                    .append("")
                    .appendNewLine()
                    .append("====================  ", ChatColor.AQUA).append("COMMANDS", ChatColor.YELLOW, ChatColor.BOLD).append("  =====================", ChatColor.AQUA)
                    .appendNewLine()
                    .append("================== ", ChatColor.AQUA).append("AreaSoundEvents", ChatColor.AQUA).append(" ===================", ChatColor.AQUA)
                    .send();
            PlayerMessage.to(player).append("").appendNewLine().append("/areasoundevents ", ChatColor.GREEN).append("<subcommand>", ChatColor.YELLOW).send();
            PlayerMessage.to(player).append("").appendNewLine().send();
            for (String subCommandName : commandManager.getSubcommandsMap().keySet()) {
                SubCommand subCommand = commandManager.getSubCommandByName(subCommandName);
                if (subCommand != null) {
                    PlayerMessage.to(player)
                            .append(subCommand.getSyntax(), ChatColor.YELLOW).append(" - ", ChatColor.RESET, ChatColor.GRAY).append(subCommand.getDescription(), ChatColor.RESET, ChatColor.ITALIC).send();
                }
            }
            PlayerMessage.to(player)
                    .append("")
                    .appendNewLine()
                    .append("=====================================================", ChatColor.AQUA)
                    .append("")
                    .appendNewLine()
                    .send();
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
