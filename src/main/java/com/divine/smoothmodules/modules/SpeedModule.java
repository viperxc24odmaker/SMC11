package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.util.PlayerStats;

public class SpeedModule extends HudModule {
    public SpeedModule() { super("Speed", "Horizontal speed in blocks/second", 0.01, 0.28); }

    @Override
    public String[] getLines() {
        return new String[]{ String.format("%.1f b/s", PlayerStats.getSpeedBps()) };
    }
}
