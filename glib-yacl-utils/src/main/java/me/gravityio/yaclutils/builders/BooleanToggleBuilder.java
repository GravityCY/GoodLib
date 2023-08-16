package me.gravityio.yaclutils.builders;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import me.gravityio.yaclutils.OptionData;
import me.gravityio.yaclutils.annotations.elements.BooleanToggle;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static me.gravityio.yaclutils.Helper.DEFAULT_NAMESPACED_FORMAT;

public class BooleanToggleBuilder extends GenericBuilder<Boolean> {
    @Override
    public Function<Option<Boolean>, Controller<Boolean>> getController(OptionData data) {
        var booleanAnnot = data.field().getAnnotation(BooleanToggle.class);
        Function<Boolean, Text> formatter = getBooleanTextFunction(data, booleanAnnot);

        return opt -> {
            BooleanController controller;
            if (formatter != null) {
                controller = new BooleanController(opt, formatter, true);
            } else {
                controller = new BooleanController(opt, true);
            }
            return controller;
        };
    }

    @Nullable
    private static Function<Boolean, Text> getBooleanTextFunction(OptionData data, BooleanToggle booleanAnnot) {
        Function<Boolean, Text> formatter;
        if (booleanAnnot != null) {
            var useCustomFormatter = booleanAnnot.useCustomFormatter();

            if (useCustomFormatter) {
                var namespacedKey = data.getNamespacedKey(DEFAULT_NAMESPACED_FORMAT);
                var on = namespacedKey + ".on";
                var off = namespacedKey + ".off";
                formatter = v -> v ? Text.translatable(on) : Text.translatable(off);
            } else {
                formatter = BooleanController.ON_OFF_FORMATTER;
            }
        } else {
            formatter = null;
        }
        return formatter;
    }
}
