package com.divine.smoothmodules.gui;

import com.divine.smoothmodules.config.ProfileManager;
import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.module.ModuleManager;
import com.divine.smoothmodules.util.Theme;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Drag enabled HUD elements to reposition them (Right Ctrl). Snaps to a grid,
 * shows the grid faintly, and has a Reset button that restores every module to
 * 1.0x scale.
 */
public class HudEditScreen extends Screen {

    private static final int GRID = 6; // snap step in pixels

    private HudModule dragging = null;
    private double dragOffX, dragOffY;

    public HudEditScreen() {
        super(Text.literal("HUD Editor"));
    }

    private int snap(int v) { return Math.round((float) v / GRID) * GRID; }

    private boolean overReset(int mx, int my) {
        int bx = this.width / 2 - 34, by = 20;
        return mx >= bx && mx <= bx + 68 && my >= by && my <= by + 14;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x70000000);

        // faint grid
        for (int x = 0; x < this.width; x += GRID * 4) ctx.fill(x, 0, x + 1, this.height, 0x0AFFFFFF);
        for (int y = 0; y < this.height; y += GRID * 4) ctx.fill(0, y, this.width, y + 1, 0x0AFFFFFF);
        int mid = this.width / 2;
        ctx.fill(mid, 0, mid + 1, this.height, 0x22FFFFFF);
        ctx.fill(0, this.height / 2, this.width, this.height / 2 + 1, 0x22FFFFFF);

        for (HudModule hm : ModuleManager.getHudModules()) {
            if (!hm.isEnabled()) continue;
            boolean isDrag = (hm == dragging);
            hm.render(ctx, this.textRenderer, this.width, this.height, isDrag);
            int[] r = hm.screenRect(this.width, this.height, this.textRenderer);
            com.divine.smoothmodules.util.RenderUtil.border(ctx, r[0] - 1, r[1] - 1, r[2] + 2, r[3] + 2,
                    isDrag ? Theme.ACCENT_CYAN : 0x40FFFFFF);
        }

        // title
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("\u00A7lHUD Editor\u00A7r  \u00A77drag \u2022 grid-snap \u2022 Right Ctrl/Esc to finish"),
                this.width / 2, 8, Theme.TEXT);

        // reset button
        int bx = this.width / 2 - 34, by = 20;
        boolean hov = overReset(mouseX, mouseY);
        ctx.fill(bx, by, bx + 68, by + 14, hov ? Theme.ACCENT : 0x33FFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Reset Sizes"),
                this.width / 2, by + 3, hov ? 0xFF101014 : Theme.TEXT);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mx = mouseX();
        int my = mouseY();

        if (overReset(mx, my)) {
            for (HudModule hm : ModuleManager.getHudModules()) hm.setScale(1.0);
            ProfileManager.saveActive();
            return true;
        }

        var hudModules = ModuleManager.getHudModules();
        for (int i = hudModules.size() - 1; i >= 0; i--) {
            HudModule hm = hudModules.get(i);
            if (!hm.isEnabled()) continue;
            int[] r = hm.screenRect(this.width, this.height, this.textRenderer);
            if (mx >= r[0] && mx <= r[0] + r[2] && my >= r[1] && my <= r[1] + r[3]) {
                dragging = hm;
                dragOffX = mx - r[0];
                dragOffY = my - r[1];
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging != null) {
            int newX = snap((int) (mouseX() - dragOffX));
            int newY = snap((int) (mouseY() - dragOffY));
            dragging.setFrac((double) newX / this.width, (double) newY / this.height);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (dragging != null) {
            dragging = null;
            ProfileManager.saveActive();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        ProfileManager.saveActive();
        super.close();
    }

    @Override
    public boolean shouldPause() { return false; }

    private static int mouseX() {
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        return (int) (mc.mouse.getX() / mc.getWindow().getScaleFactor());
    }

    private static int mouseY() {
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        return (int) (mc.mouse.getY() / mc.getWindow().getScaleFactor());
    }
}
