package me.gravityio.goodlib.lib.arm_renderable;

import me.gravityio.goodlib.GoodLib;
import me.gravityio.goodlib.mixin.impl.ArmRenderableImplMixin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A registry that shouldn't really exist because fabric has custom renderers for items,
 * but I didn't know at the time and I don't want to get rid of it now<br><br>
 * Oh, and this is a class to register {@link ArmRenderable ArmRenderables}
 * that will be used to render items that want to also render the players' arm
 * @see ArmRenderableImplMixin ArmRenderableMixin - the mixin that implements this
 */
public class ArmRenderableRegistry {
    private static final List<ArmRenderable> armRenderableList = new ArrayList<>();
    public static <T extends ArmRenderable> T register(T armRenderable) {
        GoodLib.LOGGER.debug("[ArmRenderableRegistry] Registering Arm Renderable");
        armRenderableList.add(armRenderable);
        return armRenderable;
    }

    public static ArmRenderable getArmRenderable(ItemStack itemStack) {
        Item item = itemStack.getItem();
        for (ArmRenderable armRenderable : armRenderableList) {
            for (Item armItem : armRenderable.getItems()) {
                if (armItem.equals(item)) return armRenderable;
            }
        }
        return null;
    }
}
