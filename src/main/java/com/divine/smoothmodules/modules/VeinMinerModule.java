package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**

Breaks connected logs/wood or ores of the same type when you mine one.

Client-side: capped at MAX_BLOCKS within RADIUS to avoid lag/abuse.
*/
public class VeinMinerModule extends Module {

private static final int MAX_BLOCKS = 64;
private static final int RADIUS = 6;

private static boolean active = false;
private static final List<BlockPos> captured = new ArrayList<>();

public VeinMinerModule() {
super("VeinMiner", "Break connected logs and ores at once", ModuleCategory.MISC);
}

@Override
protected void onEnable() {
active = true;
}

@Override
protected void onDisable() {
active = false;
captured.clear();
}

public static boolean isActive() {
return active;
}

/**

Called before a block breaks: flood-fill connected blocks of the same type.

Only logs/wood and ores are allowed to trigger vein mining.
*/
public static void capture(BlockPos origin) {
captured.clear();

if (!active) return;

MinecraftClient mc = MinecraftClient.getInstance();

if (mc.world == null || mc.player == null) return;

try {
BlockState originState = mc.world.getBlockState(origin);
Block target = originState.getBlock();

 if (!isVeinMineable(originState)) {
     return;
 }

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
                         || Math.abs(np.getZ() - origin.getZ()) > RADIUS) {
                     continue;
                 }

                 seen.add(np);

                 BlockState state = mc.world.getBlockState(np);

                 // Only continue through the exact same block type.
                 if (state.getBlock() == target) {
                     captured.add(np);
                     queue.add(np);

                     if (captured.size() >= MAX_BLOCKS) {
                         break;
                     }
                 }
             }
         }
     }
 }

} catch (Exception ignored) {
captured.clear();
}
}

/**

Only logs/wood and ores can activate VeinMiner.
*/
private static boolean isVeinMineable(BlockState state) {
return state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.ORES);
}

/**

Called after the original block has been broken successfully.
*/
public static void breakCaptured() {
if (!active || captured.isEmpty()) return;

MinecraftClient mc = MinecraftClient.getInstance();

if (mc.world == null || mc.player == null || mc.interactionManager == null) {
captured.clear();
return;
}

ClientPlayerInteractionManager interactionManager = mc.interactionManager;

try {
for (BlockPos pos : new ArrayList<>(captured)) {
if (!active) break;

     if (mc.world.getBlockState(pos).isAir()) continue;

     interactionManager.attackBlock(pos, mc.player.getHorizontalFacing().getOpposite());
 }

} catch (Exception ignored) {
} finally {
captured.clear();
}
}
}
