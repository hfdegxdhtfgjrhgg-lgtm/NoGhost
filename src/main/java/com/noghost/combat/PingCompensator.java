package com.noghost.combat;

import com.noghost.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.List;

public final class PingCompensator {

    private final ConfigManager config;

    public PingCompensator(ConfigManager config) {
        this.config = config;
    }

    public double getAllowedReach(Player attacker) {
        double base = config.getBaseReach();
        if (!config.isHighPingCompensation()) return base;

        int ping = getPing(attacker);
        if (ping <= 0) return base;

        int maxPing = config.getMaxPingCompensationMs();
        int clamped = Math.min(ping, maxPing);

        List<ConfigManager.PingTier> tiers = config.getPingTiers();
        ConfigManager.PingTier best = null;
        for (ConfigManager.PingTier tier : tiers) {
            if (clamped <= tier.getMaxPing()) {
                if (best == null || tier.getMaxPing() < best.getMaxPing()) {
                    best = tier;
                }
            }
        }

        double extra = (best != null) ? best.getExtraReach() : 0.0;
        double allowed = base + extra;
        double cap = config.getMaxReach();
        return Math.min(allowed, cap);
    }

    public int getPing(Player player) {
        try {
            return player.getPing();
        } catch (Throwable t) {
            return 0;
        }
    }
}
