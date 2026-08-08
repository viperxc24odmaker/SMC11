package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class DirectionModule extends HudModule {
    public DirectionModule() { super("Direction", "Cardinal facing direction", 0.01, 0.20); }

    @Override
    public String[] getLines() {
        if (mc.player == null) return new String[]{ "-- " };
        String card;
        switch (mc.player.getHorizontalFacing()) {
            case NORTH -> card = "North (-Z)";
            case SOUTH -> card = "South (+Z)";
            case EAST  -> card = "East (+X)";
            case WEST  -> card = "West (-X)";
            default    -> card = "--";
        }
        return new String[]{ card };
    }
}
