package com.divine.smoothmodules.util;

/**
 * SC palette. Neutral white accent (no blue), translucent dark panels.
 * All colors are ARGB ints (0xAARRGGBB).
 */
public final class Theme {

    private Theme() {}

    // Accent = neutral white (blue removed)
    public static final int ACCENT       = 0xFFBFC2C8; // grey accent
    public static final int ACCENT_CYAN  = 0xFFDDDDDD; // secondary highlight (drag/editor)

    // Panels (slightly transparent)
    public static final int PANEL_BG     = 0xB8101014; // translucent dark
    public static final int PANEL_HEADER = 0xD01A1A20;
    public static final int ROW_BG       = 0x30000000;
    public static final int ROW_HOVER    = 0x22FFFFFF;

    // Text
    public static final int TEXT         = 0xFFFFFFFF;
    public static final int TEXT_MUTED   = 0xFFAAAAAA;
    public static final int TEXT_DISABLED= 0xFF666666;

    // State
    public static final int ON           = 0xFF5BCB7A; // green
    public static final int OFF          = 0xFF3A3A42; // dark (pill off)
    public static final int DANGER       = 0xFFED4245; // red (delete)
}
