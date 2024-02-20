package me.lavinytuttini.areasoundevents.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerMessage {
    private final StringBuilder messageBuilder;
    private final String prefix;
    private final Player player;

    public PlayerMessage(Player player) {
        this.player = player;
        this.prefix = Prefix.getPrefixPlayerMessage();
        this.messageBuilder = new StringBuilder();
    }

    public PlayerMessage append(String text, ChatColor... chatColor) {
        for (ChatColor color : chatColor) {
            messageBuilder.append(color);
        }

        messageBuilder.append(text);
        return this;
    }

    public PlayerMessage append(String text) {
        messageBuilder.append(text);
        return this;
    }

    public PlayerMessage appendLine(String text, ChatColor chatColor) {
        messageBuilder.append(this.prefix).append(chatColor).append(text).append("\n");
        return this;
    }

    public PlayerMessage appendLine(String text) {
        messageBuilder.append(this.prefix).append(text).append("\n");
        return this;
    }

    public PlayerMessage appendFormatted(String format, ChatColor chatColor, Object... args) {
        messageBuilder.append(chatColor).append(String.format(format, args));
        return this;
    }

    public PlayerMessage appendFormatted(String format, Object... args) {
        messageBuilder.append(String.format(format, args));
        return this;
    }

    public PlayerMessage appendLineFormatted(String format, ChatColor chatColor, Object... args) {
        messageBuilder.append(this.prefix).append(chatColor).append(String.format(format, args)).append("\n");
        return this;
    }

    public PlayerMessage appendLineFormatted(String format, Object... args) {
        messageBuilder.append(this.prefix).append(String.format(format, args)).append("\n");
        return this;
    }

    public PlayerMessage appendNewLine() {
        messageBuilder.append("\n");
        return this;
    }

    public PlayerMessage appendLineSeparator() {
        messageBuilder.append(ChatColor.GRAY).append("-".repeat(40)).append("\n");
        return this;
    }

    public void send() {
        player.sendMessage(String.valueOf(messageBuilder));
    }

    public static PlayerMessage to(Player player) {
        return new PlayerMessage(player);
    }

    public PlayerMessage toAll(List<Player> players) {
        return new PlayerMessageGroup(players);
    }

    public PlayerMessage toAll(Player... players) {
        return new PlayerMessageGroup(Arrays.asList(players));
    }

    private class PlayerMessageGroup extends PlayerMessage {
        private final List<Player> players;

        private PlayerMessageGroup(List<Player> players) {
            super(null);
            this.players = new ArrayList<>(players);
        }

        @Override
        public void send() {
            for (Player player : players) {
                player.sendMessage(String.valueOf(messageBuilder));
            }
        }
    }
}
