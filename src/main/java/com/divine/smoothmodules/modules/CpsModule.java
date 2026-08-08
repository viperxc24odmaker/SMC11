package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.util.ClickTracker;

public class CpsModule extends HudModule {
    public CpsModule() { super("CPS", "Clicks per second (L / R)", 0.01, 0.16); }

    @Override
    public String[] getLines() {
        return new String[]{ ClickTracker.leftCps() + " | " + ClickTracker.rightCps() + " CPS" };
    }
}
