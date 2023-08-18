package me.gravityio.goodlib.armrenderables;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;

public interface ArmRenderable {
    Item[] getItems();
    void renderArm(MatrixStack stack, Hand hand);
    void renderItem(MatrixStack stack, Hand hand);
}
