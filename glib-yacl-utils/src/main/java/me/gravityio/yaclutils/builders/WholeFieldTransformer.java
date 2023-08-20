package me.gravityio.yaclutils.builders;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import me.gravityio.yaclutils.OptionData;
import me.gravityio.yaclutils.annotations.elements.nums.WholeField;

import java.util.function.Function;

public class WholeFieldTransformer extends GenericTransformer<Integer> {
    @Override
    public Function<Option<Integer>, Controller<Integer>> getController(OptionData data) {
        var wholeAnnot = data.field().getAnnotation(WholeField.class);
        int min;
        int max;

        if (wholeAnnot != null) {
            min = wholeAnnot.min();
            max = wholeAnnot.max();
        } else {
            min = Integer.MIN_VALUE;
            max = Integer.MAX_VALUE;
        }

        return opt -> new IntegerFieldController(opt, min, max);
    }
}
