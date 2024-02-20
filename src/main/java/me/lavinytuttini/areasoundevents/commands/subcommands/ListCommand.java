package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Pagination;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance(AreaSoundEvents.getInstance());
    private final DefaultSubcommandPermissions defaultSubcommandPermissions = ConfigSettings.getInstance().getDefaultSubcommandPermissions();
    private static final LocalizationManager localization = LocalizationManager.getInstance();

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return localization.getString("commands_list_description");
    }

    @Override
    public String getSyntax() {
        return "list";
    }

    @Override
    public List<String> getContext(String[] args) {
        return null;
    }

    @Override
    public String getPermission() {
        String permission = defaultSubcommandPermissions.getSubcommandHelp();
        return !permission.isEmpty() ? permission : "areasoundevents.list";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            Map<String, RegionData> regionDataMap = regionsSettings.getRegionDataMap();

            if (regionDataMap.isEmpty()) {
                PlayerMessage.to(player).appendLine(localization.getString("commands_list_no_entries_found"), ChatColor.YELLOW).send();
            } else {
                List<BaseComponent[]> messageList = new ArrayList<>();
                regionDataMap.forEach((regionName, regionData) -> messageList.add(getMessage(regionName, regionData)));
                Pagination.getInstance().init(player, messageList);
            }
        } else {
            PlayerMessage.to(player)
                    .appendLine(localization.getString("commands_common_arguments_not_needed"), ChatColor.RED)
                    .appendNewLine()
                    .append("/areasoundevents ", ChatColor.YELLOW).append(this.getSyntax())
                    .send();
        }
    }

    private static BaseComponent[] getMessage(String regionName, RegionData regionData) {
        ComponentBuilder messageBuilder = new ComponentBuilder();

        String soundName = regionData.getSound();
        SoundCategory source = regionData.getSource();
        float volume = regionData.getVolume();
        float pitch = regionData.getPitch();
        boolean loop = regionData.isLoop();
        int loopTime = regionData.getLoopTime();



        ComponentBuilder modifyButtonBuilder = createButton(
                localization.getString("commands_list_modify_button"),
                ChatColor.GREEN,
                ClickEvent.Action.SUGGEST_COMMAND,
                "/areasoundevents modify " + regionName,
                localization.getString("commands_list_tooltip_modify_button") + ChatColor.GOLD + regionName
        );

        ComponentBuilder removeButtonBuilder = createButton(
                localization.getString("commands_list_remove_button"),
                ChatColor.RED,
                ClickEvent.Action.RUN_COMMAND,
                "/areasoundevents remove " + regionName,
                localization.getString("commands_list_tooltip_remove_button") + ChatColor.GOLD + regionName
        );

        ComponentBuilder regionButtonBuilder = createButton(
                "Region:",
                "/areasoundevents modify " + regionName + " name="
        );

        ComponentBuilder soundButtonBuilder = createButton(
                "Sound:",
                "/areasoundevents modify " + regionName + " sound="
        );

        ComponentBuilder sourceButtonBuilder = createButton(
                "Source:",
                "/areasoundevents modify " + regionName + " source="
        );

        ComponentBuilder volumeButtonBuilder = createButton(
                "Volume:",
                "/areasoundevents modify " + regionName + " volume="
        );

        ComponentBuilder pitchButtonBuilder = createButton(
                "Pitch:",
                "/areasoundevents modify " + regionName + " pitch="
        );

        ComponentBuilder loopButtonBuilder = createButton(
                "Loop:",
                "/areasoundevents modify " + regionName + " loop="
        );

        ComponentBuilder loopTimeButtonBuilder = createButton(
                "LoopTime:",
                "/areasoundevents modify " + regionName + " loopTime="
        );

        messageBuilder.append(regionButtonBuilder.create())
                .color(ChatColor.GREEN.asBungee())
                .append("").reset()
                .append(ChatColor.GOLD + " " + regionName)
                .append(modifyButtonBuilder.create())
                .append(removeButtonBuilder.create())
                .append("\n").reset();
        messageBuilder.append(ChatColor.GREEN + " | ").append(soundButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + soundName + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(sourceButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + source + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(volumeButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + volume + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(pitchButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + pitch + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(loopButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + loop + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(loopTimeButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + loopTime);

        return messageBuilder.create();
    }

    private static ComponentBuilder createButton(String buttonText, ChatColor chatColor, ClickEvent.Action clickEventAction, String command, String hoverText) {
        return new ComponentBuilder(" [" + buttonText + "]")
                .color(chatColor.asBungee())
                .event(new ClickEvent(clickEventAction, command))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
    }

    private static ComponentBuilder createButton(String buttonText, String command) {
        return new ComponentBuilder(buttonText)
                .color(ChatColor.GREEN.asBungee())
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
    }
}
