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
        // The public EntityRenderManager setter was removed in newer Minecraft
        // versions. Keep this module source-compatible with 1.21.11.
        try {
            var options = mc.options;
            var field = options.getClass().getDeclaredField("debugEnabled");
            field.setAccessible(true);
            field.setBoolean(options, v);
        } catch (Exception ignored) {}
    }
}
