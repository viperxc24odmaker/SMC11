package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockModule extends HudModule {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    public ClockModule() { super("Clock", "Real-world system time", 0.01, 0.24); }

    @Override
    public String[] getLines() {
        return new String[]{ LocalTime.now().format(FMT) };
    }
}
