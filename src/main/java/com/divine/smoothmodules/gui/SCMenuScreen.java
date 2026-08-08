package com.divine.smoothmodules.gui;

import com.divine.smoothmodules.config.KeybindManager;
import com.divine.smoothmodules.config.ProfileManager;
import com.divine.smoothmodules.cosmetics.WingVariants;
import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import com.divine.smoothmodules.module.ModuleManager;
import com.divine.smoothmodules.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * SC Menu (Right Shift): a tall, translucent panel flush to the left edge.
 * Top: SC title. Then stacked nav buttons (SC Mods / Cosmetics / Profiles).
 * Below: the selected section. SC Mods is a NoRisk-style grouped list -
 * a category header, its modules under it, then the next category, and so on.
 */
public class SCMenuScreen extends Screen {

    private static final int PANEL_W = 184;
    private static final int HEADER_H = 28;
    private static final int NAV_Y = 32;
    private static final int NAV_H = 18;
    private static final int NAV_GAP = 3;
    private static final int ROW_H = 13;

    private static final double[] SCALES = { 0.5, 0.75, 1.0, 1.25, 1.5, 2.0, 2.5, 3.0 };

    private static final String[] NAV = { "SC Mods", "Cosmetics", "Profiles", "Keybinds" };
    private static final int SEC_MODS = 0, SEC_COSMETICS = 1, SEC_PROFILES = 2, SEC_KEYBINDS = 3;

    // categories shown inside SC Mods (Cosmetics is its own section)
    private static final ModuleCategory[] MOD_CATS = {
        ModuleCategory.HUD, ModuleCategory.RENDER, ModuleCategory.PLAYER, ModuleCategory.PERFORMANCE, ModuleCategory.MISC
    };

    private static int section = SEC_MODS;
    private int scroll = 0;
    private float navAnim = -1f; // animated selection highlight (lerps toward target)
    private long openTime = 0L;  // for the entrance slide-in
    private int listeningKeybind = -1;  // which core bind is being rebound
    private boolean typingName = false; // profile name entry active
    private String nameBuffer = "";

    public SCMenuScreen() {
        super(Text.literal("SC Menu"));
    }

    // ---- a row in the SC Mods list: either a category header or a module ----
    private static final class Row {
        final boolean header;
        final String label;
        final Module module;
        Row(String label) { this.header = true; this.label = label; this.module = null; }
        Row(Module m) { this.header = false; this.label = m.getName(); this.module = m; }
    }

    private List<Row> buildModsList() {
        List<Row> rows = new ArrayList<>();
        for (ModuleCategory cat : MOD_CATS) {
            List<Module> mods = ModuleManager.getByCategory(cat);
            if (mods.isEmpty()) continue;
            rows.add(new Row(cat.getDisplayName()));
            for (Module m : mods) rows.add(new Row(m));
        }
        return rows;
    }

    private int contentY() { return NAV_Y + NAV.length * (NAV_H + NAV_GAP) + 8; }
    private int visibleRows() { return Math.max(1, (this.height - contentY() - 6) / ROW_H); }

    @Override
    protected void init() { scroll = 0; openTime = System.currentTimeMillis(); }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // entrance slide-in (subtle, ~160ms smoothstep)
        float p = Math.min(1f, (System.currentTimeMillis() - openTime) / 160f);
        p = p * p * (3f - 2f * p);
        var m = ctx.getMatrices();
        m.pushMatrix();
        m.translate((1f - p) * -14f, 0f);

        // translucent panel, flush left, full height (stays see-through)
        ctx.fill(0, 0, PANEL_W, this.height, Theme.PANEL_BG);
        ctx.fill(PANEL_W - 1, 0, PANEL_W, this.height, 0x18FFFFFF);  // inner sheen
        ctx.fill(PANEL_W, 0, PANEL_W + 1, this.height, Theme.ACCENT);    // grey edge line

        // header
        ctx.fill(0, 0, PANEL_W, HEADER_H, Theme.PANEL_HEADER);
        ctx.fill(10, 8, 26, 20, Theme.ACCENT);                          // SC badge
        ctx.fill(10, 8, 26, 9, 0x40FFFFFF);                         // badge sheen
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00A7lSC"), 18, 10, 0xFF101014);
        ctx.drawTextWithShadow(this.textRenderer, "\u00A7fClient", 30, 10, Theme.TEXT);
        ctx.drawTextWithShadow(this.textRenderer, "\u00A78v2.3.0", PANEL_W - 38, 10, Theme.TEXT_MUTED);
        ctx.fill(0, HEADER_H - 1, PANEL_W, HEADER_H, Theme.ACCENT);      // accent underline

