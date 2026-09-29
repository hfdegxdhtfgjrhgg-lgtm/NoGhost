package com.noghost.commands;

import com.noghost.NoGhost;
import com.noghost.config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class NoGhostCommand implements CommandExecutor, TabCompleter {

    private final NoGhost plugin;
    private final ConfigManager config;

    public NoGhostCommand(NoGhost plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage(msg("messages.unknown-command"));
            return true;
        }

        if (!args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(msg("messages.unknown-command"));
            return true;
        }

        if (!sender.hasPermission("noghost.admin")) {
            sender.sendMessage(msg("messages.no-permission"));
            return true;
        }

        boolean ok = plugin.reloadPluginConfig();
        sender.sendMessage(ok ? msg("messages.reload-success") : msg("messages.reload-failed"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String msg(String path) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String text = plugin.getConfig().getString(path, path);
        return ChatColor.translateAlternateColorCodes('&', prefix + text);
    }
}
