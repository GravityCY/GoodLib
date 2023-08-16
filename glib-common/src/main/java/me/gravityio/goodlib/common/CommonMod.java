package me.gravityio.goodlib.common;

import me.gravityio.goodlib.bettercommands.CommandProcessor;
import me.gravityio.goodlib.common.dev.DevCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CommonMod implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CommandProcessor.init();
            CommandProcessor.register(DevCommands.class);
        }
    }
}
