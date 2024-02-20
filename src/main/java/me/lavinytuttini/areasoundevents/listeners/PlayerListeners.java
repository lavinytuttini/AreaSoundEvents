package me.lavinytuttini.areasoundevents.listeners;

import me.lavinytuttini.areasoundevents.managers.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {
    private final PlayerManager playerManager = new PlayerManager();

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        playerManager.quitEvent(event.getPlayer());
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        playerManager.joinEvent(event.getPlayer());
    }

    @EventHandler
    public void moveEvent(PlayerMoveEvent event) {
        playerManager.moveEvent(event.getPlayer());
    }
}
