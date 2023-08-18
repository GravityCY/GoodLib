package me.gravityio.keybindmanager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class KeybindManagerMod implements ClientModInitializer, ClientTickEvents.StartTick {
    @Override
    public void onInitializeClient() {
    ClientTickEvents.START_CLIENT_TICK.register(this);
    }

    @Override
    public void onStartTick(MinecraftClient client) {
    KeybindManager.tick();
    }
}