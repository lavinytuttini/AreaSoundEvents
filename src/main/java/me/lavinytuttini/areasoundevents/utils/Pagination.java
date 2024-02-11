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
    private final LocalizationManager localization = LocalizationManager.getInstance();
    public final Map<Player, PaginationData> paginationDataMap = new HashMap<>();
    public List<BaseComponent[]> messages = new ArrayList<>();
    public int totalPages = 0;
    public int pageSize;

    public PaginationData getPaginationDataMap(Player player) {
        return paginationDataMap.get(player);
    }

    public void setMessages(List<BaseComponent[]> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
    }

    public void init(Player player, List<BaseComponent[]> messages) {
        totalPages = 0;
        PaginationData paginationData = getPaginationDataMap(player);
        if (paginationData != null) paginationData.setCurrentPage(1);
        paginationDataMap.putIfAbsent(player, new PaginationData());
        setMessages(messages);
        sendPaginatedMessage(player, 1);
    }

    public void sendPaginatedMessage(Player player, int page) {
        ConfigSettings configSettings = ConfigSettings.getInstance();
        this.pageSize = configSettings.getDefaultSettings().getDefaultListPageSize();

        totalPages = (int) Math.ceil((double) messages.size() / pageSize);

        if (page < 1 || page > totalPages) {
            player.sendMessage(ChatColor.RED + localization.getString("pagination_invalid_page_number"));
            return;
        }

        player.sendMessage(ChatColor.BOLD + localization.getString("pagination_page") + " " + page + "/" + totalPages);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, messages.size());

        for (int i = startIndex; i < endIndex; i++) {
            player.spigot().sendMessage(messages.get(i));
        }

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