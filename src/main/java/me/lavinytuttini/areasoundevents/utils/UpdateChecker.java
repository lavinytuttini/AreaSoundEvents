package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private static final String UPDATE_URL = "https://api.spigotmc.org/legacy/update.php?resource=";
    private final AreaSoundEvents areaSoundEvents;
    private final int resourceId;

    public UpdateChecker(AreaSoundEvents areaSoundEvents, int resourceId) {
        this.areaSoundEvents = areaSoundEvents;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.areaSoundEvents, () -> {
            try {
                URL url = new URL(UPDATE_URL + this.resourceId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                try (InputStream inputStream = connection.getInputStream();
                     Scanner scanner = new Scanner(inputStream)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                }
                connection.disconnect();
            } catch (IOException ex) {
                areaSoundEvents.getLogger().warning("Error checking for updates: " + ex.getMessage());
            }
        });
    }
}
