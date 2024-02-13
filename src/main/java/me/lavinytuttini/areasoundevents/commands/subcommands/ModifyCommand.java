package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ModifyCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance(AreaSoundEvents.getInstance());
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() {
        return "modify";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_modify_description");
    }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "modify &a<region-name> &9[name={region-name}] [sound={sound-name}] [source={source}] [volume={volume}] [pitch={pitch}]");
    }

    @Override
    public List<String> getContext(String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2) {
            for (RegionData region : regionsSettings.getRegionDataMap().values()) {
                suggestions.add(region.getName());
            }
        } else if (args.length >= 3 && args.length < 8) {
            if (containsParameter(args, "name")) {
                suggestions.add("name=");
            }
            if (containsParameter(args, "sound")) {
                suggestions.add("sound=");
            }
            if (containsParameter(args, "source")) {
                suggestions.add("source=");
            }
            if (containsParameter(args, "volume")) {
                suggestions.add("volume=");
            }
            if (containsParameter(args, "pitch")) {
                suggestions.add("pitch=");
            }
            if (containsParameter(args, "loop")) {
                suggestions.add("loop=");
            }
            if (containsParameter(args, "loopTime")) {
                suggestions.add("loopTime=");
            }
        }

        return suggestions;
    }

    private boolean containsParameter(String[] args, String parameter) {
        for (String arg : args) {
            if (arg.startsWith(parameter + "=")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandHelp();
        return (!permission.isEmpty()) ? permission : "areasoundevents.modify";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args == null || args.length <= 2) {
            player.sendMessage(ChatColor.RED + localization.getString("commands_common_arguments_needed"));
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
            return;
        }

        RegionData regionData = RegionsSettings.getInstance(AreaSoundEvents.getInstance()).regionDataMap(args[1]);

        if (regionData == null) {
            player.sendMessage(ChatColor.RED + localization.getString("region_settings_common_region_no_exists", args[1]));
            return;
        }

        List<String> invalidArguments = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            String[] argParts = args[i].split("=");
            if (argParts.length != 2 || argParts[1].isEmpty()) {
                invalidArguments.add(args[i]);
                continue;
            }
            switch (argParts[0]) {
                case "name":
                    String name = argParts[1];
                    regionData.setName(name.toLowerCase());
                    break;
                case "sound":
                    String sound = argParts[1];
                    regionData.setSound(sound.toLowerCase());
                    break;
                case "source":
                    try {
                        SoundCategory source = Utils.processSoundCategoryArgument(argParts[1], null);
                        if (source == null) {
                            invalidArguments.add(args[i]);
                        } else {
                            regionData.setSource(source);
                        }
                    } catch (IllegalArgumentException e) {
                        invalidArguments.add(args[i]);
                    }
                    break;
                case "volume":
                    try {
                        float volume = Float.parseFloat(argParts[1]);
                        if (volume < 0 || volume > 1) {
                            invalidArguments.add(args[i]);
                        } else {
                            regionData.setVolume(volume);
                        }
                    } catch (NumberFormatException e) {
                        invalidArguments.add(args[i]);
                    }
                    break;
                case "pitch":
                    try {
                        float pitch = Float.parseFloat(argParts[1]);
                        if (pitch < 0 || pitch > 1) {
                            invalidArguments.add(args[i]);
                        } else {
                            regionData.setPitch(pitch);
                        }
                    } catch (NumberFormatException e) {
                        invalidArguments.add(args[i]);
                    }
                    break;
                case "loop":
                    if (argParts[1].equalsIgnoreCase("true")) {
                        regionData.setLoop(true);
                    } else if (argParts[1].equalsIgnoreCase("false")) {
                        regionData.setLoop(false);
                    } else {
                        invalidArguments.add(args[i]);
                    }
                    break;
                case "loopTime":
                    try {
                        int loopTime = Integer.parseInt(argParts[1]);
                        regionData.setLoopTime(loopTime);
                    } catch (NumberFormatException e) {
                        invalidArguments.add(args[i]);
                    }
                    break;
                default:
                    invalidArguments.add(args[i]);
                    break;
            }
        }

        if (!invalidArguments.isEmpty()) {
            player.sendMessage(ChatColor.RED + localization.getString("commands_common_invalid_arguments"));
            for (String invalidArgument : invalidArguments) {
                player.sendMessage(ChatColor.RED + " - " + invalidArgument);
            }
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
            return;
        }

        RegionsSettings.getInstance(AreaSoundEvents.getInstance()).updateRegion(player, args[1], regionData);
    }
}
