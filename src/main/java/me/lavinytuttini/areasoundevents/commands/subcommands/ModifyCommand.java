package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
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
            suggestions.addAll(regionsSettings.getRegionDataMap().keySet());
        } else if (args.length >= 3 && args.length < 8) {
            for (String parameter : new String[]{"name", "sound", "source", "volume", "pitch", "loop", "loopTime"}) {
                if (containsParameter(args, parameter)) {
                    suggestions.add(parameter + "=");
                }
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
        return !permission.isEmpty() ? permission : "areasoundevents.modify";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args == null || args.length <= 2) {
            PlayerMessage.to(player)
                    .appendLine(localization.getString("commands_common_arguments_needed"), ChatColor.RED)
                    .appendNewLine()
                    .append("/areasoundevents ", ChatColor.YELLOW)
                    .append(this.getSyntax())
                    .send();
            return;
        }

        RegionData regionData = regionsSettings.regionDataMap(args[1]);

        if (regionData == null) {
            PlayerMessage.to(player).appendLineFormatted(localization.getString("region_settings_common_region_no_exists"), ChatColor.RED, args[1]).send();
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
                    regionData.setName(argParts[1].toLowerCase());
                    break;
                case "sound":
                    regionData.setSound(argParts[1].toLowerCase());
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
                case "pitch":
                    try {
                        float value = Float.parseFloat(argParts[1]);
                        if (value < 0 || value > 1) {
                            invalidArguments.add(args[i]);
                        } else if (argParts[0].equals("volume")) {
                            regionData.setVolume(value);
                        } else {
                            regionData.setPitch(value);
                        }
                    } catch (NumberFormatException e) {
                        invalidArguments.add(args[i]);
                    }
                    break;
                case "loop":
                    if (!argParts[1].equalsIgnoreCase("true") && !argParts[1].equalsIgnoreCase("false")) {
                        invalidArguments.add(args[i]);
                    } else {
                        regionData.setLoop(Boolean.parseBoolean(argParts[1]));
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
            PlayerMessage.to(player).appendLine(localization.getString("commands_common_invalid_arguments"), ChatColor.RED).send();
            invalidArguments.forEach(invalidArgument -> PlayerMessage.to(player).appendFormatted(" - ", ChatColor.RED).append(invalidArgument, ChatColor.RED).send());
            PlayerMessage.to(player).append("/areasoundevents ", ChatColor.YELLOW).append(this.getSyntax()).send();
            return;
        }

        regionsSettings.updateRegion(player, args[1], regionData);
    }
}
