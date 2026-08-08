package com.divine.smoothmodules.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class VeinMinerModule extends Module {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Set<BlockPos> capturedBlocks = new HashSet<>();

    public VeinMinerModule() {
        super("VeinMiner", "Break connected ores and logs");
    }

    @Override
    public void onDisable() {
        clear();
    }

    public static void capture(BlockPos pos) {
        capturedBlocks.add(pos.toImmutable());
    }

    public static void clear() {
        capturedBlocks.clear();
    }

    public static void breakCaptured() {
        if (mc.player == null || mc.interactionManager == null) {
            clear();
            return;
        }

        Set<BlockPos> blocks = new HashSet<>(capturedBlocks);
        clear();

        for (BlockPos pos : blocks) {
            if (!mc.world.getBlockState(pos).isAir()) {
                mc.interactionManager.attackBlock(
                        pos,
                        net.minecraft.util.math.Direction.UP
                );
            }
        }
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
