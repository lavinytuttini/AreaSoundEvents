package me.lavinytuttini.areasoundevents.managers;

import me.lavinytuttini.areasoundevents.utils.ServerVersion;
import me.lavinytuttini.areasoundevents.utils.Utils;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
    public static String getColoredMessage(String message) {
        if (Utils.isServerVersionNewerThan(ServerVersion.v1_19_R1)) {
            Pattern compiledPattern = Pattern.compile("#[a-fA-F0-9]{6}");

            for (Matcher matchedText = compiledPattern.matcher(message); matchedText.find(); matchedText = compiledPattern.matcher(message)) {
                String subText = message.substring(matchedText.start(), matchedText.end());
                message = message.replace(subText, "" + ChatColor.of(subText));
            }
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }
}
