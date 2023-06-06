package me.gravityio.goodlib.mixin.impl.events;

import me.gravityio.goodlib.GoodLib;
import me.gravityio.goodlib.events.GoodEvents;
import me.gravityio.goodlib.helper.GoodInventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.LegacySmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.LegacySmithingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds an event for when something is crafted in the smithing table
 */
public abstract class ForgingScreenHandlerMixins {

    @Mixin(ForgingScreenHandler.class)
    private abstract static class ForgingScreenHandlerMixin extends ScreenHandler {
        protected ForgingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
            super(type, syncId);
        }
        @Inject(method = "quickMove",
                at = @At(value = "INVOKE", target = "net/minecraft/screen/slot/Slot.getStack ()Lnet/minecraft/item/ItemStack;", ordinal = 0))
        private void onBeforeCraft(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
            if (slot != 2 || !GoodInventoryHelper.canInsertInventory(this.slots, this.slots.get(slot).getStack(), 3, 39)) return;
            ForgingScreenHandler self = (ForgingScreenHandler) (Object) this;
            if (self instanceof LegacySmithingScreenHandler smithingScreenHandler) {
                GoodLib.LOGGER.debug("[SmithingScreenHandlerMixin] Player {} quick moved item {} in smithing table output for recipe {}", smithingScreenHandler.currentRecipe, player.getDisplayName().getString(), this.slots.get(slot).getStack());
                GoodEvents.ON_CRAFT.invoker().craft(smithingScreenHandler.currentRecipe, this.slots.get(slot).getStack(), player);
            }
        }
    }

    @Mixin(LegacySmithingScreenHandler.class)
    private static class LegacySmithingScreenHandlerMixin {

        @Shadow
        @Nullable
        public LegacySmithingRecipe currentRecipe;

        @Inject(method = "onTakeOutput", at = @At("HEAD"))
        private void onCraft(PlayerEntity player, ItemStack stack, CallbackInfo info) {
            if (stack.isEmpty()) return;
            GoodLib.LOGGER.debug("[SmithingScreenHandlerMixin] Player {} crafted item {} in smithing table", player.getDisplayName().getString(), stack.getName());
            GoodEvents.ON_CRAFT.invoker().craft(this.currentRecipe, stack, player);
        }
    }
}