        // sliding selection highlight (animated)
        float target = NAV_Y + section * (NAV_H + NAV_GAP);
        if (navAnim < 0) navAnim = target;
        navAnim += (target - navAnim) * 0.35f;
        int ny = Math.round(navAnim);
        ctx.fill(6, ny, PANEL_W - 6, ny + NAV_H, 0x22FFFFFF);
        ctx.fill(6, ny, 8, ny + NAV_H, Theme.ACCENT);

        // nav labels + hover (text nudges right on hover)
        for (int i = 0; i < NAV.length; i++) {
            int y = NAV_Y + i * (NAV_H + NAV_GAP);
            boolean sel = i == section;
            boolean hov = inRect(mouseX, mouseY, 6, y, PANEL_W - 12, NAV_H);
            if (!sel && hov) ctx.fill(6, y, PANEL_W - 6, y + NAV_H, Theme.ROW_HOVER);
            int tx = 14 + ((hov && !sel) ? 2 : 0);
            ctx.drawTextWithShadow(this.textRenderer, NAV[i], tx, y + 5, sel ? Theme.TEXT : Theme.TEXT_MUTED);
        }

        if (section == SEC_MODS) renderMods(ctx, mouseX, mouseY);
        else if (section == SEC_COSMETICS) renderCosmetics(ctx, mouseX, mouseY);
        else if (section == SEC_PROFILES) renderProfiles(ctx, mouseX, mouseY);
        else renderKeybinds(ctx, mouseX, mouseY);

