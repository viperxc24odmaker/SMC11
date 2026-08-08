package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class MemoryModule extends HudModule {
    public MemoryModule() { super("Memory", "JVM memory usage", 0.80, 0.52); }

    @Override
    public String[] getLines() {
        Runtime rt = Runtime.getRuntime();
        long max = rt.maxMemory() / 1048576L;
        long used = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
        int pct = max > 0 ? (int) (used * 100L / max) : 0;
        return new String[]{ "Mem " + used + "/" + max + "MB (" + pct + "%)" };
    }
}
