package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.particle.ParticlesMode;

/** Minimises particle rendering to reduce load. Restores your setting on disable. */
public class NoParticlesModule extends Module {
    private ParticlesMode previous = ParticlesMode.ALL;

    public NoParticlesModule() {
        super("NoParticles", "Minimise particles to save FPS", ModuleCategory.PERFORMANCE);
    }

    @Override
    protected void onEnable() {
        if (mc.options == null) return;
        previous = mc.options.getParticles().getValue();
        mc.options.getParticles().setValue(ParticlesMode.MINIMAL);
    }

    @Override
    protected void onDisable() {
        if (mc.options == null) return;
        mc.options.getParticles().setValue(previous);
    }
}
