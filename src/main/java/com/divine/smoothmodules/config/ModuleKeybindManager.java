package com.divine.smoothmodules.config;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages per-module keybinds. Each module can have a custom toggle key.
 * Keybinds are persisted to config/smoothmodules-module-keys.json
 */
public final class ModuleKeybindManager {

	private static final Path FILE =
		FabricLoader.getInstance().getConfigDir().resolve("smoothmodules-module-keys.json");

	private static final Map<String, KeyBinding> MODULE_KEYS = new HashMap<>();

	private ModuleKeybindManager() {}

	/**
	 * Initialize keybinds for all misc and player modules.
	 * Call after ModuleManager.init().
	 */
	public static void init() {
		ModuleManager.getModules().forEach(module -> {
			String name = module.getName();
			KeyBinding kb = new KeyBinding(
				"key.smoothmodules.module." + name.toLowerCase().replace(" ", "_"),
				-1, // No default key
				"category.smoothmodules"
			);
			MODULE_KEYS.put(name, kb);
		});
		load();
	}

	public static KeyBinding getKeybind(Module module) {
		return MODULE_KEYS.get(module.getName());
	}

	public static KeyBinding getKeybind(String moduleName) {
		return MODULE_KEYS.get(moduleName);
	}

	public static void setKeybind(String moduleName, int keyCode, int scanCode) {
		KeyBinding kb = MODULE_KEYS.get(moduleName);
		if (kb == null) return;
		try {
			kb.setBoundKey(InputUtil.fromKeyCode(new KeyInput(keyCode, scanCode, 0)));
			KeyBinding.updateKeysByCode();
			save();
		} catch (Exception ignored) {}
	}

	public static String getKeyName(String moduleName) {
		KeyBinding kb = MODULE_KEYS.get(moduleName);
		if (kb == null) return "?";
		try {
			return kb.getBoundKeyLocalizedText().getString();
		} catch (Exception e) {
			return "?";
		}
	}

	public static void save() {
		JsonObject root = new JsonObject();
		MODULE_KEYS.forEach((name, kb) -> {
			try {
				root.addProperty(name, kb.getBoundKeyTranslationKey());
			} catch (Exception ignored) {}
		});
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, root.toString());
		} catch (Exception ignored) {}
	}

	public static void load() {
		if (!Files.exists(FILE)) return;
		try {
			JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
			MODULE_KEYS.forEach((name, kb) -> {
				if (!root.has(name)) return;
				try {
					String translationKey = root.get(name).getAsString();
					kb.setBoundKey(InputUtil.fromTranslationKey(translationKey));
				} catch (Exception ignored) {}
			});
			KeyBinding.updateKeysByCode();
		} catch (Exception ignored) {}
	}

	public static Map<String, KeyBinding> getAllKeybinds() {
		return new HashMap<>(MODULE_KEYS);
	}
}
