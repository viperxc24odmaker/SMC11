package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Warns (action bar) when any equipped armor or your held item drops below 10%
 * durability. Purely reads item data - safe on any server.
 */
public class DurabilityWarningModule extends Module {

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
        EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
    };

    private long lastWarn = 0L;

    public DurabilityWarningModule() {
        super("DurabilityWarning", "Alerts when gear is nearly broken", ModuleCategory.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        long now = System.currentTimeMillis();
        if (now - lastWarn < 4000L) return;
        try {
            for (EquipmentSlot slot : SLOTS) {
                ItemStack s = mc.player.getEquippedStack(slot);
                if (s == null || s.isEmpty() || !s.isDamageable() || s.getMaxDamage() <= 0) continue;
                int left = s.getMaxDamage() - s.getDamage();
                int pct = left * 100 / s.getMaxDamage();
                if (pct <= 10) {
                    mc.player.sendMessage(
                        Text.literal("\u00A7c\u26A0 " + s.getName().getString() + " at " + pct + "%"), true);
                    lastWarn = now;
                    return;
                }
            }
        } catch (Exception ignored) {}
    }
}
