package com.noghost.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {

    private final JavaPlugin plugin;

    private boolean combatEnabled;
    private boolean highPingCompensation;
    private int maxPingCompensationMs;
    private boolean ghostHitProtection;
    private boolean duplicateHitProtection;
    private boolean distanceCheck;
    private boolean hitboxCheck;
    private boolean strictReachLimit;
    private double baseReach;
    private double maxReach;
    private final List<PingTier> pingTiers = new ArrayList<>();
    private long duplicateWindowMs;
    private int duplicateHistorySize;
    private boolean debugEnabled;
    private boolean logRejectedHits;
    private boolean logHighPingHits;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        FileConfiguration cfg = plugin.getConfig();

        combatEnabled = cfg.getBoolean("combat.enabled", true);
        highPingCompensation = cfg.getBoolean("combat.high-ping-compensation", true);
        maxPingCompensationMs = cfg.getInt("combat.max-ping-compensation-ms", 250);
        ghostHitProtection = cfg.getBoolean("combat.ghost-hit-protection", true);
        duplicateHitProtection = cfg.getBoolean("combat.duplicate-hit-protection", true);
        distanceCheck = cfg.getBoolean("combat.distance-check", true);
        hitboxCheck = cfg.getBoolean("combat.hitbox-check", true);
        strictReachLimit = cfg.getBoolean("combat.strict-reach-limit", true);

        baseReach = cfg.getDouble("combat.base-reach", 3.0);
        maxReach = cfg.getDouble("combat.max-reach", 4.5);

        duplicateWindowMs = cfg.getLong("combat.duplicate-window-ms", 50L);
        duplicateHistorySize = cfg.getInt("combat.duplicate-history-size", 200);

        debugEnabled = cfg.getBoolean("combat.debug.enabled", false);
        logRejectedHits = cfg.getBoolean("logging.log-rejected-hits", false);
        logHighPingHits = cfg.getBoolean("logging.log-high-ping-hits", false);

        pingTiers.clear();
        List<?> raw = cfg.getList("combat.ping-compensation-table");
        if (raw != null) {
            for (Object obj : raw) {
                if (obj instanceof ConfigurationSection sec) {
                    int maxPing = sec.getInt("max-ping", 0);
                    double extra = sec.getDouble("extra-reach", 0.0);
                    pingTiers.add(new PingTier(maxPing, extra));
                }
            }
        }

        if (pingTiers.isEmpty()) {
            pingTiers.add(new PingTier(50, 0.0));
            pingTiers.add(new PingTier(100, 0.3));
            pingTiers.add(new PingTier(150, 0.6));
            pingTiers.add(new PingTier(200, 0.9));
            pingTiers.add(new PingTier(250, 1.2));
        }
    }

    public boolean isCombatEnabled() { return combatEnabled; }
    public boolean isHighPingCompensation() { return highPingCompensation; }
    public int getMaxPingCompensationMs() { return maxPingCompensationMs; }
    public boolean isGhostHitProtection() { return ghostHitProtection; }
    public boolean isDuplicateHitProtection() { return duplicateHitProtection; }
    public boolean isDistanceCheck() { return distanceCheck; }
    public boolean isHitboxCheck() { return hitboxCheck; }
    public boolean isStrictReachLimit() { return strictReachLimit; }
    public double getBaseReach() { return baseReach; }
    public double getMaxReach() { return maxReach; }
    public long getDuplicateWindowMs() { return duplicateWindowMs; }
    public int getDuplicateHistorySize() { return duplicateHistorySize; }
    public boolean isDebugEnabled() { return debugEnabled; }
    public boolean isLogRejectedHits() { return logRejectedHits; }
    public boolean isLogHighPingHits() { return logHighPingHits; }
    public List<PingTier> getPingTiers() { return pingTiers; }

    public static final class PingTier {
        private final int maxPing;
        private final double extraReach;

        public PingTier(int maxPing, double extraReach) {
            this.maxPing = maxPing;
            this.extraReach = extraReach;
        }

        public int getMaxPing() { return maxPing; }
        public double getExtraReach() { return extraReach; }
    }
}
