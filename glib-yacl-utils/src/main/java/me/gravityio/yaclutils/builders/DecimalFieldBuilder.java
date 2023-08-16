package me.gravityio.yaclutils.builders;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.number.DoubleFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import me.gravityio.yaclutils.OptionData;
import me.gravityio.yaclutils.annotations.elements.nums.DecimalField;

public class DecimalFieldBuilder implements AnnotBuilder{

    @Override
    public Option.Builder<?> setup(OptionData data) {
        var type = data.field().getType();
        var field = data.field().getAnnotation(DecimalField.class);
        double min;
        double max;

        if (field != null) {
            min = field.min();
            max = field.max();
        } else {
            min = 0;
            max = 100;
        }

        if (type == float.class || type == Float.class) {
            Option.Builder<Float> builder = GenericBuilder.getDefault(data);
            builder.customController(opt -> new FloatFieldController(opt, (float) min, (float) max))
                    .binding((Float) data.def(), () -> (Float) data.getter().get(), data.setter()::accept);
            return builder;
        } else if(type == double.class || type == Double.class) {
            Option.Builder<Double> builder = GenericBuilder.getDefault(data);
            builder.customController(opt -> new DoubleFieldController(opt, min, max))
                    .binding((Double) data.def(), () -> (Double) data.getter().get(), data.setter()::accept);
            return builder;
        }

        return null;
    }
}
