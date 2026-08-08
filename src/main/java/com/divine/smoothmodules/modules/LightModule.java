package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class LightModule extends HudModule {
    public LightModule() { super("Light Level", "Light at your current block", 0.01, 0.44); }

    @Override
    public String[] getLines() {
        if (mc.world == null || mc.player == null) return new String[]{ "Light --" };
        int light = mc.world.getLightLevel(mc.player.getBlockPos());
        return new String[]{ "Light " + light };
    }
}