        m.popMatrix();
        super.render(ctx, mouseX, mouseY, delta);
    }

    /** Rounded pill toggle (pure fill). */
    private void drawPill(DrawContext ctx, int px, int py, int w, int h, boolean on) {
        int col = on ? Theme.ACCENT : Theme.OFF;
        ctx.fill(px + 1, py, px + w - 1, py + h, col);
        ctx.fill(px, py + 1, px + w, py + h - 1, col);
        int knob = on ? px + w - 8 : px + 2;
        ctx.fill(knob, py + 1, knob + 6, py + h - 1, 0xFFF2F2F2);
    }

    // ---- SC Mods (NoRisk-style grouped list) ----
    private void renderMods(DrawContext ctx, int mouseX, int mouseY) {
        List<Row> rows = buildModsList();
        int cy = contentY();
        int vis = visibleRows();
        int shown = Math.min(vis, rows.size() - scroll);

        for (int r = 0; r < shown; r++) {
            Row row = rows.get(scroll + r);
            int y = cy + r * ROW_H;
            if (row.header) {
                ctx.fill(6, y + ROW_H - 1, PANEL_W - 6, y + ROW_H, 0x22FFFFFF);
                ctx.drawTextWithShadow(this.textRenderer, "\u00A77" + row.label.toUpperCase(), 8, y + 3, Theme.TEXT_MUTED);
                continue;
            }
            Module m = row.module;
            boolean hov = inRect(mouseX, mouseY, 6, y, PANEL_W - 12, ROW_H);
            if (hov) ctx.fill(6, y, PANEL_W - 6, y + ROW_H, Theme.ROW_HOVER);

            int nameX = 12 + (hov ? 2 : 0);
            ctx.drawTextWithShadow(this.textRenderer, m.getName(), nameX, y + 3,
                    m.isEnabled() ? Theme.TEXT : Theme.TEXT_MUTED);

            // toggle pill
            int pillW = 20, pillH = 9;
            int px = PANEL_W - pillW - 8;
            int py = y + (ROW_H - pillH) / 2;
            drawPill(ctx, px, py, pillW, pillH, m.isEnabled());

            // scale label (HUD only), click to cycle
            if (m instanceof HudModule hm) {
                String s = trimScale(hm.getScale()) + "x";
                int sw = this.textRenderer.getWidth(s);
                int sx = px - sw - 8;
                boolean sh = inRect(mouseX, mouseY, sx - 2, y, sw + 4, ROW_H);
                ctx.drawTextWithShadow(this.textRenderer, s, sx, y + 3, sh ? Theme.ACCENT : Theme.TEXT_MUTED);
            }
        }

        // clean scrollbar (green thumb) instead of arrows
        if (rows.size() > vis) {
            int tx = PANEL_W - 4;
            int top = cy;
            int bot = this.height - 6;
            int th = bot - top;
            ctx.fill(tx, top, tx + 2, bot, 0x1EFFFFFF);
            int thumbH = Math.max(14, th * vis / rows.size());
            int maxScroll = rows.size() - vis;
            int thumbY = top + (maxScroll <= 0 ? 0 : (th - thumbH) * scroll / maxScroll);
            ctx.fill(tx, thumbY, tx + 2, thumbY + thumbH, Theme.ACCENT);
        }
    }

    // ---- Cosmetics (Wings toggle + variant list) ----
    private void renderCosmetics(DrawContext ctx, int mouseX, int mouseY) {
        int cy = contentY();
        Module wings = ModuleManager.getByName("Wings");

        // wings toggle row
        boolean on = wings != null && wings.isEnabled();
        boolean hovW = inRect(mouseX, mouseY, 6, cy, PANEL_W - 12, ROW_H);
        if (hovW) ctx.fill(6, cy, PANEL_W - 6, cy + ROW_H, Theme.ROW_HOVER);
        ctx.drawTextWithShadow(this.textRenderer, "Wings", 12, cy + 3, on ? Theme.TEXT : Theme.TEXT_MUTED);
        int pillW = 20, pillH = 9, px = PANEL_W - pillW - 8, py = cy + (ROW_H - pillH) / 2;
        drawPill(ctx, px, py, pillW, pillH, on);

        // variant list (scrollable with the wheel)
        int listY = cy + ROW_H + 6;
        ctx.drawTextWithShadow(this.textRenderer, "\u00A78VARIANT", 8, listY, Theme.TEXT_MUTED);
        listY += 11;
        int vis = Math.max(1, (this.height - listY - 6) / ROW_H);
        int shown = Math.min(vis, WingVariants.count() - scroll);
        for (int r = 0; r < shown; r++) {
            int i = scroll + r;
            int y = listY + r * ROW_H;
            boolean cur = i == WingVariants.getSelected();
            boolean hov = inRect(mouseX, mouseY, 6, y, PANEL_W - 12, ROW_H);
            if (cur) ctx.fill(6, y, PANEL_W - 6, y + ROW_H, 0x24FFFFFF);
            else if (hov) ctx.fill(6, y, PANEL_W - 6, y + ROW_H, Theme.ROW_HOVER);
            if (cur) ctx.fill(6, y, 8, y + ROW_H, Theme.ACCENT);
            // color chip (rounded)
            ctx.fill(13, y + 2, 24, y + ROW_H - 2, WingVariants.COLORS[i]);
            ctx.fill(12, y + 3, 25, y + ROW_H - 3, WingVariants.COLORS[i]);
            ctx.fill(13, y + 2, 24, y + 3, 0x50FFFFFF); // sheen
            int nameX = 30 + (hov ? 2 : 0);
            ctx.drawTextWithShadow(this.textRenderer, WingVariants.NAMES[i], nameX, y + 3,
                    cur ? Theme.TEXT : Theme.TEXT_MUTED);
        }
        // scrollbar
        if (WingVariants.count() > vis) {
            int tx = PANEL_W - 4, top = listY, bot = this.height - 6, th = bot - top;
            ctx.fill(tx, top, tx + 2, bot, 0x1EFFFFFF);
            int thumbH = Math.max(14, th * vis / WingVariants.count());
            int maxS = WingVariants.count() - vis;
            int thumbY = top + (maxS <= 0 ? 0 : (th - thumbH) * scroll / maxS);
            ctx.fill(tx, thumbY, tx + 2, thumbY + thumbH, Theme.ACCENT);
        }
    }

    private int cosmeticsVisibleRows() {
        int listY = contentY() + ROW_H + 6 + 11;
        return Math.max(1, (this.height - listY - 6) / ROW_H);
    }

    // ---- Profiles ----
    private void renderProfiles(DrawContext ctx, int mouseX, int mouseY) {
        int cy = contentY();
        ctx.drawTextWithShadow(this.textRenderer, "\u00A77ACTIVE: \u00A7f" + ProfileManager.getActive(), 8, cy, Theme.TEXT);
        int listY = cy + 14;
        List<String> names = ProfileManager.getProfileNames();
        for (int i = 0; i < names.size(); i++) {
            String n = names.get(i);
            int y = listY + i * ROW_H;
            boolean cur = n.equals(ProfileManager.getActive());
            boolean hov = inRect(mouseX, mouseY, 6, y, PANEL_W - 12, ROW_H);
            if (cur) ctx.fill(6, y, PANEL_W - 6, y + ROW_H, 0x24FFFFFF);
            else if (hov) ctx.fill(6, y, PANEL_W - 6, y + ROW_H, Theme.ROW_HOVER);
            if (cur) ctx.fill(6, y, 8, y + ROW_H, Theme.ACCENT);
            ctx.drawTextWithShadow(this.textRenderer, n, 12, y + 3, cur ? Theme.TEXT : Theme.TEXT_MUTED);
            if (names.size() > 1) {
                ctx.drawTextWithShadow(this.textRenderer, "\u00A7cx", PANEL_W - 16, y + 3, Theme.DANGER);
            }
        }
        int inY = this.height - 40;
        ctx.fill(8, inY, PANEL_W - 8, inY + 14, typingName ? 0x33FFFFFF : 0x22FFFFFF);
        if (typingName) ctx.fill(8, inY, 10, inY + 14, Theme.ACCENT);
        String shown = nameBuffer.isEmpty()
                ? (typingName ? "\u00A77_" : "\u00A78type a name...")
                : nameBuffer + (typingName ? "\u00A77_" : "");
        ctx.drawTextWithShadow(this.textRenderer, shown, 13, inY + 3, Theme.TEXT);

        int by = this.height - 20;
        boolean hNew = inRect(mouseX, mouseY, 8, by, 54, 14);
        boolean hRen = inRect(mouseX, mouseY, 66, by, 66, 14);
        ctx.fill(8, by, 62, by + 14, hNew ? Theme.ACCENT : 0x33FFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("+ New"), 35, by + 3, hNew ? 0xFF101014 : Theme.TEXT);
        ctx.fill(66, by, 132, by + 14, hRen ? Theme.ACCENT : 0x33FFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Rename"), 99, by + 3, hRen ? 0xFF101014 : Theme.TEXT);
    }

    // ---- Keybinds (rebind core keys) ----
    private void renderKeybinds(DrawContext ctx, int mouseX, int mouseY) {
        int cy = contentY();
        ctx.drawTextWithShadow(this.textRenderer, "\u00A78CLICK A BIND, PRESS A KEY", 8, cy, Theme.TEXT_MUTED);
        int listY = cy + 14;
        for (int i = 0; i < KeybindManager.count(); i++) {
            int y = listY + i * (ROW_H + 4);
            boolean hov = inRect(mouseX, mouseY, 6, y, PANEL_W - 12, ROW_H);
            if (hov || listeningKeybind == i) ctx.fill(6, y, PANEL_W - 6, y + ROW_H, Theme.ROW_HOVER);
            ctx.drawTextWithShadow(this.textRenderer, KeybindManager.LABELS[i], 12, y + 3, Theme.TEXT);
            String key = (listeningKeybind == i) ? "> ... <" : "[" + KeybindManager.keyName(i) + "]";
            int kw = this.textRenderer.getWidth(key);
            ctx.drawTextWithShadow(this.textRenderer, key, PANEL_W - kw - 12, y + 3,
                    listeningKeybind == i ? Theme.TEXT : Theme.TEXT_MUTED);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int total = -1, vis = 0;
        if (section == SEC_MODS) { total = buildModsList().size(); vis = visibleRows(); }
        else if (section == SEC_COSMETICS) { total = WingVariants.count(); vis = cosmeticsVisibleRows(); }
        if (total > vis) {
            if (verticalAmount > 0) scroll = Math.max(0, scroll - 1);
            else if (verticalAmount < 0) scroll = Math.min(total - vis, scroll + 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    // ---- input ----
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mx = mouseX();
        int my = mouseY();

        // nav
        for (int i = 0; i < NAV.length; i++) {
            int y = NAV_Y + i * (NAV_H + NAV_GAP);
            if (inRect(mx, my, 6, y, PANEL_W - 12, NAV_H)) {
                section = i;
                scroll = 0;
                return true;
            }
        }

        if (section == SEC_MODS) return modsClick(mx, my);
        if (section == SEC_COSMETICS) return cosmeticsClick(mx, my);
        if (section == SEC_PROFILES) return profilesClick(mx, my);
        return keybindsClick(mx, my);
    }

    private boolean keybindsClick(int mx, int my) {
        int listY = contentY() + 14;
        for (int i = 0; i < KeybindManager.count(); i++) {
            int y = listY + i * (ROW_H + 4);
            if (inRect(mx, my, 6, y, PANEL_W - 12, ROW_H)) {
                listeningKeybind = i;
                return true;
            }
        }
        return false;
    }

    private boolean modsClick(int mx, int my) {
        List<Row> rows = buildModsList();
        int cy = contentY();
        int vis = visibleRows();

        int shown = Math.min(vis, rows.size() - scroll);
        for (int r = 0; r < shown; r++) {
            Row row = rows.get(scroll + r);
            if (row.header) continue;
            int y = cy + r * ROW_H;
            Module m = row.module;
            int pillW = 20, px = PANEL_W - pillW - 8;

            // scale label click (HUD)
            if (m instanceof HudModule hm) {
                String s = trimScale(hm.getScale()) + "x";
                int sw = this.textRenderer.getWidth(s);
                int sx = px - sw - 8;
                if (inRect(mx, my, sx - 2, y, sw + 4, ROW_H)) { cycleScale(hm); return true; }
            }
            if (inRect(mx, my, 6, y, PANEL_W - 12, ROW_H)) {
                m.toggle();
                ProfileManager.saveActive();
                return true;
            }
        }
        return false;
    }

    private boolean cosmeticsClick(int mx, int my) {
        int cy = contentY();
        Module wings = ModuleManager.getByName("Wings");
        if (inRect(mx, my, 6, cy, PANEL_W - 12, ROW_H)) {
            if (wings != null) { wings.toggle(); ProfileManager.saveActive(); }
            return true;
        }
        int listY = cy + ROW_H + 6 + 11;
        int vis = cosmeticsVisibleRows();
        int shown = Math.min(vis, WingVariants.count() - scroll);
        for (int r = 0; r < shown; r++) {
            int i = scroll + r;
            int y = listY + r * ROW_H;
            if (inRect(mx, my, 6, y, PANEL_W - 12, ROW_H)) {
                WingVariants.setSelected(i);
                ProfileManager.saveActive();
                return true;
            }
        }
        return false;
    }

    private boolean profilesClick(int mx, int my) {
        int cy = contentY();
        int listY = cy + 14;
        List<String> names = ProfileManager.getProfileNames();
        for (int i = 0; i < names.size(); i++) {
            int y = listY + i * ROW_H;
            if (names.size() > 1 && inRect(mx, my, PANEL_W - 18, y, 16, ROW_H)) {
                ProfileManager.deleteProfile(names.get(i));
                return true;
            }
            if (inRect(mx, my, 6, y, PANEL_W - 12, ROW_H)) {
                ProfileManager.switchTo(names.get(i));
                return true;
            }
        }
        // name input box focus
        int inY = this.height - 40;
        if (inRect(mx, my, 8, inY, PANEL_W - 16, 14)) { typingName = true; return true; }

        int by = this.height - 20;
        if (inRect(mx, my, 8, by, 54, 14)) {          // + New
            ProfileManager.createProfile(nameBuffer);
            nameBuffer = ""; typingName = false;
            return true;
        }
        if (inRect(mx, my, 66, by, 66, 14)) {         // Rename active
            ProfileManager.renameProfile(ProfileManager.getActive(), nameBuffer);
            nameBuffer = ""; typingName = false;
            return true;
        }
        typingName = false;
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningKeybind >= 0) {
            if (keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                KeybindManager.rebind(listeningKeybind, keyCode, scanCode);
            }
            listeningKeybind = -1;
            return true;
        }
        if (typingName) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) { typingName = false; return true; }
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
                if (!nameBuffer.isEmpty()) nameBuffer = nameBuffer.substring(0, nameBuffer.length() - 1);
                return true;
            }
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) {
                ProfileManager.createProfile(nameBuffer);
                nameBuffer = ""; typingName = false;
                return true;
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (typingName && nameBuffer.length() < 20 && chr >= 32 && chr != 127) {
            nameBuffer += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    private void cycleScale(HudModule hm) {
        int idx = 0;
        double cur = hm.getScale();
        for (int i = 0; i < SCALES.length; i++) {
            if (Math.abs(SCALES[i] - cur) < Math.abs(SCALES[idx] - cur)) idx = i;
        }
        idx = (idx + 1) % SCALES.length;
        hm.setScale(SCALES[idx]);
        ProfileManager.saveActive();
    }

    private static String trimScale(double s) {
        if (s == Math.floor(s)) return String.format("%.1f", s);
        return String.valueOf(s);
    }

    private static boolean inRect(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private static int mouseX() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return (int) (mc.mouse.getX() / mc.getWindow().getScaleFactor());
    }

    private static int mouseY() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return (int) (mc.mouse.getY() / mc.getWindow().getScaleFactor());
    }

    @Override
    public void close() {
        ProfileManager.saveActive();
        super.close();
    }

    @Override
    public boolean shouldPause() { return false; }
}
