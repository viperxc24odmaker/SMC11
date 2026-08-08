package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

/**
 * Automatically sprints while you are moving forward on the ground.
 */
public class ToggleSprintModule extends Module {

    public ToggleSprintModule() {
        super("ToggleSprint", "Sprint automatically while moving", ModuleCategory.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.options == null) return;
        if (mc.player.isSneaking()) return;
        if (mc.options.forwardKey.isPressed() && !mc.player.horizontalCollision) {
            mc.player.setSprinting(true);
        }
    }
}
