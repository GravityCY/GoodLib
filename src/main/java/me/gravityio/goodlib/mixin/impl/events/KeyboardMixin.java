package me.gravityio.goodlib.mixin.impl.events;

import me.gravityio.goodlib.events.GoodEvents;
import net.minecraft.client.Keyboard;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyAny(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (GoodEvents.ON_KEY_PRESSED.invoker().pressed(window, key, scancode, action, modifiers) == ActionResult.FAIL)
            ci.cancel();
    }
}
