package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class FacingAngleModule extends HudModule {
    public FacingAngleModule() { super("Angles", "Yaw and pitch", 0.01, 0.14); }

    @Override
    public String[] getLines() {
        if (mc.player == null) return new String[]{ "Yaw -- Pitch --" };
        float yaw = mc.player.getYaw() % 360f;
        if (yaw < 0) yaw += 360f;
        float pitch = mc.player.getPitch();
        return new String[]{ String.format("Yaw %.1f  Pitch %.1f", yaw, pitch) };
    }
}
