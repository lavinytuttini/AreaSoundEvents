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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final Map<Player, PlayerData> entered = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> runningTasks = new ConcurrentHashMap<>();
    private final ConfigSettings configSettings = ConfigSettings.getInstance();
    private final LocalizationManager localization = LocalizationManager.getInstance();

    public void quitEvent(Player player) {
        entered.remove(player);
    }

    public void joinEvent(Player player) {
        enterRegion(player);
    }

    public void moveEvent(Player player) {
        enterRegion(player);
    }

    private void enterRegion(Player player) {
        LocalPlayer localPlayer = AreaSoundEvents.getWorldGuardPlugin().wrapPlayer(player);
        ApplicableRegionSet applicableRegionSet = getRegionsAtPlayerLocation(localPlayer);
        boolean playerInsideRegion = false;

        for (ProtectedRegion region : applicableRegionSet) {
            if (isPlayerInsideRegion(player, region)) {
                playerInsideRegion = true;
                break;
            }
        }

        if (playerInsideRegion) {
            if (!entered.containsKey(player)) {
                RegionData regionData = getRegionDataForPlayer(player, applicableRegionSet);
                if (regionData != null) {

                    PlayerData playerData = new PlayerData(regionData.getSound(), regionData.getSource(), regionData.getName());
                    entered.put(player, playerData);

                    if (regionData.isLoop()) {
                        startLoopingSoundTask(player, regionData);
                    } else {
                        playSoundForPlayer(player, regionData);
                    }

                    if (configSettings.getMainSettings().isSilentMode()) {
                        PlayerMessage.to(player)
                                .appendLineFormatted(localization.getString("information_player_enters_region"), ChatColor.GREEN, regionData.getName())
                                .appendNewLine()
                                .appendFormatted(localization.getString("information_player_enters_region_sound"), ChatColor.GREEN, regionData.getSound())
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
