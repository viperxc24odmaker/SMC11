package com.divine.smoothmodules.config;

import com.divine.smoothmodules.cosmetics.WingVariants;
import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Named loadout profiles. Each profile stores every module's enabled state plus
 * each HUD module's position and scale. The whole thing lives in one JSON file:
 *
 * {
 *   "active": "Default",
 *   "profiles": { "Default": { "Fps": {enabled,x,y,scale}, ... }, "PvP": {...} }
 * }
 *
 * The live module state always reflects the active profile; any change calls
 * {@link #saveActive()} to snapshot back into it and write the file.
 */
public final class ProfileManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("smooth-modules.json");

    // Ordered profile name -> (module name -> saved entry)
    private static final Map<String, Map<String, Entry>> PROFILES = new LinkedHashMap<>();
    private static String active = "Default";

    private ProfileManager() {}

    private static final class Entry {
        boolean enabled;
        double x, y, scale;
    }

    // ---- lifecycle ----

    public static void load() {
        if (Files.exists(FILE)) {
            try {
                JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
                if (root.has("active")) active = root.get("active").getAsString();
                if (root.has("wingVariant")) WingVariants.setSelected(root.get("wingVariant").getAsInt());
                if (root.has("profiles")) {
                    JsonObject profs = root.getAsJsonObject("profiles");
                    for (String pName : profs.keySet()) {
                        JsonObject mods = profs.getAsJsonObject(pName);
                        Map<String, Entry> map = new LinkedHashMap<>();
                        for (String mName : mods.keySet()) {
                            JsonObject e = mods.getAsJsonObject(mName);
                            Entry entry = new Entry();
                            entry.enabled = e.has("enabled") && e.get("enabled").getAsBoolean();
                            entry.x = e.has("x") ? e.get("x").getAsDouble() : 0;
                            entry.y = e.has("y") ? e.get("y").getAsDouble() : 0;
                            entry.scale = e.has("scale") ? e.get("scale").getAsDouble() : 1.0;
                            map.put(mName, entry);
                        }
                        PROFILES.put(pName, map);
                    }
                }
            } catch (Exception e) {
                System.err.println("[SmoothModules] Failed to load profiles: " + e.getMessage());
            }
        }
        if (PROFILES.isEmpty()) {
            PROFILES.put("Default", new LinkedHashMap<>());
            active = "Default";
        }
        if (!PROFILES.containsKey(active)) {
            active = PROFILES.keySet().iterator().next();
        }
        applyToModules(active);
    }

    private static void writeFile() {
        JsonObject root = new JsonObject();
        root.addProperty("active", active);
        root.addProperty("wingVariant", WingVariants.getSelected());
        JsonObject profs = new JsonObject();
        for (Map.Entry<String, Map<String, Entry>> p : PROFILES.entrySet()) {
            JsonObject mods = new JsonObject();
            for (Map.Entry<String, Entry> m : p.getValue().entrySet()) {
                Entry e = m.getValue();
                JsonObject obj = new JsonObject();
                obj.addProperty("enabled", e.enabled);
                obj.addProperty("x", e.x);
                obj.addProperty("y", e.y);
                obj.addProperty("scale", e.scale);
                mods.add(m.getKey(), obj);
            }
            profs.add(p.getKey(), mods);
        }
        root.add("profiles", profs);
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(root));
        } catch (IOException e) {
            System.err.println("[SmoothModules] Failed to save profiles: " + e.getMessage());
        }
    }

    // ---- applying / snapshotting ----

    private static void applyToModules(String profileName) {
        Map<String, Entry> map = PROFILES.get(profileName);
        if (map == null) return;
        for (Module m : ModuleManager.getModules()) {
            Entry e = map.get(m.getName());
            m.setEnabled(e != null && e.enabled);
            if (m instanceof HudModule hm && e != null) {
                hm.setFrac(e.x, e.y);
                hm.setScale(e.scale <= 0 ? 1.0 : e.scale);
            }
        }
    }

    /** Snapshot the current live module state into the active profile and save. */
    public static void saveActive() {
        Map<String, Entry> map = new LinkedHashMap<>();
        for (Module m : ModuleManager.getModules()) {
            Entry e = new Entry();
            e.enabled = m.isEnabled();
            if (m instanceof HudModule hm) {
                e.x = hm.getXFrac();
                e.y = hm.getYFrac();
                e.scale = hm.getScale();
            } else {
                e.scale = 1.0;
            }
            map.put(m.getName(), e);
        }
        PROFILES.put(active, map);
        writeFile();
    }

    // ---- profile operations ----

    public static List<String> getProfileNames() {
        return new ArrayList<>(PROFILES.keySet());
    }

    public static String getActive() { return active; }

    public static void switchTo(String name) {
        if (!PROFILES.containsKey(name)) return;
        // snapshot current before leaving so nothing is lost
        saveActive();
        active = name;
        applyToModules(name);
        writeFile();
    }

    /** Create a new profile snapshotting the current state; returns its name. */
    public static String createProfile() {
        int n = PROFILES.size() + 1;
        String name = "Profile " + n;
        while (PROFILES.containsKey(name)) { n++; name = "Profile " + n; }
        // snapshot current live state into the new profile
        Map<String, Entry> map = new LinkedHashMap<>();
        for (Module m : ModuleManager.getModules()) {
            Entry e = new Entry();
            e.enabled = m.isEnabled();
            if (m instanceof HudModule hm) {
                e.x = hm.getXFrac(); e.y = hm.getYFrac(); e.scale = hm.getScale();
            } else e.scale = 1.0;
            map.put(m.getName(), e);
        }
        PROFILES.put(name, map);
        active = name;
        writeFile();
        return name;
    }

    /** Create a profile with a chosen name (falls back to auto-name if blank). */
    public static String createProfile(String name) {
        if (name == null || name.trim().isEmpty()) return createProfile();
        name = name.trim();
        if (name.length() > 20) name = name.substring(0, 20);
        if (PROFILES.containsKey(name)) { switchTo(name); return name; }
        Map<String, Entry> map = new LinkedHashMap<>();
        for (Module m : ModuleManager.getModules()) {
            Entry e = new Entry();
            e.enabled = m.isEnabled();
            if (m instanceof HudModule hm) {
                e.x = hm.getXFrac(); e.y = hm.getYFrac(); e.scale = hm.getScale();
            } else e.scale = 1.0;
            map.put(m.getName(), e);
        }
        PROFILES.put(name, map);
        active = name;
        writeFile();
        return name;
    }

    /** Rename an existing profile. No-op if the new name is blank or taken. */
    public static void renameProfile(String oldName, String newName) {
        if (newName == null) return;
        newName = newName.trim();
        if (newName.isEmpty() || newName.length() > 20) return;
        if (!PROFILES.containsKey(oldName) || PROFILES.containsKey(newName)) return;
        // rebuild preserving insertion order
        Map<String, Map<String, Entry>> rebuilt = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Entry>> e : PROFILES.entrySet()) {
            rebuilt.put(e.getKey().equals(oldName) ? newName : e.getKey(), e.getValue());
        }
        PROFILES.clear();
        PROFILES.putAll(rebuilt);
        if (active.equals(oldName)) active = newName;
        writeFile();
    }

    public static void deleteProfile(String name) {
        if (PROFILES.size() <= 1) return; // always keep at least one
        PROFILES.remove(name);
        if (active.equals(name)) {
            active = PROFILES.keySet().iterator().next();
            applyToModules(active);
        }
        writeFile();
    }
}
