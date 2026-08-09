package com.divine.smoothmodules.config;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages per-module keybinds. Each module can have a custom toggle key.
 * Keybinds are persisted to config/smoothmodules-module-keys.json
 * 
 * NOTE: This is a basic implementation. Full integration with KeyBindingHelper
 * would be needed for proper Minecraft keybind registration.
 */
public final class ModuleKeybindManager {

	private static final Path FILE =
		FabricLoader.getInstance().getConfigDir().resolve("smoothmodules-module-keys.json");

	private static final Map<String, Integer> MODULE_KEYS = new HashMap<>();

	private ModuleKeybindManager() {}

	/**
	 * Initialize keybinds for all modules.
	 * Stores keycodes per module (default: -1 = unbound).
	 */
	public static void init() {
		ModuleManager.getModules().forEach(module -> {
			MODULE_KEYS.put(module.getName(), -1);
		});
		load();
	}

	public static int getKeybind(Module module) {
		return MODULE_KEYS.getOrDefault(module.getName(), -1);
	}

	public static int getKeybind(String moduleName) {
		return MODULE_KEYS.getOrDefault(moduleName, -1);
	}

	public static void setKeybind(String moduleName, int keyCode) {
		if (MODULE_KEYS.containsKey(moduleName)) {
			MODULE_KEYS.put(moduleName, keyCode);
			save();
		}
	}

	public static String getKeyName(String moduleName) {
		int keyCode = MODULE_KEYS.getOrDefault(moduleName, -1);
		if (keyCode == -1) return "UNBOUND";
		try {
			return InputUtil.fromKeyCode(keyCode, 0).getLocalizedText().getString();
		} catch (Exception e) {
			return "UNKNOWN";
		}
	}

	public static void save() {
		JsonObject root = new JsonObject();
		MODULE_KEYS.forEach(root::addProperty);
		try {
			Files.createDirectories(FILE.getParent());
			Files.writeString(FILE, root.toString());
		} catch (Exception ignored) {}
	}

	public static void load() {
		if (!Files.exists(FILE)) return;
		try {
			JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
			MODULE_KEYS.forEach((name, defaultVal) -> {
				if (root.has(name)) {
					MODULE_KEYS.put(name, root.get(name).getAsInt());
				}
			});
		} catch (Exception ignored) {}
	}

	public static Map<String, Integer> getAllKeybinds() {
		return new HashMap<>(MODULE_KEYS);
	}
}

