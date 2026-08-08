package com.divine.smoothmodules;

import com.divine.smoothmodules.config.ProfileManager;
import com.divine.smoothmodules.gui.SCMenuScreen;
import com.divine.smoothmodules.gui.HudEditScreen;
import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.module.ModuleManager;
import com.divine.smoothmodules.util.ClickTracker;
import com.divine.smoothmodules.util.PlayerStats;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SmoothModulesClient implements ClientModInitializer {

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("smoothmodules", "main"));

    private KeyBinding clickGuiKey;
    private KeyBinding hudEditKey;
    private KeyBinding elytraSwapKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ProfileManager.load();

        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smoothmodules.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY
        ));

        hudEditKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smoothmodules.hudedit",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                CATEGORY
        ));

        elytraSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smoothmodules.elytraswap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                CATEGORY
        ));

        // expose binds to the rebinding UI, then apply any saved rebinds
        com.divine.smoothmodules.config.KeybindManager.set(0, clickGuiKey);
        com.divine.smoothmodules.config.KeybindManager.set(1, hudEditKey);
        com.divine.smoothmodules.config.KeybindManager.set(2, elytraSwapKey);
        com.divine.smoothmodules.config.KeybindManager.load();

        // Per-tick: keybinds, stats, module ticks.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (clickGuiKey.wasPressed()) {
                client.setScreen(new SCMenuScreen());
            }
            while (hudEditKey.wasPressed()) {
                client.setScreen(new HudEditScreen());
            }
            while (elytraSwapKey.wasPressed()) {
                com.divine.smoothmodules.modules.ElytraSwitcherModule.swap();
            }
            PlayerStats.onTick();
            com.divine.smoothmodules.util.ComboTracker.onTick();
            ModuleManager.onTick();
        });

        // Per-frame: poll clicks (accurate CPS) and render enabled HUD modules.
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            ClickTracker.update();

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            // Don't double-draw over the HUD editor (it renders them itself).
            if (client.currentScreen instanceof HudEditScreen) return;
            if (client.options != null && client.options.hudHidden) return;

            int w = ctx.getScaledWindowWidth();
            int h = ctx.getScaledWindowHeight();
            for (HudModule hm : ModuleManager.getHudModules()) {
                if (!hm.isEnabled()) continue;
                try {
                    hm.render(ctx, client.textRenderer, w, h, false);
                } catch (Exception ignored) {
                }
            }
        });

        SmoothModules.LOGGER.info("Smooth Modules client ready ({} modules).",
                ModuleManager.getModules().size());
    }
}
