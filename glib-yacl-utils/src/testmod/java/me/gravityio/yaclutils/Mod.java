package me.gravityio.yaclutils;

import net.fabricmc.api.ModInitializer;

public class Mod implements ModInitializer {
    @Override
    public void onInitialize() {
        YaclUtils.LOGGER.debug("[Debug] Hello from the Test Mod!");
        YaclUtils.LOGGER.info("Hello from the Test Mod!");
    }
}
