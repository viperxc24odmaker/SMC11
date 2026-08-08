package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/**
 * Elytra-style wings cosmetic. A mixin on ElytraFeatureRenderer supplies a
 * custom wings texture so vanilla renders elytra wings on the player's back
 * (with correct placement + fold/spread animation) even without a real elytra.
 */
public class WingsModule extends Module {

    private static boolean active = false;

    public WingsModule() {
        super("Wings", "Elytra-style wings cosmetic on your back", ModuleCategory.COSMETICS);
    }

    @Override
    protected void onEnable() { active = true; }

    @Override
    protected void onDisable() { active = false; }

    public static boolean isActive() { return active; }
}
