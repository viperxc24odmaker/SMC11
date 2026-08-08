# Smooth Modules (SC)

A clean, VulkanMod-safe Fabric client for **Minecraft 1.21.11**. 27 modules in a
Dawn-style **SC Menu** with profiles, per-module scaling, a draggable HUD editor,
and a blurple/cyan theme. Pure `GuiGraphics` rendering.

## Controls
- **Right Shift** — open the SC Menu (left rail: categories + Profiles)
- **Right Ctrl** — open the HUD editor (drag elements)

## SC Menu
- Left rail sections: HUD, Render, Player, Cosmetics, Misc, Profiles
- Each module row: click to toggle; HUD rows have an inline scale stepper (0.5–3.0)
- **Profiles**: create, switch, and delete loadouts — auto-saved to `config/smooth-modules.json`

## Modules (27)
**HUD (22):** FPS, Coordinates, CPS, Keystrokes, Armor HUD, Direction, Angles,
Clock, Game Time, Speed, Session, Biome, Day Counter, Potion HUD, Ping, XP,
Target Info, Light Level, Memory, Held Item, Entities, Chunk.
**Render (2):** FullBright, Zoom. **Player (2):** ToggleSprint, AutoJump.
**Cosmetics (1):** Wings.

## Build
Push to `main` → GitHub Actions builds the jar → **Artifacts → smooth-modules**.
