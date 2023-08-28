package me.gravityio.yaclutils.transformers;

import me.gravityio.yaclutils.transformers.nums.DecimalFieldTransformer;
import me.gravityio.yaclutils.transformers.nums.DecimalSliderTransformer;
import me.gravityio.yaclutils.transformers.nums.WholeFieldTransformer;
import me.gravityio.yaclutils.transformers.nums.WholeSliderTransformer;

public record TransformerType<T extends OptionTransformer>(T builder) {
    public static final TransformerType<StringTransformer> STRING = new TransformerType<>(new StringTransformer());
    public static TransformerType<BooleanToggleTransformer> BOOLEAN = new TransformerType<>(new BooleanToggleTransformer());
    public static TransformerType<WholeFieldTransformer> WHOLE_FIELD = new TransformerType<>(new WholeFieldTransformer());
    public static final TransformerType<DecimalFieldTransformer> DECIMAL_FIELD = new TransformerType<>(new DecimalFieldTransformer());
    public static final TransformerType<WholeSliderTransformer> WHOLE_SLIDER = new TransformerType<>(new WholeSliderTransformer());
    public static TransformerType<DecimalSliderTransformer> DECIMAL_SLIDER = new TransformerType<>(new DecimalSliderTransformer());
}
