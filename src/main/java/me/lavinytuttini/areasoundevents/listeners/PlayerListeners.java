package me.lavinytuttini.areasoundevents.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.listener.PlayerMoveListener;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.data.PlayerData;
import me.lavinytuttini.areasoundevents.data.RegionData;
import me.lavinytuttini.areasoundevents.data.config.DefaultSettings;
import me.lavinytuttini.areasoundevents.data.config.MainSettings;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class PlayerListeners implements Listener {
    AreaSoundEvents areaSoundEvents = AreaSoundEvents.getInstance();
    private final ArrayList<Player> left = new ArrayList<>();
    private final Map<Player, PlayerData> entered = new HashMap<>();
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
        enterRegion(player);
    }

    @EventHandler
    public void moveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        enterRegion(player);
    }

    public void enterRegion(Player player) {
        LocalPlayer localPlayer = areaSoundEvents.worldGuardPlugin.wrapPlayer(player);
        Location playerLocation = BukkitAdapter.adapt(localPlayer.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(BukkitAdapter.adapt(playerLocation));

        for (ProtectedRegion regions : applicableRegionSet) {
            if (regions.contains(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ())) {
                if (!entered.containsKey(player)) {
                    try {
                        String regionId = regions.getId();
                        StateFlag.State state = regions.getFlag(AreaSoundEvents.AREA_SOUND_EVENTS_FLAG);

                        if (state == StateFlag.State.ALLOW) {
                            RegionData regionData = RegionsSettings.getInstance().regionDataMap(regionId);

                            if (regionData != null) {
                                PlayerData playerData = new PlayerData(regionData.getSound(), regionData.getSource(), regionData.getName());
                                left.remove(player);
                                entered.put(player, playerData);

                                player.stopAllSounds(); // TODO: Only allowed from v1.17.1
                                // player.stopSound(regionData.getSource()); TODO: Send SoundCategory is only allowed in stopSound method from v1.19.1
                                // TODO: Create looping sound: Use considerable tasks as playing a sound repeatedly can consume server resources -> this.runTaskTimer(areaSoundEvents.getInstance(), 0L, 20L);
                                player.playSound(player.getLocation(), regionData.getSound(), regionData.getSource(), regionData.getVolume(), regionData.getPitch());

                                if (!configSettings.getMainSettings().isSilentMode()) {
                                    player.sendMessage(ChatColor.GREEN + localization.getString("information_player_enters_region", regionData.getName()));
                                    player.sendMessage(ChatColor.GREEN + localization.getString("information_player_enters_region_sound", regionData.getSound()));
                                }
                            }
                        }
                    } catch (Exception e) {
                        getLogger().severe(e.getMessage());
                    }
                }
            }
        }

        if (!left.contains(player) && entered.get(player) != null) {
            if (applicableRegionSet.size() == 0) {
                player.stopSound(entered.get(player).getSound(), entered.get(player).getSource());
                player.sendMessage(ChatColor.RED + localization.getString("information_player_leaves_region", entered.get(player).getRegion()));
                entered.remove(player);
                left.add(player);
            }
        }
    }
}
