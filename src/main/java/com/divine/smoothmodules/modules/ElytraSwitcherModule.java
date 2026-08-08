package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Swaps your chest slot between an elytra and a chestplate (kept in inventory)
 * with one keybind. Uses inventory slot clicks; guarded so it never crashes.
 *
 * NOTE: sends inventory packets - use on servers that allow it.
 */
public class ElytraSwitcherModule extends Module {

    private static boolean active = false;
    private static final int CHEST_SLOT = 6; // PlayerScreenHandler chest armor slot

    public ElytraSwitcherModule() {
        super("ElytraSwitcher", "Swap chestplate <-> elytra with a key", ModuleCategory.MISC);
    }

    @Override
    protected void onEnable() { active = true; }

    @Override
    protected void onDisable() { active = false; }

    public static boolean isActive() { return active; }

    private static boolean isChestplate(ItemStack s) {
        return s.isOf(Items.LEATHER_CHESTPLATE) || s.isOf(Items.CHAINMAIL_CHESTPLATE)
                || s.isOf(Items.IRON_CHESTPLATE) || s.isOf(Items.GOLDEN_CHESTPLATE)
                || s.isOf(Items.DIAMOND_CHESTPLATE) || s.isOf(Items.NETHERITE_CHESTPLATE);
    }

    /** Perform the swap (called from the keybind). */
    public static void swap() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!active || mc.player == null || mc.interactionManager == null) return;
        try {
            PlayerScreenHandler h = mc.player.playerScreenHandler;
            int sync = h.syncId;

            ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
            boolean chestIsElytra = chest.isOf(Items.ELYTRA);

            // find the item to swap in (main inventory + hotbar: slots 9..44)
            int invSlot = -1;
            for (int i = 9; i <= 44; i++) {
                ItemStack s = h.getSlot(i).getStack();
                if (s.isEmpty()) continue;
                boolean match = chestIsElytra ? isChestplate(s) : s.isOf(Items.ELYTRA);
                if (match) { invSlot = i; break; }
            }
            if (invSlot < 0) return;

            // 3-click swap: lift chest -> swap with target -> place target into chest slot
            mc.interactionManager.clickSlot(sync, CHEST_SLOT, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(sync, invSlot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(sync, CHEST_SLOT, 0, SlotActionType.PICKUP, mc.player);
        } catch (Exception ignored) {
        }
    }
}
