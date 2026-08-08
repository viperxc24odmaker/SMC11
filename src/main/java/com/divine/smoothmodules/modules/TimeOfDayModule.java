package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class TimeOfDayModule extends HudModule {
    public TimeOfDayModule() { super("Game Time", "In-game clock (HH:MM)", 0.01, 0.30); }

    @Override
    public String[] getLines() {
        if (mc.world == null) return new String[]{ "--:--" };
        long t = mc.world.getTimeOfDay() % 24000L;
        long hours = (t / 1000L + 6L) % 24L;
        long minutes = (t % 1000L) * 60L / 1000L;
        return new String[]{ String.format("%02d:%02d MC", hours, minutes) };
    }
}
