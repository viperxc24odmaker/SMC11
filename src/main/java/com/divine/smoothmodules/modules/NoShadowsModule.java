package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/** Disables entity shadows. Restores your setting on disable. */
public class NoShadowsModule extends Module {
    private boolean previous = true;

    public NoShadowsModule() {
        super("NoShadows", "Disable entity shadows for FPS", ModuleCategory.PERFORMANCE);
    }

    @Override
    protected void onEnable() {
        if (mc.options == null) return;
        previous = mc.options.getEntityShadows().getValue();
        mc.options.getEntityShadows().setValue(false);
    }

    @Override
    protected void onDisable() {
        if (mc.options == null) return;
        mc.options.getEntityShadows().setValue(previous);
    }
}
