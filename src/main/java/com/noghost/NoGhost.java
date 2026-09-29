package com.noghost;

import com.noghost.commands.NoGhostCommand;
import com.noghost.config.ConfigManager;
import com.noghost.listeners.CombatListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NoGhost extends JavaPlugin {

    private ConfigManager configManager;
    private CombatListener combatListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.combatListener = new CombatListener(this, configManager);
        getServer().getPluginManager().registerEvents(combatListener, this);

        PluginCommand cmd = getCommand("noghost");
        if (cmd != null) {
            NoGhostCommand executor = new NoGhostCommand(this, configManager);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        getLogger().info("NoGhost v" + getPluginMeta().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("NoGhost disabled.");
    }

    public boolean reloadPluginConfig() {
        try {
            reloadConfig();
            configManager.load();
            if (combatListener != null) {
                combatListener.getCombatManager().clear();
            }
            return true;
        } catch (Exception ex) {
            getLogger().severe("Failed to reload config: " + ex.getMessage());
            return false;
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
