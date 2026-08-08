package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Keeps a Totem of Undying in your offhand: when the offhand is empty and a
 * totem is in your inventory, it moves one over. Uses inventory clicks.
 *
 * NOTE: sends inventory packets - use where allowed.
 */
public class InventoryTotemModule extends Module {

    private static final int OFFHAND_SLOT = 45; // PlayerScreenHandler offhand
    private long lastMove = 0L;

    public InventoryTotemModule() {
        super("InventoryTotem", "Auto-refill a totem into your offhand", ModuleCategory.MISC);
    }

    @Override
    public void onTick() {
        try {
            if (mc.player == null || mc.interactionManager == null) return;
            if (!mc.player.getOffHandStack().isEmpty()) return;
            long now = System.currentTimeMillis();
            if (now - lastMove < 500L) return;

            PlayerScreenHandler h = mc.player.playerScreenHandler;
            int totemSlot = -1;
            for (int i = 9; i <= 44; i++) {
                ItemStack s = h.getSlot(i).getStack();
                if (s.isOf(Items.TOTEM_OF_UNDYING)) { totemSlot = i; break; }
            }
            if (totemSlot < 0) return;

            int sync = h.syncId;
            mc.interactionManager.clickSlot(sync, totemSlot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(sync, OFFHAND_SLOT, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(sync, totemSlot, 0, SlotActionType.PICKUP, mc.player);
            lastMove = now;
        } catch (Exception ignored) {}
    }
}
