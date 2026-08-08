package com.divine.smoothmodules.mixin;

import com.divine.smoothmodules.modules.VeinMinerModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hooks block breaking to power VeinMiner. Captures the connected set before the
 * block is gone, then breaks that set after the original break succeeds. A busy
 * flag prevents the extra breaks from re-triggering the vein logic (recursion).
 */
@Mixin(ClientPlayerInteractionManager.class)
public class VeinMinerMixin {

    @Unique private static boolean smoothmodules$busy = false;

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void smoothmodules$capture(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (smoothmodules$busy || !VeinMinerModule.isActive()) return;
        VeinMinerModule.capture(pos);
    }

    @Inject(method = "breakBlock", at = @At("RETURN"))
    private void smoothmodules$vein(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (smoothmodules$busy || !VeinMinerModule.isActive()) return;
        if (!cir.getReturnValueZ()) { VeinMinerModule.clear(); return; }
        smoothmodules$busy = true;
        try {
            VeinMinerModule.breakCaptured((ClientPlayerInteractionManager) (Object) this);
        } finally {
            smoothmodules$busy = false;
        }
    }
}
