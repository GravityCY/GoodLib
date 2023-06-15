package me.gravityio.goodlib.dev;

import me.gravityio.goodlib.lib.arm_renderable.ArmRenderable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.joml.Quaternionf;

public class ArmRenderableTest implements ArmRenderable {
    private static final Item[] items = new Item[] { Items.STONE };
    @Override
    public Item[] getItems() {
        return items;
    }

    @Override
    public void renderArm(MatrixStack stack, Hand hand) {
        stack.translate(0f, 0f, 0.2f);
    }

    @Override
    public void renderItem(MatrixStack stack, Hand hand) {
        stack.scale(0.7f, 0.7f, 1);
        stack.translate(0.4f, 0f, -1.2f);
    }
}
