package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/**
 * Toggles vanilla entity hitbox rendering (the F3+B view) so you can see mob
 * and player hitboxes - handy for survival aim and mob farms.
 */
public class HitboxesModule extends Module {
    public HitboxesModule() {
        super("Hitboxes", "Show entity hitboxes", ModuleCategory.MISC);
    }

    @Override
    protected void onEnable() { set(true); }

    @Override
    protected void onDisable() { set(false); }

    private void set(boolean v) {
        try {
            mc.getEntityRenderDispatcher().setRenderHitboxes(v);
        } catch (Exception ignored) {}
    }
}
