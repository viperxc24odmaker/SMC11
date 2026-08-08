package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import com.divine.smoothmodules.util.ClickTracker;
import com.divine.smoothmodules.util.Theme;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * WASD + LMB/RMB key display that lights up on press. Draws at the local origin;
 * scaling is handled by HudModule#render.
 */
public class KeystrokesModule extends HudModule {

    private static final int KEY = 14;
    private static final int GAP = 2;
    private static final int W = KEY * 3 + GAP * 2;
    private static final int H = KEY * 4 + GAP * 3;

    public KeystrokesModule() {
        super("Keystrokes", "WASD and mouse key display", 0.02, 0.62);
    }

    @Override
    public String[] getLines() {
        return new String[]{ "W A S D" };
    }

    @Override
    public int rawWidth(TextRenderer tr) { return W; }

    @Override
    public int rawHeight() { return H; }

    private boolean pressed(net.minecraft.client.option.KeyBinding kb) {
        return kb != null && kb.isPressed();
    }

    @Override
    protected void drawContent(DrawContext ctx, TextRenderer tr, boolean showAccent) {
        boolean fwd = mc.options != null && pressed(mc.options.forwardKey);
        boolean back = mc.options != null && pressed(mc.options.backKey);
        boolean left = mc.options != null && pressed(mc.options.leftKey);
        boolean right = mc.options != null && pressed(mc.options.rightKey);
        boolean lmb = mc.options != null && pressed(mc.options.attackKey);
        boolean rmb = mc.options != null && pressed(mc.options.useKey);

        int midX = KEY + GAP;
        drawKey(ctx, tr, midX, 0, KEY, KEY, "W", fwd, showAccent);
        int row2 = KEY + GAP;
        drawKey(ctx, tr, 0, row2, KEY, KEY, "A", left, showAccent);
        drawKey(ctx, tr, midX, row2, KEY, KEY, "S", back, showAccent);
        drawKey(ctx, tr, (KEY + GAP) * 2, row2, KEY, KEY, "D", right, showAccent);
        int row3 = row2 + KEY + GAP;
        int halfW = (W - GAP) / 2;
        drawKey(ctx, tr, 0, row3, halfW, KEY, String.valueOf(ClickTracker.leftCps()), lmb, showAccent);
        drawKey(ctx, tr, halfW + GAP, row3, halfW, KEY, String.valueOf(ClickTracker.rightCps()), rmb, showAccent);
    }

    private void drawKey(DrawContext ctx, TextRenderer tr, int x, int y, int w, int h,
                         String label, boolean active, boolean showAccent) {
        int bg = active ? (showAccent ? Theme.ACCENT_CYAN : Theme.ACCENT) : Theme.PANEL_BG;
        int fg = active ? 0xFF0B0B10 : Theme.TEXT;
        ctx.fill(x, y, x + w, y + h, bg);
        com.divine.smoothmodules.util.RenderUtil.border(ctx, x, y, w, h, 0x40FFFFFF);
        int tx = x + (w - tr.getWidth(label)) / 2;
        int ty = y + (h - 8) / 2;
        ctx.drawText(tr, label, tx, ty, fg, false);
    }
}
