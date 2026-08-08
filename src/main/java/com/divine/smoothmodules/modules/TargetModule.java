package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

public class TargetModule extends HudModule {
    public TargetModule() { super("Target Info", "Info about the entity you look at", 0.01, 0.56); }

    @Override
    public String[] getLines() {
        try {
            if (mc.crosshairTarget instanceof EntityHitResult ehr) {
                Entity e = ehr.getEntity();
                String name = e.getName().getString();
                if (e instanceof LivingEntity le) {
                    int hp = Math.round(le.getHealth());
                    int max = Math.round(le.getMaxHealth());
                    return new String[]{ name, hp + " / " + max + " HP" };
                }
                return new String[]{ name };
            }
            return new String[]{ "No target" };
        } catch (Exception e) {
            return new String[]{ "No target" };
        }
    }
}
