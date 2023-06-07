package me.gravityio.goodlib.lib.keybinds;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KeybindManager {
  private static final List<KeybindWrapper> binds = new ArrayList<>();
  public static <T extends KeybindWrapper> @NotNull T register(@NotNull T wrapped) {
    KeyBindingHelper.registerKeyBinding(wrapped.bind);
    binds.add(wrapped);
    return wrapped;
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
