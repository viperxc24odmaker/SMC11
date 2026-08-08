package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PotionHudModule extends HudModule {
    public PotionHudModule() { super("Potion HUD", "Active status effects", 0.80, 0.02); }

    @Override
    public String[] getLines() {
        try {
            if (mc.player == null) return new String[0];
            List<String> out = new ArrayList<>();
            for (StatusEffectInstance sei : mc.player.getStatusEffects()) {
                String name = Text.translatable(sei.getEffectType().value().getTranslationKey()).getString();
                int lvl = sei.getAmplifier() + 1;
                String dur;
                if (sei.isInfinite()) {
                    dur = "\u221E";
                } else {
                    int s = sei.getDuration() / 20;
                    dur = (s / 60) + ":" + String.format("%02d", s % 60);
                }
                out.add(name + " " + lvl + "  " + dur);
            }
            if (out.isEmpty()) return new String[]{ "No effects" };
            return out.toArray(new String[0]);
        } catch (Exception e) {
            return new String[]{ "No effects" };
        }
    }
}
