package com.divine.smoothmodules.module;

import com.divine.smoothmodules.modules.*;

import java.util.ArrayList;
import java.util.List;

// Auto-imported by wildcard: AutoClickerModule, BulletProofModule

/**
 * Holds every module instance and provides lookup / dispatch helpers.
 */
public final class ModuleManager {

    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        if (!MODULES.isEmpty()) return;

        // HUD (22)
        register(new FpsModule());
        register(new CoordsModule());
        register(new CpsModule());
        register(new KeystrokesModule());
        register(new ArmorHudModule());
        register(new DirectionModule());
        register(new FacingAngleModule());
        register(new ClockModule());
        register(new TimeOfDayModule());
        register(new SpeedModule());
        register(new SessionModule());
        register(new BiomeModule());
        register(new DayModule());
        register(new PotionHudModule());
        register(new PingModule());
        register(new XpModule());
        register(new TargetModule());
        register(new LightModule());
        register(new MemoryModule());
        register(new HeldItemModule());
        register(new EntityCountModule());
        register(new ComboModule());

        // Render (2)
        register(new FullBrightModule());
        register(new ZoomModule());

        // Player (2)
        register(new ToggleSprintModule());
        register(new AutoJumpModule());

        // Performance (4)
        register(new NoParticlesModule());
        register(new NoShadowsModule());
        register(new NoViewBobModule());
        register(new LowEntityDistanceModule());

        // Cosmetics (1)
        register(new WingsModule());

        // Misc (6)
        register(new VeinMinerModule());
        register(new ElytraSwitcherModule());
        register(new HitboxesModule());
        register(new AutoToolModule());
        register(new DurabilityWarningModule());
        register(new InventoryTotemModule());
        register(new AutoClickerModule());
        register(new BulletProofModule());
    }

    private static void register(Module m) {
        MODULES.add(m);
    }

    public static List<Module> getModules() {
        return MODULES;
    }

    public static List<Module> getByCategory(ModuleCategory category) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) {
            if (m.getCategory() == category) out.add(m);
        }
        return out;
    }

    public static List<HudModule> getHudModules() {
        List<HudModule> out = new ArrayList<>();
        for (Module m : MODULES) {
            if (m instanceof HudModule hm) out.add(hm);
        }
        return out;
    }

    public static Module getByName(String name) {
        for (Module m : MODULES) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    /** Dispatch a client tick to all enabled modules. */
    public static void onTick() {
        for (Module m : MODULES) {
            if (m.isEnabled()) {
                try {
                    m.onTick();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
