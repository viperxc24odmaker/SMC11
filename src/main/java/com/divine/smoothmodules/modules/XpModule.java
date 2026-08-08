package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class XpModule extends HudModule {
    public XpModule() { super("XP", "Experience level and progress", 0.01, 0.40); }

    @Override
    public String[] getLines() {
        if (mc.player == null) return new String[]{ "Lvl --" };
        int pct = Math.round(mc.player.experienceProgress * 100f);
        return new String[]{ "Lvl " + mc.player.experienceLevel + " (" + pct + "%)" };
    }
}
