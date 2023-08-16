package me.gravityio.goodlib.bettercompass.mixin.impl.client;

import me.gravityio.goodlib.bettercompass.BetterCompassHelper;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes compasses point to a structure using the NBT tag `PointsTo:{BlockPos:{}, dimension:""}`
 */
@Mixin(ModelPredicateProviderRegistry.class)
public class CustomCompassDirectionMixin {

    @Inject(method = "method_43220", at = @At("HEAD"), cancellable = true)
    private static void registerCompassModelProvider(ClientWorld world, ItemStack compass, Entity entity, CallbackInfoReturnable<GlobalPos> cir) {
        Boolean random = BetterCompassHelper.getRandom(compass);
        if (random != null && random) {
            cir.setReturnValue(null);
            return;
        }
        GlobalPos pos = BetterCompassHelper.getGlobalPosPoint(compass);
        if (pos == null) return;
        cir.setReturnValue(BetterCompassHelper.getGlobalPosPoint(compass));
    }


}
