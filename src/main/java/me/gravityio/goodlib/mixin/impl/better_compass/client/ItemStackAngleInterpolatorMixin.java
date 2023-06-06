package me.gravityio.goodlib.mixin.impl.better_compass.client;

import me.gravityio.goodlib.mixin.interfaces.IAngleInterpolatorAccessor;
import net.minecraft.client.item.CompassAnglePredicateProvider.AngleInterpolator;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackAngleInterpolatorMixin implements IAngleInterpolatorAccessor {
    AngleInterpolator interpolator;
    @Override
    public AngleInterpolator getInterpolator() {
        return this.interpolator;
    }

    @Override
    public void setInterpolator(AngleInterpolator interpolator) {
        this.interpolator = interpolator;
    }
}
