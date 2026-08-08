package com.divine.smoothmodules.module;

import net.minecraft.client.MinecraftClient;

/**
 * Base class for every module. A module is a toggleable feature.
 * HUD modules extend {@link HudModule}; behaviour-only modules extend this directly.
 */
public abstract class Module {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final ModuleCategory category;

    private boolean enabled = false;

    protected Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        try {
            if (enabled) onEnable();
            else onDisable();
        } catch (Exception ignored) {
            // Never let a module crash the game on toggle.
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    /** Called once when the module is switched on. */
    protected void onEnable() {}

    /** Called once when the module is switched off. */
    protected void onDisable() {}

    /** Called every client tick while the module is enabled. */
    public void onTick() {}
}
