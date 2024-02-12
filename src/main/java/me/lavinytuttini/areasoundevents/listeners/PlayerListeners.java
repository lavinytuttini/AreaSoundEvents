package me.lavinytuttini.areasoundevents.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.data.PlayerData;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class PlayerListeners implements Listener {
    private final ArrayList<Player> left = new ArrayList<>();
    private final Map<Player, PlayerData> entered = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> runningTasks = new ConcurrentHashMap<>();
    private final ConfigSettings configSettings = ConfigSettings.getInstance();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (entered.containsKey(player) || left.contains(player)) {
            entered.remove(player);
            left.remove(player);
        }
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            enterRegion(player);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error handling region entry for player " + player.getName(), e);
        }
    }

    @EventHandler
    public void moveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        try {
            enterRegion(player);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error handling region entry for player " + player.getName(), e);
        }
    }

    public void enterRegion(Player player) {
        LocalPlayer localPlayer = AreaSoundEvents.getWorldGuardPlugin().wrapPlayer(player);
        Location playerLocation = BukkitAdapter.adapt(localPlayer.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(BukkitAdapter.adapt(playerLocation));

        if (!entered.containsKey(player)) {
            for (ProtectedRegion regions : applicableRegionSet) {
                if (regions.contains(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ())) {
                    try {
                        String regionId = regions.getId();
                        StateFlag.State state = regions.getFlag(AreaSoundEvents.getAreaSoundEventsFlag());

                        if (state == StateFlag.State.ALLOW) {
                            RegionData regionData = RegionsSettings.getInstance().regionDataMap(regionId);

                            if (regionData != null) {
                                PlayerData playerData = new PlayerData(regionData.getSound(), regionData.getSource(), regionData.getName());
                                left.remove(player);
                                entered.put(player, playerData);

                                if (regionData.isLoop()) {
                                    BukkitTask soundTask = runningTasks.get(player.getUniqueId());
                                    if (soundTask == null || soundTask.isCancelled()) {
                                        soundTask = createLoopingSoundTask(player, regionData);
                                        runningTasks.put(player.getUniqueId(), soundTask);
                                    }
                                } else {
                                    player.stopAllSounds(); // TODO: Only allowed from v1.17.1
                                    // player.stopSound(regionData.getSource()); TODO: Send SoundCategory is only allowed in stopSound method from v1.19.1
                                    // TODO: Create looping sound: Use considerable tasks as playing a sound repeatedly can consume server resources -> this.runTaskTimer(areaSoundEvents.getInstance(), 0L, 20L);
                                    player.playSound(player.getLocation(), regionData.getSound(), regionData.getSource(), regionData.getVolume(), regionData.getPitch());
                                }

                                if (!configSettings.getMainSettings().isSilentMode()) {
                                    player.sendMessage(ChatColor.GREEN + localization.getString("information_player_enters_region", regionData.getName()));
                                    player.sendMessage(ChatColor.GREEN + localization.getString("information_player_enters_region_sound", regionData.getSound()));
                                }
                            }
                        }
                    } catch (Exception e) {
                        getLogger().severe("Error entering region: " + e.getMessage());
                    }
                }
            }
        }


        if (!left.contains(player) && entered.get(player) != null && applicableRegionSet.size() == 0) {
            if (applicableRegionSet.size() == 0) {
                player.stopSound(entered.get(player).getSound(), entered.get(player).getSource());
                player.sendMessage(ChatColor.RED + localization.getString("information_player_leaves_region", entered.get(player).getRegion()));

                BukkitTask task = runningTasks.remove(player.getUniqueId());
                if (task != null) {
                    task.cancel();
                }

                entered.remove(player);
                left.add(player);
            }
        }
    }

    private BukkitTask createLoopingSoundTask(Player player, RegionData regionData) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                player.stopAllSounds();
                player.playSound(player.getLocation(), regionData.getSound(), regionData.getSource(), regionData.getVolume(), regionData.getPitch());
            }
        }.runTaskTimerAsynchronously(AreaSoundEvents.getInstance(), 0, regionData.getLoopTime() * 20L);
    }
}
