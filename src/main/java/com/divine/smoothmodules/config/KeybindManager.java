package com.divine.smoothmodules.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Holds the mod's core keybinds and lets them be rebound from the SC Menu.
 * Rebinds are persisted to config/smoothmodules-keys.json and applied on load.
 */
public final class KeybindManager {

    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("smoothmodules-keys.json");

    public static final String[] LABELS = { "Open Menu", "HUD Editor", "Elytra Swap" };
    private static final String[] KEYS = { "clickgui", "hudedit", "elytraswap" };

    private static final KeyBinding[] BINDS = new KeyBinding[3];

    private KeybindManager() {}

    public static void set(int index, KeyBinding kb) {
        if (index >= 0 && index < BINDS.length) BINDS[index] = kb;
    }

    public static int count() { return BINDS.length; }

    public static String keyName(int index) {
        KeyBinding kb = BINDS[index];
        if (kb == null) return "?";
        try { return kb.getBoundKeyLocalizedText().getString(); }
        catch (Exception e) { return "?"; }
    }

    public static void rebind(int index, int keyCode, int scanCode) {
        KeyBinding kb = BINDS[index];
        if (kb == null) return;
        try {
            kb.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode));
            KeyBinding.updateKeyboardsAll();
            save();
        } catch (Exception ignored) {}
    }

    public static void save() {
        JsonObject root = new JsonObject();
        for (int i = 0; i < BINDS.length; i++) {
            if (BINDS[i] == null) continue;
            try {
                root.addProperty(KEYS[i], BINDS[i].getBoundKeyTranslationKey());
            } catch (Exception ignored) {}
        }
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, root.toString());
        } catch (Exception ignored) {}
    }

    /** Apply saved binds. Call after the keybinds are registered. */
    public static void load() {
        if (!Files.exists(FILE)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            for (int i = 0; i < BINDS.length; i++) {
                if (BINDS[i] == null || !root.has(KEYS[i])) continue;
                String translationKey = root.get(KEYS[i]).getAsString();
                BINDS[i].setBoundKey(InputUtil.fromTranslationKey(translationKey));
            }
            KeyBinding.updateKeyboardsAll();
        } catch (Exception ignored) {}
    }
}
