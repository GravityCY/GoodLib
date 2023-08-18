package me.gravityio.goodlib.armrenderables.mixin.impl;

import me.gravityio.goodlib.armrenderables.ArmRenderable;
import me.gravityio.goodlib.armrenderables.ArmRenderableRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A Mixin that allows for rendering items with the arm also rendering in order to make it look like items are being held by the arm
 */
@Mixin(HeldItemRenderer.class)
public abstract class ArmRenderableImplMixin {
    @Shadow protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "net/minecraft/client/network/AbstractClientPlayerEntity.isUsingItem ()Z", ordinal = 1))
    private void renderArmRenderable(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        ArmRenderable armRenderable = ArmRenderableRegistry.getArmRenderable(item);
        if (armRenderable == null) return;
        armRenderable.renderArm(matrices, hand);
        this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite());
        matrices.pop();
        matrices.push();
        armRenderable.renderItem(matrices, hand);
    }
}
