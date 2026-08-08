package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Breaks all connected blocks of the same type when you mine one. Client-side:
 * capped at {@link #MAX_BLOCKS} within {@link #RADIUS} to avoid lag/abuse.
 *
 * NOTE: sends real break packets, so servers with anticheat may flag it.
 */
public class VeinMinerModule extends Module {

    private static final int MAX_BLOCKS = 64;
    private static final int RADIUS = 6;

    private static boolean active = false;
    private static final List<BlockPos> captured = new ArrayList<>();

    public VeinMinerModule() {
        super("VeinMiner", "Break connected same-type blocks at once", ModuleCategory.MISC);
    }

    @Override
    protected void onEnable() { active = true; }

    @Override
    protected void onDisable() { active = false; }

    public static boolean isActive() { return active; }

    /** Called before a block breaks: flood-fill connected same-type positions. */
    public static void capture(BlockPos origin) {
        captured.clear();
        if (!active) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        try {
            Block target = mc.world.getBlockState(origin).getBlock();
            if (target == null) return;

            Set<BlockPos> seen = new HashSet<>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            seen.add(origin);
            queue.add(origin);

            while (!queue.isEmpty() && captured.size() < MAX_BLOCKS) {
                BlockPos cur = queue.poll();
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;
                            BlockPos np = cur.add(dx, dy, dz);
                            if (seen.contains(np)) continue;
                            if (np.getManhattanDistance(origin) > RADIUS * 2) continue;
                            if (Math.abs(np.getX() - origin.getX()) > RADIUS
                                    || Math.abs(np.getY() - origin.getY()) > RADIUS
                                    || Math.abs(np.getZ() - origin.getZ()) > RADIUS) continue;
                            seen.add(np);
                            if (mc.world.getBlockState(np).getBlock() == target) {
                                captured.add(np);
                                queue.add(np);
                                if (captured.size() >= MAX_BLOCKS) break;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            captured.clear();
        }
    }

    /** Called after the origin block broke successfully: break the captured set. */
    public static void breakCaptured(ClientPlayerInteractionManager im) {
        if (!active || captured.isEmpty()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        try {
            for (BlockPos p : captured) {
                if (mc.world == null) break;
                if (mc.world.getBlockState(p).isAir()) continue;
                im.breakBlock(p);
                if (mc.player != null) mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            }
        } catch (Exception ignored) {
        } finally {
            captured.clear();
        }
    }

    public static void clear() { captured.clear(); }
}
