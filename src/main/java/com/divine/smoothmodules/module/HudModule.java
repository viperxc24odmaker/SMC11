package com.divine.smoothmodules.module;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * A HUD text element with Dawn-style rounded rendering. Position is a screen
 * fraction (resolution-independent) and each element has its own scale. The
 * on-screen rectangle is computed once in {@link #screenRect} and used by BOTH
 * rendering and hit-testing, so the drag hitbox always matches what you see.
 */
public abstract class HudModule extends Module {

    // Dawn-ish palette (no blue): translucent dark card, white text.
    private static final int TEXT       = 0xFFFFFFFF;

    private double xFrac;
    private double yFrac;
    private double scale = 1.0;

    protected HudModule(String name, String description, double defaultXFrac, double defaultYFrac) {
        super(name, description, ModuleCategory.HUD);
        this.xFrac = defaultXFrac;
        this.yFrac = defaultYFrac;
    }

    public abstract String[] getLines();

    public double getXFrac() { return xFrac; }
    public double getYFrac() { return yFrac; }
    public double getScale() { return scale; }

    public void setFrac(double x, double y) {
        this.xFrac = Math.max(0.0, Math.min(1.0, x));
        this.yFrac = Math.max(0.0, Math.min(1.0, y));
    }

    public void setScale(double s) {
        this.scale = Math.max(0.25, Math.min(4.0, s));
    }

    public int getX(int screenW) { return (int) Math.round(xFrac * screenW); }
    public int getY(int screenH) { return (int) Math.round(yFrac * screenH); }

    /** Unscaled content width (includes padding). */
    public int rawWidth(TextRenderer tr) {
        int max = 0;
        for (String line : getLines()) max = Math.max(max, tr.getWidth(line));
        return max + 6;
    }

    /** Unscaled content height (includes padding). */
    public int rawHeight() {
        int lines = getLines().length;
        if (lines == 0) lines = 1;
        return lines * 10 + 4;
    }

    /**
     * The final on-screen rectangle {x, y, w, h}: scaled size, clamped fully on
     * screen. Single source of truth for render AND hit-testing.
     */
    public int[] screenRect(int screenW, int screenH, TextRenderer tr) {
        int w = (int) Math.round(rawWidth(tr) * scale);
        int h = (int) Math.round(rawHeight() * scale);
        int x = Math.max(0, Math.min(screenW - w, getX(screenW)));
        int y = Math.max(0, Math.min(screenH - h, getY(screenH)));
        return new int[]{ x, y, w, h };
    }

    // convenience for the HUD editor
    public int getWidth(TextRenderer tr) { return (int) Math.round(rawWidth(tr) * scale); }
    public int getHeight(TextRenderer tr) { return (int) Math.round(rawHeight() * scale); }

    /**
     * Draw a Dawn-style rounded card at the LOCAL origin (0,0), unscaled. The
     * caller has already applied translate + scale. Subclasses override for
     * custom layouts (e.g. Keystrokes).
     */
    protected void drawContent(DrawContext ctx, TextRenderer tr, boolean showAccent) {
        String[] lines = getLines();
        int w = rawWidth(tr);
        int h = rawHeight();

        // plain rectangle (semi-transparent), tight to the text so the drag
        // hitbox (screenRect) matches exactly what's drawn.
        ctx.fill(0, 0, w, h, 0xB4000000);
        if (showAccent) com.divine.smoothmodules.util.RenderUtil.border(ctx, 0, 0, w, h, 0xFFBFC2C8);

        int ty = 3;
        for (String line : lines) {
            ctx.drawTextWithShadow(tr, line, 3, ty, TEXT);
            ty += 10;
        }
    }

    /** Apply position + scale, then draw content. */
    public void render(DrawContext ctx, TextRenderer tr, int screenW, int screenH, boolean showAccent) {
        if (getLines().length == 0) return;
        int[] r = screenRect(screenW, screenH, tr);

        var m = ctx.getMatrices();
        m.pushMatrix();
        m.translate((float) r[0], (float) r[1]);
        m.scale((float) scale, (float) scale);
        drawContent(ctx, tr, showAccent);
        m.popMatrix();
    }
}
