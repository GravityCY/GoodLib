package me.gravityio.goodlib.mixin.impl;

import me.gravityio.goodlib.events.GoodEvents;
import net.minecraft.client.Mouse;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"), cancellable = true)
    private void onScrollAny(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (GoodEvents.ON_MOUSE_SCROLLED.invoker().scroll(window, horizontal, vertical) == ActionResult.FAIL)
            ci.cancel();
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMousePressedAny(long window, int button, int action, int mods, CallbackInfo ci) {
        if (GoodEvents.ON_MOUSE_PRESSED.invoker().pressed(window, button, action, mods) == ActionResult.FAIL)
            ci.cancel();
    }

    @Inject(method = "onMouseButton", at = @At("TAIL"))
    private void onMousePressedAnyAfter(long window, int button, int action, int mods, CallbackInfo ci) {
        GoodEvents.ON_AFTER_MOUSE_PRESSED.invoker().pressed(window, button, action, mods);
    }

}
