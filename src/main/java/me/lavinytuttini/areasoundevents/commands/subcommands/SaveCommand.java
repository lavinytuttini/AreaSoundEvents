package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SaveCommand extends SubCommand {
    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Save the created region in 'regions.yml'";
    }

    @Override
    public String getSyntax() {
        return "save";
    }

    @Override
    public String getPermission() {
        return "areasoundevents.save";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            RegionsSettings.getInstance().save(player);
        } else {
            player.sendMessage(ChatColor.RED + "Sorry, you do not need any other argument");
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }
}
