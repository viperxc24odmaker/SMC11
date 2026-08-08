package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import net.minecraft.item.ItemStack;

public class HeldItemModule extends HudModule {
    public HeldItemModule() { super("Held Item", "Item in your main hand", 0.01, 0.68); }

    @Override
    public String[] getLines() {
        try {
            if (mc.player == null) return new String[]{ "Empty" };
            ItemStack stack = mc.player.getMainHandStack();
            if (stack == null || stack.isEmpty()) return new String[]{ "Empty" };
            String name = stack.getName().getString();
            if (stack.getCount() > 1) name += " x" + stack.getCount();
            return new String[]{ name };
        } catch (Exception e) {
            return new String[]{ "Empty" };
        }
    }
}
