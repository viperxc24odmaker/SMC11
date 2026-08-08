package com.divine.smoothmodules;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmoothModules implements ModInitializer {

    public static final String MOD_ID = "smoothmodules";
    public static final Logger LOGGER = LoggerFactory.getLogger("Smooth Modules");

    @Override
    public void onInitialize() {
        LOGGER.info("Smooth Modules loading (server/common side).");
    }
}
