package me.gravityio.yaclutils;

import net.fabricmc.api.ModInitializer;

public class Mod implements ModInitializer {
    @Override
    public void onInitialize() {
        YaclUtils.DEBUG = true;
        YaclUtils.DEBUG("[Debug] Hello from the Test Mod!");
    }
}
