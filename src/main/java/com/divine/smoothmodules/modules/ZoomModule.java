package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/**
 * Toggle-zoom: while enabled, narrows the FOV. Restores your FOV on disable.
 */
public class ZoomModule extends Module {

    private int previousFov = 70;

    public ZoomModule() {
        super("Zoom", "Toggle a zoomed-in field of view", ModuleCategory.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.options == null) return;
        previousFov = mc.options.getFov().getValue();
        mc.options.getFov().setValue(20);
    }

    @Override
    protected void onDisable() {
        if (mc.options == null) return;
        mc.options.getFov().setValue(previousFov);
    }
}
