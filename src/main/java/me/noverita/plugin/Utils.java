package me.noverita.plugin;

import org.bukkit.entity.Player;

public class Utils {
    private static String messagePrefix = "[TTRPG Tools]";

    public static void sendMessage(Player recipient, String message) {
        recipient.sendMessage(messagePrefix + " " + message);
    }

    public static void setMessagePrefix(String prefix) {
        messagePrefix = prefix;
    }
}
