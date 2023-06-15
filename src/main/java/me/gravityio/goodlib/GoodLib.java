package me.gravityio.goodlib;

import me.gravityio.goodlib.dev.DevThings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoodLib implements ModInitializer {
    public static final String MOD_ID = "goodlib";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            DevThings.init();
        for (GoodInitializer goodlib : FabricLoader.getInstance().getEntrypoints("goodlib", GoodInitializer.class)) {
            goodlib.onInitialized();
        }
    }
}
