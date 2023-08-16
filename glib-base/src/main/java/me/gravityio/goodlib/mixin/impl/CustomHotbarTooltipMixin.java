package me.gravityio.goodlib.mixin.impl;

import me.gravityio.goodlib.helper.GoodItemHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Adds support for custom hotbar tooltips <br><br>
 * -When you switch an Item in your hotbar it shows a tooltip of that item <br>
 * -This makes it so that if the Item has an NBT tag it will render that text instead of the display name of the item
 *
 */
@Mixin(InGameHud.class)
public class CustomHotbarTooltipMixin {
    @Shadow private ItemStack currentStack;

    @ModifyVariable(
            method = "renderHeldItemTooltip",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private MutableText onRenderCustomTooltip(MutableText original) {
        MutableText tooltip = (MutableText) GoodItemHelper.getHotbarTooltip(this.currentStack);
        return tooltip == null ? original : tooltip;
    }
}
