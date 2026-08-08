package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/** Reduces how far entities render, cutting load in crowded areas. */
public class LowEntityDistanceModule extends Module {
    private double previous = 1.0;

    public LowEntityDistanceModule() {
        super("LowEntityDist", "Render entities at shorter range", ModuleCategory.PERFORMANCE);
    }

    @Override
    protected void onEnable() {
        if (mc.options == null) return;
        previous = mc.options.getEntityDistanceScaling().getValue();
        mc.options.getEntityDistanceScaling().setValue(0.5);
    }

    @Override
    protected void onDisable() {
        if (mc.options == null) return;
        mc.options.getEntityDistanceScaling().setValue(previous);
    }
}
