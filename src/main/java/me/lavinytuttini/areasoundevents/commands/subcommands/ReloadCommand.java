package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload plugin configuration";
    }

    @Override
    public String getSyntax() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "areasoundevents.reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            RegionsSettings.getInstance().reload(player);
        } else {
            player.sendMessage(ChatColor.RED + "Sorry, you do not need any other argument");
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }
}
