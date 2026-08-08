package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.HudModule;
import net.minecraft.client.network.PlayerListEntry;

public class PingModule extends HudModule {
    public PingModule() { super("Ping", "Your latency to the current server", 0.01, 0.52); }

    @Override
    public String[] getLines() {
        try {
            if (mc.getNetworkHandler() == null || mc.player == null) return new String[]{ "Ping --" };
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry == null) return new String[]{ "Ping --" };
            return new String[]{ "Ping " + entry.getLatency() + "ms" };
        } catch (Exception e) {
            return new String[]{ "Ping --" };
        }
    }
}
