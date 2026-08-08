package com.divine.smoothmodules.cosmetics;

import net.minecraft.util.Identifier;

/**
 * The 12 selectable wing textures. Holds each variant's id, display name and a
 * preview swatch color (for the Cosmetics picker), plus the currently selected
 * index. The elytra mixin reads {@link #getSelectedTexture()}.
 */
public final class WingVariants {

    private WingVariants() {}

    public static final String[] IDS = {
        "aurora", "ember", "verdant", "nebula", "solar", "frost",
        "void", "prism", "toxic", "sakura", "steel", "vapor"
    };

    public static final String[] NAMES = {
        "Aurora", "Ember", "Verdant", "Nebula", "Solar", "Frost",
        "Void", "Prism", "Toxic", "Sakura", "Steel", "Vapor"
    };

    // Representative swatch color per variant (ARGB) for the picker UI.
    public static final int[] COLORS = {
        0xFF5865F2, 0xFFF26B1F, 0xFF2ECC71, 0xFF9B59F6, 0xFFFFD13B, 0xFF9FD8F5,
        0xFF6B1E2E, 0xFFE24BE2, 0xFFB6FF3B, 0xFFFFC1E3, 0xFFC7CDD3, 0xFFFF71CE
    };

    private static final Identifier[] TEXTURES = new Identifier[IDS.length];
    static {
        for (int i = 0; i < IDS.length; i++) {
            TEXTURES[i] = Identifier.of("smoothmodules", "textures/entity/wings/" + IDS[i] + ".png");
        }
    }

    private static int selected = 0;

    public static int count() { return IDS.length; }

    public static int getSelected() { return selected; }

    public static void setSelected(int index) {
        if (index < 0) index = 0;
        if (index >= IDS.length) index = IDS.length - 1;
        selected = index;
    }

    public static Identifier getSelectedTexture() {
        return TEXTURES[selected];
    }
}
