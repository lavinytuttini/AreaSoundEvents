package me.lavinytuttini.areasoundevents.managers;

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
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final Map<Player, PlayerData> entered = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> runningTasks = new ConcurrentHashMap<>();
    private final ConfigSettings configSettings = ConfigSettings.getInstance();
    private final LocalizationManager localization = LocalizationManager.getInstance();
    private final AreaSoundEvents areaSoundEvents = AreaSoundEvents.getInstance();

    public void quitEvent(Player player) {
        entered.remove(player);
    }

    public void joinEvent(Player player) {
        Bukkit.getScheduler().runTaskLater(areaSoundEvents, () -> enterRegion(player), configSettings.getDefaultSettings().getDefaultDelayPlaySound());
    }

    public void moveEvent(Player player) {
        enterRegion(player);
    }

    public void teleportEvent(Player player) {
        Bukkit.getScheduler().runTaskLater(areaSoundEvents, () -> enterRegion(player), configSettings.getDefaultSettings().getDefaultDelayPlaySound());
    }

    public void respawnEvent(Player player) {
        Bukkit.getScheduler().runTaskLater(areaSoundEvents, () -> enterRegion(player), configSettings.getDefaultSettings().getDefaultDelayPlaySound());
    }

    private void enterRegion(Player player) {
        LocalPlayer localPlayer = AreaSoundEvents.getWorldGuardPlugin().wrapPlayer(player);
        ApplicableRegionSet applicableRegionSet = getRegionsAtPlayerLocation(localPlayer);
        boolean playerInsideRegion = false;

        RegionData newRegionData = null;
        for (ProtectedRegion region : applicableRegionSet) {
            if (isPlayerInsideRegion(player, region)) {
                playerInsideRegion = true;
                newRegionData = getRegionDataForPlayer(player, applicableRegionSet);
                break;
            }
        }

        if (playerInsideRegion) {
            if (entered.containsKey(player)) {
                PlayerData currentPlayerData = entered.get(player);
                if (!currentPlayerData.getRegion().equals(Objects.requireNonNull(newRegionData).getName())) {
                    cancelLoopingSoundTask(player);
                    stopSoundForPlayer(player, currentPlayerData);
                    entered.remove(player);
                }
            }

            if (!entered.containsKey(player)) {
                if (newRegionData != null) {
                    PlayerData playerData = new PlayerData(newRegionData.getSound(), newRegionData.getSource(), newRegionData.getName());
                    entered.put(player, playerData);

                    if (newRegionData.isLoop()) {
                        startLoopingSoundTask(player, newRegionData);
                    } else {
                        playSoundForPlayer(player, newRegionData);
                    }

                    if (configSettings.getMainSettings().isSilentMode()) {
                        PlayerMessage.to(player)
                                .appendLineFormatted(localization.getString("information_player_enters_region"), ChatColor.GREEN, newRegionData.getName())
                                .appendNewLine()
                                .appendFormatted(localization.getString("information_player_enters_region_sound"), ChatColor.GREEN, newRegionData.getSound())
                                .send();
                    }
                }
            }
        } else {
            if (entered.containsKey(player)) {
                if (configSettings.getMainSettings().isSilentMode())
                    PlayerMessage.to(player).appendLineFormatted(localization.getString("information_player_leaves_region"), ChatColor.RED, entered.get(player).getRegion()).send();

                cancelLoopingSoundTask(player);
                stopSoundForPlayer(player, entered.get(player));
                entered.remove(player);
            }
        }
    }

    private boolean isPlayerInsideRegion(Player player, ProtectedRegion region) {
        Location playerLocation = player.getLocation();

        int x = playerLocation.getBlockX();
        int y = playerLocation.getBlockY();
        int z = playerLocation.getBlockZ();

        return region.contains(x, y, z);
    }

    private ApplicableRegionSet getRegionsAtPlayerLocation(LocalPlayer localPlayer) {
        Location playerLocation = BukkitAdapter.adapt(localPlayer.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(playerLocation));
    }

    private RegionData getRegionDataForPlayer(Player player, ApplicableRegionSet applicableRegionSet) {
        for (ProtectedRegion region : applicableRegionSet) {
            if (isPlayerInsideRegion(player, region)) {
                StateFlag.State state = region.getFlag(AreaSoundEvents.getAreaSoundEventsFlag());
                if (state == StateFlag.State.ALLOW) {
                    RegionData regionData = RegionsSettings.getInstance(AreaSoundEvents.getInstance()).regionDataMap(region.getId());
                    if (regionData != null) {
                        return regionData;
                    }
                }
            }
        }
        return null;
    }

    private void playSoundForPlayer(Player player, RegionData regionData) {
        BukkitTask soundTask = runningTasks.get(player.getUniqueId());
        if (regionData.isLoop()) {
            if (soundTask == null || soundTask.isCancelled()) {
                soundTask = createLoopingSoundTask(player, regionData);
                runningTasks.put(player.getUniqueId(), soundTask);
            }
        } else {
            player.stopAllSounds();
            player.playSound(player.getLocation(), regionData.getSound(), regionData.getSource(), regionData.getVolume(), regionData.getPitch());
        }
    }

    private void stopSoundForPlayer(Player player, PlayerData playerData) {
        if (playerData != null) {
            player.stopSound(playerData.getSound(), playerData.getSource());
        }
    }

    private void cancelLoopingSoundTask(Player player) {
        BukkitTask soundTask = runningTasks.remove(player.getUniqueId());
        if (soundTask != null) {
            soundTask.cancel();
        }
    }

    private void startLoopingSoundTask(Player player, RegionData regionData) {
        BukkitTask soundTask = runningTasks.get(player.getUniqueId());
        if (soundTask == null || soundTask.isCancelled()) {
            soundTask = createLoopingSoundTask(player, regionData);
            runningTasks.put(player.getUniqueId(), soundTask);
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
