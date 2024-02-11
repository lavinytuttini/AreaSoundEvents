package me.lavinytuttini.areasoundevents.utils;

import me.lavinytuttini.areasoundevents.AreaSoundEvents;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final AreaSoundEvents areaSoundEvents;
    private final int resourceId;

    public UpdateChecker(AreaSoundEvents areaSoundEvents, int resourceId) {
        this.areaSoundEvents = areaSoundEvents;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.areaSoundEvents, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                areaSoundEvents.getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}
