package me.gravityio.yaclutils.builders;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import me.gravityio.yaclutils.OptionData;
import me.gravityio.yaclutils.annotations.elements.nums.WholeSlider;

import java.util.function.Function;

public class WholeSliderTransformer extends GenericTransformer<Integer> {

    @Override
    public Function<Option<Integer>, Controller<Integer>> getController(OptionData data) {
        var wholeAnnot = data.field().getAnnotation(WholeSlider.class);
        int min;
        int max;
        int interval;
        if (wholeAnnot != null) {
            min = wholeAnnot.min();
            max = wholeAnnot.max();
            interval = wholeAnnot.interval();
        } else {
            min = 0;
            max = 100;
            interval = 1;
        }

        return opt -> new IntegerSliderController(opt, min, max, interval);
    }


}
