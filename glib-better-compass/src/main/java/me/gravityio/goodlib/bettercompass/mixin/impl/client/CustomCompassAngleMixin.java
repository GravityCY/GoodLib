package me.gravityio.goodlib.bettercompass.mixin.impl.client;

import net.minecraft.client.item.CompassAnglePredicateProvider;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(CompassAnglePredicateProvider.class)
public abstract class CustomCompassAngleMixin {

    private final Random random = new Random();

//    @ModifyReturnValue(method = "getAngle", at = @At(value = "RETURN", ordinal = 1))
//    private float setAngleRandom(float angle, ItemStack compass, ClientWorld world, int seed, Entity _e) {
//        if (!BetterCompass.hasPointStrength(compass)) return angle;
//
//        IAngleInterpolatorAccessor accessor = (IAngleInterpolatorAccessor) (Object) compass;
//        CompassAnglePredicateProvider.AngleInterpolator stackInterpolator = accessor.getInterpolator();
//        if (stackInterpolator == null)
//            accessor.setInterpolator(stackInterpolator = new CompassAnglePredicateProvider.AngleInterpolator());
//        if (stackInterpolator.shouldUpdate(world.getTime())) {
//            double strength = BetterCompass.getPointStrength(compass);
//            double d = random.nextDouble(0.5d);
//            if (random.nextBoolean()) d = -d;
//            stackInterpolator.update(world.getTime(), angle +  d - (strength * d));
//        }
//        return (float) stackInterpolator.value;
//    }
}
