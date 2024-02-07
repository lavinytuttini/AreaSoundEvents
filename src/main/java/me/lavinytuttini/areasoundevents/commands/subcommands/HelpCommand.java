package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.managers.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show all of the commands for AreaSoundEvents";
    }

    @Override
    public String getSyntax() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "areasoundevents.help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length <= 1) {
            CommandManager commandManager = new CommandManager();
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + "=================== " + ChatColor.YELLOW + "Commands" + ChatColor.DARK_RED + "  ===================");
            player.sendMessage(ChatColor.DARK_RED + "================ " + ChatColor.RED + ChatColor.ITALIC + "Area" + ChatColor.YELLOW + "Sound" + ChatColor.RED + "Events" + ChatColor.DARK_RED + " ================");
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "/areasoundevents " + ChatColor.YELLOW + "<subcommand>");
            for (int i = 0; i < commandManager.getSubCommands().size(); i++) {
                player.sendMessage(ChatColor.YELLOW + commandManager.getSubCommands().get(i).getSyntax() + ChatColor.RESET + ChatColor.GRAY + " - " + ChatColor.RESET + ChatColor.ITALIC + commandManager.getSubCommands().get(i).getDescription());
            }
            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + "=====================================================");
            player.sendMessage("");
        } else {
            player.sendMessage(ChatColor.RED + "Sorry, you do not need any other argument");
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }
}
