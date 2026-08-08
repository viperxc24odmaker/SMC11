package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/**
 * Enables vanilla auto-jump while active. Restores your setting on disable.
 */
public class AutoJumpModule extends Module {

    private boolean previous = false;

    public AutoJumpModule() {
        super("AutoJump", "Toggle vanilla auto-jump", ModuleCategory.PLAYER);
    }

    @Override
    protected void onEnable() {
        if (mc.options == null) return;
        previous = mc.options.getAutoJump().getValue();
        mc.options.getAutoJump().setValue(true);
    }

    @Override
    protected void onDisable() {
        if (mc.options == null) return;
        mc.options.getAutoJump().setValue(previous);
    }
}
