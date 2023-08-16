package me.gravityio.goodlib.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GoodEvents {

  /**
   * Whenever a key is pressed regardless of whether your even in minecraft, just grabs key's globally. <br><br>
   *
   * Cancel the Event with ActionResult.FAIL in order to stop Minecraft from processing the key.
   */
  @Environment(EnvType.CLIENT)
  public static Event<OnKeyPressed> ON_KEY_PRESSED = EventFactory.createArrayBacked(OnKeyPressed.class,
    listeners -> (window, button, scancode, action, mods) -> {
      for (OnKeyPressed listener : listeners) {
        ActionResult result = listener.pressed(window, button, scancode, action, mods);
        if (result != ActionResult.PASS)
          return result;
      }
      return ActionResult.PASS;
  });

  /**
   * Whenever a translation is missing raise an event in order for listeners to return a fallback option for the translation. <br><br>
   *
   * Return NULL whenever a translationKey is not the one you want.
   */
  @Environment(EnvType.CLIENT)
  public static final Event<MissingTranslation> ON_MISSING_TRANSLATION = EventFactory.createArrayBacked(MissingTranslation.class,
    listeners -> (key) -> {
      for (MissingTranslation listener : listeners) {
        String newText = listener.onMissingTranslation(key);
        if (newText != null)
          return newText;
      }
      return null;
  });

  /**
   * Whenever the mouse is scrolled regardless of whether your even in minecraft, just grabs scrolls globally. <br><br>
   *
   * Cancel the Event with ActionResult.FAIL in order to stop Minecraft from processing the scroll.
   */
  @Environment(EnvType.CLIENT)
  public static Event<OnMouseScrolled> ON_MOUSE_SCROLLED = EventFactory.createArrayBacked(OnMouseScrolled.class,
    listeners -> (window, horizontal, vertical) -> {
      for (OnMouseScrolled listener : listeners) {
        ActionResult result = listener.scroll(window, horizontal, vertical);
        if (result != ActionResult.PASS)
          return result;
      }
      return ActionResult.PASS;
  });

  /**
   * Whenever the mouse is pressed regardless of whether your even in minecraft, just grabs mouse presses globally. <br><br>
   *
   * Cancel the Event with ActionResult.FAIL in order to stop Minecraft from processing the press.
   */
  @Environment(EnvType.CLIENT)
  public static Event<OnMousePressed> ON_MOUSE_PRESSED = EventFactory.createArrayBacked(OnMousePressed.class,
    listeners -> (window, button, action, mods) -> {
      for (OnMousePressed listener : listeners) {
        ActionResult result = listener.pressed(window, button, action, mods);
        if (result != ActionResult.PASS)
          return result;
      }
      return ActionResult.PASS;
  });

  /**
   * Whenever the mouse is pressed regardless of whether your even in minecraft, just grabs mouse presses globally. <br><br>
   *
   * This event executes AFTER Minecraft has processed the press.
   */
  @Environment(EnvType.CLIENT)
  public static Event<OnMousePressedAfter> ON_AFTER_MOUSE_PRESSED = EventFactory.createArrayBacked(OnMousePressedAfter.class,
    listeners -> (window, button, action, mods) -> {
      for (OnMousePressedAfter listener : listeners)
        listener.pressed(window, button, action, mods);
  });

  /**
   * Whenever a player crafts currently only works for the smithing table
   */
  public static Event<OnCraft> ON_CRAFT = EventFactory.createArrayBacked(OnCraft.class,
    listeners -> (recipe, stack, player) -> {
      for (OnCraft listener : listeners)
        listener.craft(recipe, stack, player);
  });

  /**
   * Whenever a player uses an item
   */
  public static Event<OnItemUse> ON_ITEM_USE = EventFactory.createArrayBacked(OnItemUse.class,
    listeners -> (world, player, hand) -> {
      for (OnItemUse listener : listeners) {
        TypedActionResult<ItemStack> result = listener.onUse(world, player, hand);
        if (result != null && result.getResult() != ActionResult.PASS)
          return result;
      }
      return null;
  });


  public interface MissingTranslation {
    String onMissingTranslation(String key);
  }

  public interface OnMouseScrolled {
    ActionResult scroll(long window, double horizontal, double vertical);
  }

  public interface OnMousePressed {
    ActionResult pressed(long window, int button, int action, int mods);
  }

  public interface OnMousePressedAfter {
    void pressed(long window, int button, int action, int mods);
  }

  public interface OnKeyPressed {
    ActionResult pressed(long window, int key, int scancode, int action, int mods);
  }

  public interface OnCraft {
    void craft(Recipe<?> recipe, ItemStack item, PlayerEntity player);
  }

  public interface OnItemUse {
    TypedActionResult<ItemStack> onUse(World world, PlayerEntity player, Hand hand);
  }

}
