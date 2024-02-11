package me.lavinytuttini.areasoundevents.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {
    private String lastCommand;

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        lastCommand = event.getMessage();
    }

    public String getLastCommandLine() {
        return lastCommand;
    }
}
