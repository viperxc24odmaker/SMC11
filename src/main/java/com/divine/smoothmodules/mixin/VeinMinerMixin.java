package com.divine.smoothmodules.mixin;

import com.divine.smoothmodules.modules.VeinMinerModule;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class VeinMinerMixin {

    @Inject(
            method = "attackBlock",
            at = @At("HEAD")
    )
    private void smoothmodules$captureVeinBlocks(
            BlockPos pos,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null) {
            return;
        }

        BlockState state = mc.world.getBlockState(pos);

        if (VeinMinerModule.isValidBlock(state)) {
            VeinMinerModule.capture(pos);
        }
    }

    @Inject(
            method = "attackBlock",
            at = @At("RETURN")
    )
    private void smoothmodules$breakVeinBlocks(
            BlockPos pos,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue()) {
            VeinMinerModule.clear();
            return;
        }

        VeinMinerModule.breakCaptured();
    }
}
