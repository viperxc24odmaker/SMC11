package com.divine.smoothmodules.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

/**
 * Tracks a simple PvP combo: consecutive hits on a living entity. Resets if no
 * hit lands within the window. Call {@link #onTick()} each client tick.
 */
public final class ComboTracker {

    private static final long WINDOW_MS = 2500L;

    private static int combo = 0;
    private static long lastHit = 0L;
    private static boolean wasPressed = false;

    private ComboTracker() {}

    public static void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options == null) { combo = 0; wasPressed = false; return; }
        long now = System.currentTimeMillis();
        if (now - lastHit > WINDOW_MS) combo = 0;

        boolean pressed = mc.options.attackKey.isPressed();
        if (pressed && !wasPressed) {
            if (mc.crosshairTarget instanceof EntityHitResult ehr
                    && ehr.getEntity() instanceof LivingEntity
                    && ehr.getEntity() != mc.player) {
                combo++;
                lastHit = now;
            }
        }
        wasPressed = pressed;
    }

    public static int getCombo() {
        if (System.currentTimeMillis() - lastHit > WINDOW_MS) return 0;
        return combo;
    }
}
