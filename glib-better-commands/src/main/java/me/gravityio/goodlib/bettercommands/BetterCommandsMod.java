package me.gravityio.goodlib.bettercommands;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterCommandsMod implements ModInitializer {
    public static final String MOD_ID = "bettercommands";
    public static Logger LOGGER = LoggerFactory.getLogger("glib");

    @Override
    public void onInitialize() {
        CommandProcessor.init();
    }
}
