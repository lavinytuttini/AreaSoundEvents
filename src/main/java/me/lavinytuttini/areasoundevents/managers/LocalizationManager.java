package me.lavinytuttini.areasoundevents.managers;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import me.lavinytuttini.areasoundevents.settings.ConfigSettings;
import me.lavinytuttini.areasoundevents.utils.Prefix;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class LocalizationManager {
    private static LocalizationManager instance;
    private final AreaSoundEvents areaSoundEvents;
    private final Map<String, String> localizedStrings = new HashMap<>();
    private final String localizationPath = "loc";
    private String localizationCode;
    private final List<String> supportedLocalizations = List.of(
            "en_EN", "es_ES"
    );
    private static final String prefixConsole = Prefix.getPrefixConsole();

    public LocalizationManager(AreaSoundEvents areaSoundEvents, String localizationCode) {
        instance = this;
        this.areaSoundEvents = areaSoundEvents;
        this.localizationCode = localizationCode;
        reload();
    }

    public String getString(String key) {
        String message = localizedStrings.get(key);
        if (message == null) {
            return prefixConsole + "Missing translation for key: " + key;
        }

        return message;
    }

    public void reload() {
        localizedStrings.clear();
        localizationCode = ConfigSettings.getInstance().getMainSettings().getLanguage();
        File langFolder = new File(areaSoundEvents.getDataFolder(), localizationPath);

        if (!langFolder.exists()) {
            boolean created = langFolder.mkdirs();
            if (!created) {
                getLogger().warning(prefixConsole + "Failed to create lang folder");
                return;
            }
        }

        copySupportedLocalizationResources();

        File selectedLocalizationFile = new File(langFolder, localizationCode + ".yml");
        if (!selectedLocalizationFile.exists()) {
            areaSoundEvents.saveResource(localizationPath + "/" + localizationCode + ".yml", false);
        }

        try (InputStream inputStream = new FileInputStream(selectedLocalizationFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                localizedStrings.put(entry.getKey(), entry.getValue().toString());
            }
        } catch (IOException e) {
            getLogger().severe(prefixConsole + e.getMessage());
        }
    }

    private void copySupportedLocalizationResources() {
        for(String localizations : supportedLocalizations) {
            File localizationFolder = new File(areaSoundEvents.getDataFolder(), localizations + ".yml");

            if (!localizationFolder.exists()) {
                areaSoundEvents.saveResource(localizationPath + "/" + localizations + ".yml", true);
            }
        }
    }

    public static LocalizationManager getInstance() {
        if (instance == null) {
            getLogger().warning(prefixConsole + "Localization has not been initialized");
        }
        return instance;
    }

    public static void initialize(AreaSoundEvents areaSoundEvents, String languageCode) {
        if (instance != null) {
            getLogger().warning(prefixConsole + "Localization has already been initialized");
        }
        instance = new LocalizationManager(areaSoundEvents, languageCode);
    }
}
