package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class CoordsModule extends HudModule {
    public CoordsModule() { super("Coordinates", "Shows your XYZ position", 0.01, 0.06); }

    @Override
    public String[] getLines() {
        if (mc.player == null) return new String[]{ "XYZ --" };
        return new String[]{
            "X " + mc.player.getBlockX(),
            "Y " + mc.player.getBlockY(),
            "Z " + mc.player.getBlockZ()
        };
    }
}
