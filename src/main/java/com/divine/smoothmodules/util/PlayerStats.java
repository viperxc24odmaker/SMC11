package com.divine.smoothmodules.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Derived per-tick stats that need frame-to-frame state: horizontal speed
 * (blocks/second) and session uptime. Call {@link #onTick()} every client tick.
 */
public final class PlayerStats {

    private static double lastX, lastZ;
    private static boolean hasLast = false;
    private static double speedBps = 0.0;

    private static final long SESSION_START = System.currentTimeMillis();

    private PlayerStats() {}

    public static void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;
        if (p == null) {
            hasLast = false;
            speedBps = 0.0;
            return;
        }
        double x = p.getX();
        double z = p.getZ();
        if (hasLast) {
            double dx = x - lastX;
            double dz = z - lastZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            // 20 ticks per second -> blocks per second. Smooth a little.
            double instant = dist * 20.0;
            speedBps = speedBps * 0.6 + instant * 0.4;
        }
        lastX = x;
        lastZ = z;
        hasLast = true;
    }

    public static double getSpeedBps() {
        return speedBps;
    }

    public static long getSessionMillis() {
        return System.currentTimeMillis() - SESSION_START;
    }

    public static String getSessionString() {
        long secs = getSessionMillis() / 1000L;
        long h = secs / 3600L;
        long m = (secs % 3600L) / 60L;
        long s = secs % 60L;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }
}
