package me.gravityio.goodlib.dev;

import me.gravityio.goodlib.lib.BetterLootRegistry;
import me.gravityio.goodlib.lib.arm_renderable.ArmRenderableRegistry;
import net.minecraft.util.Identifier;

public class DevThings {

    public static void init() {
        CommandProcessor.init();
        CommandProcessor.register(DevCommands.class);
        BetterLootRegistry.registerLoot(BetterLootRegistry.ALL, new Identifier("goodlib", "chests/dev"));
        ArmRenderableRegistry.register(new ArmRenderableTest());
    }

}
