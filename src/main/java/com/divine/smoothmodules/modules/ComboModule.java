package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.util.ComboTracker;

public class ComboModule extends HudModule {
    public ComboModule() { super("Combo", "Consecutive hits on a target (PvP)", 0.80, 0.44); }

    @Override
    public String[] getLines() {
        int c = ComboTracker.getCombo();
        return new String[]{ c + " Combo" };
    }
}
