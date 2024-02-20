package me.lavinytuttini.areasoundevents.managers;

import me.lavinytuttini.areasoundevents.utils.ServerVersion;
import me.lavinytuttini.areasoundevents.utils.Utils;
import org.bukkit.ChatColor;

public class MessageManager {
    public static String getColoredMessage(String message) {
        if (Utils.isServerVersionNewerThan(ServerVersion.v1_19_R1)) {
            return ChatColor.translateAlternateColorCodes('&', message);
        } else {
            return translateLegacyColors(message);
        }
    }

    private static String translateLegacyColors(String message) {
        char colorChar = '&';
        char colorCodeSymbol = ChatColor.COLOR_CHAR;
        char[] b = message.toCharArray();

        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == colorChar && ChatColor.getByChar(b[i + 1]) != null) {
                b[i] = colorCodeSymbol;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }
}
