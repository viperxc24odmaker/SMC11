package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/**
 * True full brightness. A mixin on SimpleOption#getValue forces the gamma
 * value high while this is active, bypassing the vanilla 0..1 clamp.
 */
public class FullBrightModule extends Module {

    private static boolean active = false;

    public FullBrightModule() {
        super("FullBright", "True full brightness (see in the dark)", ModuleCategory.RENDER);
    }

    @Override
    protected void onEnable() { active = true; }

    @Override
    protected void onDisable() { active = false; }

    public static boolean isActive() { return active; }
}
