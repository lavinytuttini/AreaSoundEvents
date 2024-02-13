package me.lavinytuttini.areasoundevents;

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
    private static ServerVersion serverVersion;
    private static String prefix;
    private static StateFlag AREA_SOUND_EVENTS_FLAG;
    private WorldGuardPlugin worldGuardPlugin;
    private final String pluginVersion;
    PluginDescriptionFile pdfFile = this.getDescription();

    public AreaSoundEvents() {
        this.pluginVersion = pdfFile.getVersion();
    }

    @Override
    public void onLoad() {
        registerAreaSoundEventsFlag();
    }

    @Override
    public void onEnable() {
        instance = this;
        serverVersion = Utils.getServerVersion();
        prefix = Utils.getPrefix();
        initializeWorldGuard();
        loadConfigurations();
        checkForUpdates();
        registerCommandsAndEvents();
        printEnableMessage();
    }

    @Override
    public void onDisable() {
        saveRegionsSettings();
        printDisableMessage();
    }

    private void registerAreaSoundEventsFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            StateFlag flag = new StateFlag("area-sound-events", true);
            registry.register(flag);
            setAreaSoundEventsFlag(flag);
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("area-sound-events");
            if (existing instanceof StateFlag) {
                setAreaSoundEventsFlag((StateFlag) existing);
            } else {
                getLogger().severe("Failed to initialize area-sound-events flag. Existing flag is not of type StateFlag.");
                throw new RuntimeException(e);
            }
        }
    }

    private void initializeWorldGuard() {
        worldGuardPlugin = PluginManager.setPlugin("WorldGuard", WorldGuardPlugin.class);

        if (worldGuardPlugin == null) {
            getLogger().severe("WorldGuard plugin not found. Some features may not work correctly.");
        }
    }

    private void checkForUpdates() {
        new UpdateChecker(this, 114973).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&cThere is not a new update available. &e(&7" + version + "&e)"));
            } else {
                Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&cThere is a new version available. &e(&7" + version + "&e)"));
                Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&cYou can download it at: &ahttps://www.spigotmc.org/resources/areasoundevents-create-sound-events-for-minecraft-1-17-1-1-20-4.114973/history"));
            }
        });
    }

    private void loadConfigurations() {
        try {
            new ConfigSettings(this).loadConfig();
            LocalizationManager.initialize(this, ConfigSettings.getInstance().getMainSettings().getLanguage());
            new RegionsSettings(this).load();
        } catch (IOException e) {
            getLogger().severe("Failed to load configuration files. " + e.getMessage());
        }
    }

    private void registerCommandsAndEvents() {
        Objects.requireNonNull(this.getCommand("areasoundevents")).setExecutor(new CommandManager(this));
        Objects.requireNonNull(this.getCommand("areasoundeventsprevpage")).setExecutor(new PrevPageCommand());
        Objects.requireNonNull(this.getCommand("areasoundeventsnextpage")).setExecutor(new NextPageCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        chatListener = new ChatListener();
        Bukkit.getPluginManager().registerEvents(chatListener, this);
    }

    private void printEnableMessage() {
        Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&eHas been enabled! &fVersion: " + pluginVersion));
        Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&eThanks for using my plugin!   &f~LavinyTuttini"));
    }

    private void printDisableMessage() {
        Bukkit.getConsoleSender().sendMessage(prefix + MessageManager.getColoredMessage("&eAreaSoundEvents plugin has been disabled!"));
    }

    private void saveRegionsSettings() {
        RegionsSettings.getInstance(this).save(null);
    }

    public static WorldGuardPlugin getWorldGuardPlugin() {
        return instance.worldGuardPlugin;
    }

    public static ServerVersion getServerVersion() {
        return serverVersion;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static StateFlag getAreaSoundEventsFlag() {
        return AREA_SOUND_EVENTS_FLAG;
    }

    public void setAreaSoundEventsFlag(StateFlag stateFlag) {
        AREA_SOUND_EVENTS_FLAG = stateFlag;
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
