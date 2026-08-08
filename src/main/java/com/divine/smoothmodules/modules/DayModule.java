package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class DayModule extends HudModule {
    public DayModule() { super("Day Counter", "Current in-game day number", 0.01, 0.36); }

    @Override
    public String[] getLines() {
        if (mc.world == null) return new String[]{ "Day --" };
        long day = mc.world.getTimeOfDay() / 24000L;
        return new String[]{ "Day " + day };
    }
}
