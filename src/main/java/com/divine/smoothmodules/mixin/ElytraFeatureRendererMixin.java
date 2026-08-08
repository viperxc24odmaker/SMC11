package com.divine.smoothmodules.mixin;

import com.divine.smoothmodules.cosmetics.WingVariants;
import com.divine.smoothmodules.modules.WingsModule;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * When the Wings cosmetic is active, supply the selected wing texture so the
 * vanilla elytra renderer draws wings on the entity's back with its normal
 * placement and fold/spread animation - even when no real elytra is worn.
 */
@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private static void smoothmodules$wings(BipedEntityRenderState state,
                                            CallbackInfoReturnable<Identifier> cir) {
        if (WingsModule.isActive()) {
            cir.setReturnValue(WingVariants.getSelectedTexture());
        }
    }
}
