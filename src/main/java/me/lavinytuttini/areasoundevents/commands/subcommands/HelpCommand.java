package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.CommandManager;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

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
    public void perform(Player player, String[] args) {
        if (args.length <= 1) {
            CommandManager commandManager = new CommandManager();
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "====================  " + ChatColor.YELLOW + ChatColor.BOLD + "COMMANDS" + ChatColor.AQUA + "  =====================");
            player.sendMessage(ChatColor.AQUA + "================== " + ChatColor.AQUA + "AreaSoundEvents" + ChatColor.AQUA + " ===================");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "/areasoundevents " + ChatColor.YELLOW + "<subcommand>");
            for (int i = 0; i < commandManager.getSubCommands().size(); i++) {
                player.sendMessage(ChatColor.YELLOW + commandManager.getSubCommands().get(i).getSyntax() + ChatColor.RESET + ChatColor.GRAY + " - " + ChatColor.RESET + ChatColor.ITALIC + commandManager.getSubCommands().get(i).getDescription());
            }
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "=====================================================");
            player.sendMessage("");
        } else {
            player.sendMessage(ChatColor.RED + localization.getString("commands_common_arguments_not_needed"));
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }
}
