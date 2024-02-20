package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSettings;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance(AreaSoundEvents.getInstance());
    private final DefaultSettings defaultSettings = ConfigSettings.getInstance().getDefaultSettings();
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() { return "create"; }

    @Override
    public String getDescription() { return localization.getString("commands_create_description"); }

    @Override
    public String getSyntax() {
        return ChatColor.translateAlternateColorCodes('&', "create &a<region-name> <sound-name> &9[source] [volume] [pitch] [minVolume]");
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandCreate();
        return !permission.isEmpty() ? permission : "areasoundevents.create";
    }

    @Override
    public List<String> getContext(String[] args) {
        List<String> suggestions = new ArrayList<>();

        switch (args.length) {
            case 2:
                suggestions.add("<region-name>");
                break;
            case 3:
                for (Sound sound : Sound.values()) {
                    suggestions.add(String.valueOf(sound.getKey()));
                }
                break;
            case 4:
                for (SoundCategory category : SoundCategory.values()) {
                    suggestions.add(category.name().toLowerCase());
                }
                break;
            case 5:
                suggestions.add("[volume (0.0~1.0)]>");
                break;
            case 6:
                suggestions.add("[pitch (0.0~1.0)]>");
                break;
            case 7:
                suggestions.add("[loop (true/false)]>");
                break;
            case 8:
                suggestions.add("[loop-time (seconds)]>");
                break;
        }

        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args == null || args.length < 3) {
            PlayerMessage.to(player)
                    .appendLine(localization.getString("commands_common_missed_arguments"), ChatColor.RED)
                    .appendNewLine()
                    .append("/areasoundevents ", ChatColor.YELLOW)
                    .append(this.getSyntax())
                    .send();
            return;
        }

        String regionName = args[1].toLowerCase();
        String soundName = args[2].toLowerCase();
        SoundCategory source = defaultSettings.getDefaultSoundCategory();
        float volume = defaultSettings.getDefaultSoundVolume();
        float pitch = defaultSettings.getDefaultSoundPitch();
        boolean loop = defaultSettings.isDefaultLoopSound();
        int loopTime = defaultSettings.getDefaultSoundLoopTime();

        if (args.length >= 4) {
            source = Utils.processSoundCategoryArgument(args[3], source);
        }
        if (args.length >= 5) {
            volume = Utils.parseFloatArgument(args[4], volume);
        }
        if (args.length >= 6) {
            pitch = Utils.parseFloatArgument(args[5], pitch);
        }
        if (args.length >= 7) {
            loop = Utils.parseBooleanArgument(args[6], loop);
        }
        if (args.length == 8) {
            loopTime = Utils.parseIntegerArgument(args[7], loopTime);
        }

        createRegion(player, regionName, soundName, source, volume, pitch, loop, loopTime);
    }

    public void createRegion(Player player, String regionName, String soundName, SoundCategory source, float volume, float pitch, boolean loop, int loopTime) {
        if (regionsSettings.getRegionDataMap().containsKey(regionName)) {
            PlayerMessage.to(player).appendLineFormatted(localization.getString("commands_create_region_exists"), ChatColor.RED, regionName).send();
        } else {
            RegionData regionData = new RegionData(regionName, soundName, source, volume, pitch, loop, loopTime);
            regionsSettings.addRegion(regionName, regionData);
            PlayerMessage.to(player).appendLine(localization.getString("commands_create_created_region"), ChatColor.GREEN).send();
        }
    }
}
