package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.CommandManager;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.utils.Prefix;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends SubCommand {
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();
    private final AreaSoundEvents areaSoundEvents = AreaSoundEvents.getInstance();
    private final String prefixPlayerMessage = Prefix.getPrefixPlayerMessage();

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
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "====================  " + ChatColor.YELLOW + ChatColor.BOLD + "COMMANDS" + ChatColor.AQUA + "  =====================");
            player.sendMessage(ChatColor.AQUA + "================== " + ChatColor.AQUA + "AreaSoundEvents" + ChatColor.AQUA + " ===================");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "/areasoundevents " + ChatColor.YELLOW + "<subcommand>");
            for (String subCommandName : commandManager.getSubcommandsMap().keySet()) {
                SubCommand subCommand = commandManager.getSubCommandByName(subCommandName);
                if (subCommand != null) {
                    player.sendMessage(ChatColor.YELLOW + subCommand.getSyntax() + ChatColor.RESET + ChatColor.GRAY + " - " + ChatColor.RESET + ChatColor.ITALIC + subCommand.getDescription());
                }
            }
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "=====================================================");
            player.sendMessage("");
        } else {
            player.sendMessage(prefixPlayerMessage + ChatColor.RED + localization.getString("commands_common_arguments_not_needed"));
            player.sendMessage(prefixPlayerMessage + ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }
}
