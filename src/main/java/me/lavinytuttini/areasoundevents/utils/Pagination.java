package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.data.PaginationData;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Pagination {
    private static Pagination instance;
    private final LocalizationManager localization;
    private final ConfigSettings configSettings;
    private final Map<Player, PaginationData> paginationDataMap;
    private final List<BaseComponent[]> messages;

    private Pagination() {
        localization = LocalizationManager.getInstance();
        configSettings = ConfigSettings.getInstance();
        paginationDataMap = new HashMap<>();
        messages = new ArrayList<>();
    }

    public PaginationData getPaginationDataMap(Player player) {
        return paginationDataMap.get(player);
    }

    public void setMessages(List<BaseComponent[]> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
    }

    public void init(Player player, List<BaseComponent[]> messages) {
        paginationDataMap.computeIfAbsent(player, k -> new PaginationData()).setCurrentPage(1);
        setMessages(messages);
        sendPaginatedMessage(player, 1);
    }

    public void sendPaginatedMessage(Player player, int page) {
        int pageSize = configSettings.getDefaultSettings().getDefaultListPageSize();

        int totalPages = (int) Math.ceil((double) messages.size() / pageSize);

        if (page < 1 || page > totalPages) {
            player.sendMessage(ChatColor.RED + localization.getString("pagination_invalid_page_number"));
            return;
        }

        player.sendMessage(ChatColor.BOLD + localization.getString("pagination_page") + " " + page + "/" + totalPages);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, messages.size());

        messages.subList(startIndex, endIndex).forEach(player.spigot()::sendMessage);

        sendPaginationButtons(player, page, totalPages);
    }

    private void sendPaginationButtons(Player player, int currentPage, int totalPages) {
        ComponentBuilder componentBuilder = new ComponentBuilder();

        if (currentPage > 1) {
            componentBuilder.append("[<<<<]").color(net.md_5.bungee.api.ChatColor.AQUA).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/areasoundeventsprevpage"));
            componentBuilder.append(" ").reset();
        }

        if (currentPage < totalPages) {
            componentBuilder.append("[>>>>]").color(net.md_5.bungee.api.ChatColor.AQUA).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/areasoundeventsnextpage"));
        }

        player.spigot().sendMessage(componentBuilder.create());
    }

    public static Pagination getInstance() {
        if (instance == null) {
            instance = new Pagination();
        }

        return instance;
    }
}
