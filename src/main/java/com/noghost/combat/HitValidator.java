package com.noghost.combat;

import com.noghost.config.ConfigManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public final class HitValidator {

    private final ConfigManager config;
    private final PingCompensator pingCompensator;

    public HitValidator(ConfigManager config, PingCompensator pingCompensator) {
        this.config = config;
        this.pingCompensator = pingCompensator;
    }

    public Result validate(Player attacker, Player victim) {
        if (attacker == null || victim == null) return Result.rejected("null player");
        if (!attacker.isOnline() || !victim.isOnline()) return Result.rejected("offline");
        if (attacker.isDead() || victim.isDead()) return Result.rejected("dead");
        if (attacker.getUniqueId().equals(victim.getUniqueId())) return Result.rejected("self hit");
        if (attacker.getGameMode() == GameMode.SPECTATOR
                || victim.getGameMode() == GameMode.SPECTATOR) return Result.rejected("spectator");
        if (victim.isInvulnerable()) return Result.rejected("invulnerable");
        if (attacker.getWorld() != victim.getWorld()) return Result.rejected("different world");

        if (config.isDistanceCheck()) {
            double distance = eyeToBoxDistance(attacker, victim);
            double allowed = pingCompensator.getAllowedReach(attacker);
            if (distance > allowed) {
                return Result.rejected("reach(" + String.format("%.2f", distance)
                        + " > " + String.format("%.2f", allowed) + ")");
            }
        }

        if (config.isHitboxCheck()) {
            if (!isRoughlyFacing(attacker, victim)) return Result.rejected("direction");
        }

        return Result.accepted();
    }

    private double eyeToBoxDistance(Player attacker, Player victim) {
        Location eye = attacker.getEyeLocation();
        BoundingBox box = victim.getBoundingBox();

        double cx = clamp(eye.getX(), box.getMinX(), box.getMaxX());
        double cy = clamp(eye.getY(), box.getMinY(), box.getMaxY());
        double cz = clamp(eye.getZ(), box.getMinZ(), box.getMaxZ());

        double dx = eye.getX() - cx;
        double dy = eye.getY() - cy;
        double dz = eye.getZ() - cz;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private boolean isRoughlyFacing(Player attacker, Player victim) {
        Vector dir = attacker.getEyeLocation().getDirection().normalize();
        Vector toVictim = victim.getLocation().toVector()
                .add(new Vector(0, victim.getHeight() / 2.0, 0))
                .subtract(attacker.getEyeLocation().toVector());

        if (toVictim.lengthSquared() < 1.0E-4) return true;
        toVictim.normalize();
        return dir.dot(toVictim) > 0.0;
    }

    public static final class Result {
        private final boolean accepted;
        private final String reason;

        private Result(boolean accepted, String reason) {
            this.accepted = accepted;
            this.reason = reason;
        }

        public static Result accepted() { return new Result(true, null); }
        public static Result rejected(String reason) { return new Result(false, reason); }
        public boolean isAccepted() { return accepted; }
        public String getReason() { return reason; }
    }
}
