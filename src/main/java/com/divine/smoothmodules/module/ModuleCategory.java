package com.divine.smoothmodules.module;

public enum ModuleCategory {
    HUD("HUD"),
    RENDER("Render"),
    PLAYER("Player"),
    PERFORMANCE("Performance"),
    COSMETICS("Cosmetics"),
    MISC("Misc");

    private final String displayName;
    ModuleCategory(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
