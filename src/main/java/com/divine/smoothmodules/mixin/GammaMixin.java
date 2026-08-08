package com.divine.smoothmodules.mixin;

import com.divine.smoothmodules.modules.FullBrightModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * When FullBright is active, force the gamma option to report a very high value
 * so the world is fully lit, bypassing the vanilla 0..1 clamp.
 */
@Mixin(SimpleOption.class)
public class GammaMixin {

    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private void smoothmodules$fullbright(CallbackInfoReturnable<Object> cir) {
        if (!FullBrightModule.isActive()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.options == null) return;
        // Only override the gamma option, nothing else.
        if ((Object) this == mc.options.getGamma()) {
            cir.setReturnValue(15.0D);
        }
    }
}
