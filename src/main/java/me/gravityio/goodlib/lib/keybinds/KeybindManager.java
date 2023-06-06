package me.gravityio.goodlib.lib.keybinds;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import java.util.ArrayList;
import java.util.List;

public class KeybindManager {
  private static final List<KeybindWrapper> binds = new ArrayList<>();
  public static <T extends KeybindWrapper> T register(T bind) {
    binds.add(bind);
    return bind;
  }

  public static void init() {
    for (KeybindWrapper wrapped : binds)
      KeyBindingHelper.registerKeyBinding(wrapped.bind);
  }

  public static void tick() {
    for (KeybindWrapper bind : binds) {
      boolean prevDown = bind.down;
      bind.down = bind.bind.isPressed();
      while (bind.bind.wasPressed()) {
        bind.whilePressed();
      }
      if (bind.down && !prevDown)
        bind.onPressed();
      if (prevDown && !bind.down)
        bind.onRelease();
    }
  }
}
