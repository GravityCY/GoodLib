package me.gravityio.goodlib.client;

import me.gravityio.goodlib.lib.keybinds.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class GoodClientLib implements ClientModInitializer, ClientTickEvents.StartTick {
  @Override
  public void onInitializeClient() {
    ClientTickEvents.START_CLIENT_TICK.register(this);
  }

  @Override
  public void onStartTick(MinecraftClient client) {
    KeybindManager.tick();
  }
}
