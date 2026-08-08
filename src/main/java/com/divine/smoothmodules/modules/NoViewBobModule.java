package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/** Turns off view bobbing (smoother, cleaner view). Restores on disable. */
public class NoViewBobModule extends Module {
    private boolean previous = true;

    public NoViewBobModule() {
        super("NoViewBob", "Disable view bobbing", ModuleCategory.PERFORMANCE);
    }

    @Override
    protected void onEnable() {
        if (mc.options == null) return;
        previous = mc.options.getBobView().getValue();
        mc.options.getBobView().setValue(false);
    }

    @Override
    protected void onDisable() {
        if (mc.options == null) return;
        mc.options.getBobView().setValue(previous);
    }
}
