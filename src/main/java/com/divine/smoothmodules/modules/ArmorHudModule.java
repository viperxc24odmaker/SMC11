package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorHudModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorHudModule() { super("Armor HUD", "Equipped armor and durability", 0.80, 0.30); }

    @Override
    public String[] getLines() {
        try {
            if (mc.player == null) return new String[]{ "No armor" };
            List<String> out = new ArrayList<>();
            for (EquipmentSlot slot : SLOTS) {
                ItemStack stack = mc.player.getEquippedStack(slot);
                if (stack == null || stack.isEmpty()) continue;
                String name = stack.getName().getString();
                if (stack.isDamageable() && stack.getMaxDamage() > 0) {
                    int left = stack.getMaxDamage() - stack.getDamage();
                    int pct = Math.round(left * 100f / stack.getMaxDamage());
                    out.add(name + "  " + pct + "%");
                } else {
                    out.add(name);
                }
            }
            if (out.isEmpty()) return new String[]{ "No armor" };
            return out.toArray(new String[0]);
        } catch (Exception e) {
            return new String[]{ "No armor" };
        }
    }
}
