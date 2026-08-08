package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import net.minecraft.util.math.Box;

public class EntityCountModule extends HudModule {
    public EntityCountModule() { super("Entities", "Entities loaded within 64 blocks", 0.80, 0.60); }

    @Override
    public String[] getLines() {
        try {
            if (mc.world == null || mc.player == null) return new String[]{ "Ent --" };
            Box box = mc.player.getBoundingBox().expand(64.0);
            int count = mc.world.getOtherEntities(mc.player, box).size() + 1; // +1 for you
            return new String[]{ "Entities " + count };
        } catch (Exception e) {
            return new String[]{ "Ent --" };
        }
    }
}
