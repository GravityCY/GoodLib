package me.gravityio.yaclutils.transformers.nums;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import me.gravityio.yaclutils.api.OptionData;
import me.gravityio.yaclutils.annotations.elements.nums.DecimalSlider;
import me.gravityio.yaclutils.transformers.GenericTransformer;
import me.gravityio.yaclutils.transformers.OptionTransformer;

public class DecimalSliderTransformer implements OptionTransformer {
    @Override
    public Option.Builder<?> setup(OptionData data) {
        var type = data.field().getType();
        var slider = data.field().getAnnotation(DecimalSlider.class);
        double min;
        double max;
        double interval;

        if (slider != null) {
            min = slider.min();
            max = slider.max();
            interval = slider.interval();
        } else {
            min = 0;
            max = 100;
            interval = 1;
        }

        if (type == float.class || type == Float.class) {
            Option.Builder<Float> builder = GenericTransformer.getDefault(data);
            builder.customController(opt -> new FloatSliderController(opt, (float) min, (float) max, (float) interval))
                    .binding((Float) data.def(), () -> (Float) data.getter().get(), data.setter()::accept);

            return builder;
        } else if(type == double.class || type == Double.class) {
            Option.Builder<Double> builder = GenericTransformer.getDefault(data);
            builder.customController(opt -> new DoubleSliderController(opt, min, max, interval))
                    .binding((Double) data.def(), () -> (Double) data.getter().get(), data.setter()::accept);
            return builder;
        }

        return null;
    }
}
