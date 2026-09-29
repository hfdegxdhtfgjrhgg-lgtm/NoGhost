package com.noghost.listeners;

import com.noghost.NoGhost;
import com.noghost.combat.CombatManager;
import com.noghost.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class CombatListener implements Listener {

    private final NoGhost plugin;
    private final ConfigManager config;
    private final CombatManager combatManager;

    public CombatListener(NoGhost plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        this.combatManager = new CombatManager(plugin, config);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!config.isCombatEnabled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        boolean allow = combatManager.processHit(attacker, victim);
        if (!allow) {
            event.setCancelled(true);
        }
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }
}
