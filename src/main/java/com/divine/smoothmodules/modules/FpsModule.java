package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class FpsModule extends HudModule {
    public FpsModule() { super("FPS", "Shows current frames per second", 0.01, 0.02); }

    @Override
    public String[] getLines() {
        return new String[]{ mc.getCurrentFps() + " FPS" };
    }
}
