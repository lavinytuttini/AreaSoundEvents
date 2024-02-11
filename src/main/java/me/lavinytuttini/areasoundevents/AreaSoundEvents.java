package me.lavinytuttini.areasoundevents;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.lavinytuttini.areasoundevents.commands.NextPageCommand;
import me.lavinytuttini.areasoundevents.commands.PrevPageCommand;
import me.lavinytuttini.areasoundevents.listeners.ChatListener;
import me.lavinytuttini.areasoundevents.listeners.PlayerListeners;
import me.lavinytuttini.areasoundevents.managers.CommandManager;
import me.lavinytuttini.areasoundevents.managers.LocalizationManager;
import me.lavinytuttini.areasoundevents.managers.PluginManager;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.settings.RegionsSettings;
import me.lavinytuttini.areasoundevents.utils.ServerVersion;
import me.lavinytuttini.areasoundevents.utils.UpdateChecker;
import me.lavinytuttini.areasoundevents.managers.MessageManager;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class AreaSoundEvents extends JavaPlugin {
    private static AreaSoundEvents instance;
    private ChatListener chatListener;
    public static ServerVersion serverVersion;
    public static String prefix;
    public static StateFlag AREA_SOUND_EVENTS_FLAG;
    public WorldGuardPlugin worldGuardPlugin;
    public WorldEditPlugin worldEditPlugin;
    public final String pluginVersion;
    PluginDescriptionFile pdfFile = this.getDescription();

    public AreaSoundEvents() {
        this.pluginVersion = pdfFile.getVersion();
        serverVersion = Utils.getServerVersion();
        prefix = Utils.getPrefix();
    }

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            StateFlag flag = new StateFlag("area-sound-events", true);
            registry.register(flag);
            AREA_SOUND_EVENTS_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("area-sound-events");
            if (existing instanceof StateFlag) {
                AREA_SOUND_EVENTS_FLAG = (StateFlag) existing;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        try {
            new ConfigSettings(this).loadConfig();
            LocalizationManager.initialize(this, ConfigSettings.getInstance().getMainSettings().getLanguage());
            new RegionsSettings(this).load();
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
        }

        new UpdateChecker(this, 114973).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&cThere is not a new update available. &e(&7" + version + "&e)"));
            } else {
                Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&cThere is a new version available. &e(&7" + version + "&e)"));
                Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&cYou can download it at: &ahttps://www.spigotmc.org/resources/areasoundevents-create-sound-events-for-minecraft-1-17-1-1-20-4.114973/history"));
            }
        });

        worldGuardPlugin = PluginManager.setPlugin("WorldGuard", WorldGuardPlugin.class);
        worldEditPlugin = PluginManager.setPlugin("WorldEdit", WorldEditPlugin.class);

        registerCommands();
        registerEvents();

        Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&eHas been enabled! &fVersion: " + pluginVersion));
        Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&eThanks for using my plugin!   &f~LavinyTuttini"));
    }

    @Override
    public void onDisable() {
        RegionsSettings.getInstance().save(null);
        Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&eAreaSoundEvents plugin has been disabled!"));
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("areasoundevents")).setExecutor(new CommandManager(this));
        Objects.requireNonNull(this.getCommand("areasoundeventsprevpage")).setExecutor(new PrevPageCommand());
        Objects.requireNonNull(this.getCommand("areasoundeventsnextpage")).setExecutor(new NextPageCommand());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        chatListener = new ChatListener();
        Bukkit.getPluginManager().registerEvents(chatListener, this);
    }

    public ChatListener getChatListener() {
        return chatListener;
    }

    public static AreaSoundEvents getInstance() {
        if (instance == null) {
            instance = new AreaSoundEvents();
        }

        return instance;
    }
}
