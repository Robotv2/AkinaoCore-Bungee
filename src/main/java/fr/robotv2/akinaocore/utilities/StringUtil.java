package fr.robotv2.akinaocore.utilities;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class StringUtil {

    private static final String PREFIX = "&e&lAKINAO &8- ";

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(CommandSender sender, String message, boolean prefix) {
        if(prefix) {
            sender.sendMessage(colorize(PREFIX + message));
        } else {
            sender.sendMessage(colorize(message));
        }
    }
}
