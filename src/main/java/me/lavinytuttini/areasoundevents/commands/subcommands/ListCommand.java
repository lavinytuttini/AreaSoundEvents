package me.lavinytuttini.areasoundevents.commands.subcommands;

import me.lavinytuttini.areasoundevents.commands.SubCommand;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSubcommandPermissions;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.Pagination;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListCommand extends SubCommand {
    private final RegionsSettings regionsSettings = RegionsSettings.getInstance();
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
        return (!permission.isEmpty()) ? permission : "areasoundevents.list";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            Map<String, RegionData> regionDataMap = regionsSettings.getRegionDataMap();

            List<BaseComponent[]> messageList = new ArrayList<>();
            if (regionDataMap.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + localization.getString("commands_list_no_entries_found"));
            } else {
                for (Map.Entry<String, RegionData> entry : regionDataMap.entrySet()) {
                    BaseComponent[] messageComponents = getMessage(entry);
                    messageList.add(messageComponents);
                }
                Pagination.getInstance().init(player, messageList);
            }
        } else {
            player.sendMessage(ChatColor.RED + localization.getString("commands_common_arguments_not_needed"));
            player.sendMessage(ChatColor.YELLOW + "/areasoundsevents " + this.getSyntax());
        }
    }

    private static BaseComponent[] getMessage(Map.Entry<String, RegionData> entry) {
        String regionName = entry.getKey();
        RegionData regionData = entry.getValue();

        String soundName = regionData.getSound();
        SoundCategory source = regionData.getSource();
        float volume = regionData.getVolume();
        float pitch = regionData.getPitch();
        boolean loop = regionData.isLoop();
        int loopTime = regionData.getLoopTime();

        ComponentBuilder messageBuilder = new ComponentBuilder();

        ComponentBuilder modifyButtonBuilder = new ComponentBuilder(" [" + localization.getString("commands_list_modify_button") + "] ")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(localization.getString("commands_list_tooltip_modify_button") + " " + ChatColor.GOLD + regionName)));

        ComponentBuilder removeButtonBuilder = new ComponentBuilder("[" + localization.getString("commands_list_remove_button") + "] \n")
                .color(net.md_5.bungee.api.ChatColor.RED)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/areasoundevents remove " + regionName))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(localization.getString("commands_list_tooltip_remove_button") + " " + ChatColor.GOLD + regionName)));

        ComponentBuilder regionButtonBuilder = new ComponentBuilder("Region:")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName + " name="));

        ComponentBuilder soundButtonBuilder = new ComponentBuilder("Sound:")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName + " sound="));

        ComponentBuilder sourceButtonBuilder = new ComponentBuilder("Source:")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName + " source="));

        ComponentBuilder volumeButtonBuilder = new ComponentBuilder("Volume:")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName + " volume="));

        ComponentBuilder pitchButtonBuilder = new ComponentBuilder("Pitch:")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName + " pitch="));

        ComponentBuilder loopButtonBuilder = new ComponentBuilder("Loop:")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName + " loop="));

        ComponentBuilder loopTimeButtonBuilder = new ComponentBuilder("LoopTime:")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/areasoundevents modify " + regionName + " loopTime="));

        messageBuilder.append(regionButtonBuilder.create())
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .append("").reset()
                .append(ChatColor.GOLD + " " + regionName)
                .append(modifyButtonBuilder.create())
                .append(removeButtonBuilder.create())
                .append("").reset();
        messageBuilder.append(ChatColor.GREEN + " | ").append(soundButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + soundName + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(sourceButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + source + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(volumeButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + volume + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(pitchButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + pitch + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(loopButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + loop + "\n");
        messageBuilder.append(ChatColor.GREEN + " | ").append(loopTimeButtonBuilder.create()).append("").reset().append(ChatColor.WHITE + " " + loopTime);

        return messageBuilder.create();
    }
}
