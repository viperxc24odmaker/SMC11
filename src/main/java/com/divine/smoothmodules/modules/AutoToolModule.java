package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * While mining, automatically switches your hotbar to the fastest tool for the
 * block you're breaking. Only touches your hotbar selection - no packets.
 */
public class AutoToolModule extends Module {
    public AutoToolModule() {
        super("AutoTool", "Swap to the best tool while mining", ModuleCategory.MISC);
    }

    @Override
    public void onTick() {
        try {
            if (mc.player == null || mc.world == null || mc.options == null) return;
            if (!mc.options.attackKey.isPressed()) return;
            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof BlockHitResult bhr)) return;
            BlockState state = mc.world.getBlockState(bhr.getBlockPos());
            if (state.isAir()) return;

            int bestSlot = -1;
            float bestSpeed = -1f;
            for (int i = 0; i < 9; i++) {
                ItemStack s = mc.player.getInventory().getStack(i);
                if (s.isEmpty()) continue;
                float speed = s.getMiningSpeedMultiplier(state);
                if (speed > bestSpeed) { bestSpeed = speed; bestSlot = i; }
            }
            if (bestSlot >= 0 && bestSpeed > 1.0f) {
                mc.player.getInventory().setSelectedSlot(bestSlot);
            }
        } catch (Exception ignored) {}
    }
}
