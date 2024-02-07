package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.data.PaginationData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pagination {
    private static Pagination instance;
    public final Map<Player, PaginationData> paginationDataMap = new HashMap<>();
    public List<String> messages = new ArrayList<>();
    public int totalPages = 0;

    public PaginationData getPaginationDataMap(Player player) {
        return paginationDataMap.get(player);
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void init(Player player, List<String> messages) {
        totalPages = 0;
        PaginationData paginationData = getPaginationDataMap(player);
        if (paginationData != null) paginationData.setCurrentPage(1);
        paginationDataMap.putIfAbsent(player, new PaginationData());
        setMessages(messages);
        sendPaginatedMessage(player, 1, 2);
    }

    public void sendPaginatedMessage(Player player, int page, int pageSize) {
        totalPages = (int) Math.ceil((double) messages.size() / pageSize);

        if (page < 1 || page > totalPages) {
            player.sendMessage(ChatColor.RED + "Invalid page number.");
            return;
        }

        player.sendMessage(ChatColor.BOLD + "Page " + page + "/" + totalPages);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, messages.size());

        for (int i = startIndex; i < endIndex; i++) {
            player.sendMessage(messages.get(i));
        }

        sendPaginationButtons(player, page, totalPages);
    }

    private void sendPaginationButtons(Player player, int currentPage, int totalPages) {
        ComponentBuilder componentBuilder = new ComponentBuilder();

        if (currentPage > 1) {
            componentBuilder.append("[").color(net.md_5.bungee.api.ChatColor.GRAY);
            componentBuilder.append("<<<<]").color(net.md_5.bungee.api.ChatColor.GREEN)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/areasoundeventsprevpage"));
            componentBuilder.append(" ").reset();
        }

        if (currentPage < totalPages) {
            componentBuilder.append("[").color(net.md_5.bungee.api.ChatColor.GRAY);
            componentBuilder.append(">>>>]").color(net.md_5.bungee.api.ChatColor.GREEN)
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
