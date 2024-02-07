package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ModifyCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance();

    @Override
    public String getName() {
        return "modify";
    }

    @Override
    public String getDescription() {
        return "Modify a selected region";
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "modify &a<region-name> &9[name={region-name}] [sound={sound-name}] [source={source}] [volume={volume}] [pitch={pitch}]");
    }

    @Override
    public String getPermission() {
        return "areasoundevents.modify";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args == null || args.length <= 2) {
            player.sendMessage(ChatColor.RED + "Sorry, you miss some arguments");
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
            return;
        }

        RegionData regionData = RegionsSettings.getInstance().regionDataMap(args[1]);

        if (regionData != null) {
            List<String> invalidArguments = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                switch (args[i].split("=")[0]) {
                    case "name":
                        String name = args[i].substring(args[i].indexOf("=") + 1);
                        regionData.setName(name.toLowerCase());
                        break;
                    case "sound":
                        String sound = args[i].substring(args[i].indexOf("=") + 1);
                        regionData.setSound(sound.toLowerCase());
                        break;
                    case "source":
                        SoundCategory source = Utils.processSoundCategoryArgument(args[i].substring(args[i].indexOf("=") + 1));
                        regionData.setSource(source);
                        break;
                    case "volume":
                        float volume = Utils.parseFloatArgument(args[i].substring(args[i].indexOf("=") + 1));
                        regionData.setVolume(volume);
                        break;
                    case "pitch":
                        float pitch = Utils.parseFloatArgument(args[i].substring(args[i].indexOf("=") + 1));
                        regionData.setPitch(pitch);
                        break;
                    default:
                        invalidArguments.add(args[i]);
                        break;
                }
            }

            if (!invalidArguments.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid arguments:");
                for (String invalidArgument : invalidArguments) {
                    player.sendMessage(ChatColor.YELLOW + " - " + invalidArgument);
                }
                player.sendMessage(this.getSyntax());
                return;
            }

            regionsSettings.modify(player, regionData, args[1]);
        } else {
            player.sendMessage(ChatColor.RED + "A region with '" + args[1] + "' name does not exists.");
        }
    }
}
