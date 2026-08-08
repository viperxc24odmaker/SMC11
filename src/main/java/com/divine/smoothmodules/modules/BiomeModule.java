package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;

public class BiomeModule extends HudModule {
    public BiomeModule() { super("Biome", "Name of the biome you are standing in", 0.01, 0.48); }

    @Override
    public String[] getLines() {
        try {
            if (mc.world == null || mc.player == null) return new String[]{ "Biome --" };
            String path = mc.world.getBiome(mc.player.getBlockPos())
                    .getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("unknown");
            // prettify: "old_growth_taiga" -> "Old Growth Taiga"
            String[] parts = path.split("_");
            StringBuilder sb = new StringBuilder();
            for (String p : parts) {
                if (p.isEmpty()) continue;
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(' ');
            }
            return new String[]{ sb.toString().trim() };
        } catch (Exception e) {
            return new String[]{ "Biome --" };
        }
    }
}
