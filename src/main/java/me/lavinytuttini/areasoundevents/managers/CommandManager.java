package me.lavinytuttini.areasoundevents.managers;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;

import me.lavinytuttini.areasoundevents.commands.subcommands.*;
import me.lavinytuttini.areasoundevents.listeners.ChatListener;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    public CommandManager() {
        subcommands.add(new HelpCommand());
        subcommands.add(new CreateCommand());
        subcommands.add(new SaveCommand());
        subcommands.add(new RemoveCommand());
        subcommands.add(new ReloadCommand());
        subcommands.add(new ListCommand());
        subcommands.add(new ModifyCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            AreaSoundEvents areaSoundEvents = AreaSoundEvents.getInstance();
            ChatListener chatListener = areaSoundEvents.getChatListener();

            String lastCommandLine = chatListener.getLastCommandLine();
            ConfigSettings configSettings = ConfigSettings.getInstance();
            if (!configSettings.getMainSettings().isSilentMode()) {
                player.sendMessage("");
                player.sendMessage(lastCommandLine);
            }

            if (args.length == 0) {
                HelpCommand help = new HelpCommand();
                help.perform(player, args);
            } else {
                for (int i = 0; i < this.getSubCommands().size(); i++) {
                    if (args[0].equalsIgnoreCase((this.getSubCommands().get(i).getName()))) {
                        String subCommandPermission = this.getSubCommands().get(i).getPermission();
                        if (player.hasPermission(subCommandPermission) || player.isOp()) {
                            this.getSubCommands().get(i).perform(player, args);
                        } else {
                            player.sendMessage(ChatColor.RED + localization.getString("commands_command_not_allowed"));
                        }

                        return true;
                    }
                }

                player.sendMessage(ChatColor.RED + localization.getString("commands_command_invalid"));
                player.sendMessage(ChatColor.GREEN + "/areasoundevents help");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + localization.getString("commands_only_player_can_use"));
        }

        return true;
    }

    public ArrayList<SubCommand> getSubCommands() { return subcommands; }
}
