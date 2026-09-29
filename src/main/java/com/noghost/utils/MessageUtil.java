package com.noghost.utils;

import org.bukkit.ChatColor;

public final class MessageUtil {

    private MessageUtil() {
    }

    public static String color(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
