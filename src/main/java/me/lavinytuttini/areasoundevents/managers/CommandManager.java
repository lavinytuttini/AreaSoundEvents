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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final LocalizationManager localization = LocalizationManager.getInstance();
    private final Map<String, SubCommand> subcommandsMap = new HashMap<>();

    public CommandManager(AreaSoundEvents areaSoundEvents) {
        subcommandsMap.put("help", new HelpCommand());
        subcommandsMap.put("create", new CreateCommand());
        subcommandsMap.put("save", new SaveCommand());
        subcommandsMap.put("remove", new RemoveCommand());
        subcommandsMap.put("reload", new ReloadCommand());
        subcommandsMap.put("list", new ListCommand());
        subcommandsMap.put("modify", new ModifyCommand());
        Objects.requireNonNull(areaSoundEvents.getCommand("areasoundevents")).setTabCompleter(this);
    }

    public SubCommand getSubCommandByName(String name) {
        return subcommandsMap.get(name.toLowerCase());
    }

    public Map<String, SubCommand> getSubcommandsMap() {
        return subcommandsMap;
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
                for (SubCommand subCommand : getSubcommandsMap().values()) {
                    if (args[0].equalsIgnoreCase(subCommand.getName())) {
                        String subCommandPermission = subCommand.getPermission();
                        if (player.hasPermission(subCommandPermission) || player.isOp()) {
                            subCommand.perform(player, args);
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(filterSuggestions(subcommandsMap.keySet(), args[args.length - 1]));
        } else if (args.length >= 2) {
            String subCommandName = args[0];
            SubCommand subCommand = getSubCommandByName(subCommandName);

            if (subCommand != null) {
                List<String> context = subCommand.getContext(args);
                if (context != null) {
                    completions.addAll(filterSuggestions(context, args[args.length - 1]));
                }
            }
        }

        return completions;
    }

    private List<String> filterSuggestions(Collection<String> suggestions, String prefix) {
        return suggestions.stream()
                .filter(option -> option.startsWith(prefix))
                .collect(Collectors.toList());
    }
}
