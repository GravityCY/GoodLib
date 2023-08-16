package me.gravityio.goodlib.bettercompass.mixin.inter;

import net.minecraft.client.item.CompassAnglePredicateProvider.AngleInterpolator;

public interface IAngleInterpolatorAccessor {

    AngleInterpolator getInterpolator();
    void setInterpolator(AngleInterpolator interpolator);
}
