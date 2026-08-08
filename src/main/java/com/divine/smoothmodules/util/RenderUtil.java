package com.divine.smoothmodules.util;

import net.minecraft.client.gui.DrawContext;

/**
 * Small rendering helpers. DrawContext#drawBorder was removed in 1.21.11, so
 * we draw a 1px outline manually with four fill() calls (VulkanMod-safe).
 */
public final class RenderUtil {

    private RenderUtil() {}

    public static void border(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);             // top
        ctx.fill(x, y + h - 1, x + w, y + h, color);     // bottom
        ctx.fill(x, y, x + 1, y + h, color);             // left
        ctx.fill(x + w - 1, y, x + w, y + h, color);     // right
    }
}
