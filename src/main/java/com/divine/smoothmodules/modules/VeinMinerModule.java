package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class VeinMinerModule extends Module {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static boolean breakingVein = false;

    public VeinMinerModule() {
        super(
                "VeinMiner",
                "Break connected ores and logs",
                ModuleCategory.PLAYER
        );
    }

    @Override
    protected void onDisable() {
        breakingVein = false;
    }

    public static boolean isBreakingVein() {
        return breakingVein;
    }

    public static void mineVein(BlockPos startPos) {
        if (breakingVein
                || mc.player == null
                || mc.world == null
                || mc.interactionManager == null) {
            return;
        }

        BlockState startState = mc.world.getBlockState(startPos);

        if (!isValidBlock(startState)) {
            return;
        }

        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> vein = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        BlockPos start = startPos.toImmutable();

        visited.add(start);
        queue.add(start);

        int maxBlocks = 64;

        while (!queue.isEmpty() && vein.size() < maxBlocks) {
            BlockPos current = queue.poll();

            BlockState state = mc.world.getBlockState(current);

            if (!sameVeinType(startState, state)) {
                continue;
            }

            vein.add(current);

            for (Direction direction : Direction.values()) {
                BlockPos next = current.offset(direction);

                if (visited.add(next)) {
                    queue.add(next.toImmutable());
                }
            }
        }

        vein.remove(start);

        if (vein.isEmpty()) {
            return;
        }

        breakingVein = true;

        try {
            for (BlockPos pos : vein) {
                if (mc.world != null
                        && !mc.world.getBlockState(pos).isAir()
                        && mc.interactionManager != null) {

                    mc.interactionManager.attackBlock(
                            pos,
                            Direction.UP
                    );
                }
            }
        } finally {
            breakingVein = false;
        }
    }

    private static boolean sameVeinType(
            BlockState startState,
            BlockState otherState
    ) {
        if (isLog(startState)) {
            return isLog(otherState);
        }

        if (isOre(startState)) {
            return isOre(otherState);
        }

        return false;
    }

    public static boolean isValidBlock(BlockState state) {
        return isLog(state) || isOre(state);
    }

    private static boolean isLog(BlockState state) {
        return state.isOf(Blocks.OAK_LOG)
                || state.isOf(Blocks.SPRUCE_LOG)
                || state.isOf(Blocks.BIRCH_LOG)
                || state.isOf(Blocks.JUNGLE_LOG)
                || state.isOf(Blocks.ACACIA_LOG)
                || state.isOf(Blocks.DARK_OAK_LOG)
                || state.isOf(Blocks.MANGROVE_LOG)
                || state.isOf(Blocks.CHERRY_LOG)
                || state.isOf(Blocks.PALE_OAK_LOG)
                || state.isOf(Blocks.CRIMSON_STEM)
                || state.isOf(Blocks.WARPED_STEM);
    }

    private static boolean isOre(BlockState state) {
        return state.isOf(Blocks.COAL_ORE)
                || state.isOf(Blocks.DEEPSLATE_COAL_ORE)
                || state.isOf(Blocks.IRON_ORE)
                || state.isOf(Blocks.DEEPSLATE_IRON_ORE)
                || state.isOf(Blocks.COPPER_ORE)
                || state.isOf(Blocks.DEEPSLATE_COPPER_ORE)
                || state.isOf(Blocks.GOLD_ORE)
                || state.isOf(Blocks.DEEPSLATE_GOLD_ORE)
                || state.isOf(Blocks.REDSTONE_ORE)
                || state.isOf(Blocks.DEEPSLATE_REDSTONE_ORE)
                || state.isOf(Blocks.LAPIS_ORE)
                || state.isOf(Blocks.DEEPSLATE_LAPIS_ORE)
                || state.isOf(Blocks.DIAMOND_ORE)
                || state.isOf(Blocks.DEEPSLATE_DIAMOND_ORE)
                || state.isOf(Blocks.EMERALD_ORE)
                || state.isOf(Blocks.DEEPSLATE_EMERALD_ORE)
                || state.isOf(Blocks.NETHER_GOLD_ORE)
                || state.isOf(Blocks.NETHER_QUARTZ_ORE)
                || state.isOf(Blocks.ANCIENT_DEBRIS);
    }
}
