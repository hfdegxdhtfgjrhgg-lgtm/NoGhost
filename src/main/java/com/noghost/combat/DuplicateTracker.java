package com.noghost.combat;

import com.noghost.config.ConfigManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

public final class DuplicateTracker {

    private final ConfigManager config;
    private final Deque<HitRecord> history = new ArrayDeque<>();

    public DuplicateTracker(ConfigManager config) {
        this.config = config;
    }

    public synchronized boolean isDuplicate(UUID attacker, UUID victim, long now) {
        if (!config.isDuplicateHitProtection()) return false;

        long window = config.getDuplicateWindowMs();

        while (!history.isEmpty() && (now - history.peekFirst().timestamp) > window) {
            history.pollFirst();
        }

        for (HitRecord rec : history) {
            if (rec.attacker.equals(attacker) && rec.victim.equals(victim)) {
                return true;
            }
        }

        history.addLast(new HitRecord(attacker, victim, now));

        int maxSize = config.getDuplicateHistorySize();
        while (history.size() > maxSize) {
            history.pollFirst();
        }

        return false;
    }

    public synchronized void clear() {
        history.clear();
    }

    private static final class HitRecord {
        final UUID attacker;
        final UUID victim;
        final long timestamp;

        HitRecord(UUID attacker, UUID victim, long timestamp) {
            this.attacker = attacker;
            this.victim = victim;
            this.timestamp = timestamp;
        }
    }
}
