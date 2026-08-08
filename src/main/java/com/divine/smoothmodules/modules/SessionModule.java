package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.util.PlayerStats;

public class SessionModule extends HudModule {
    public SessionModule() { super("Session", "Time since the game launched", 0.01, 0.32); }

    @Override
    public String[] getLines() {
        return new String[]{ "Session " + PlayerStats.getSessionString() };
    }
}
