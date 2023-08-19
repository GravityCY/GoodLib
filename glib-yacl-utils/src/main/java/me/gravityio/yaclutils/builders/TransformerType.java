package me.gravityio.yaclutils.builders;

public record TransformerType<T extends OptionTransformer>(T builder) {
    public static TransformerType<BooleanToggleBuilder> BOOLEAN = new TransformerType<>(new BooleanToggleBuilder());
    public static TransformerType<WholeFieldTransformer> WHOLE_FIELD = new TransformerType<>(new WholeFieldTransformer());
    public static final TransformerType<DecimalFieldTransformer> DECIMAL_FIELD = new TransformerType<>(new DecimalFieldTransformer());
    public static final TransformerType<WholeSliderTransformer> WHOLE_SLIDER = new TransformerType<>(new WholeSliderTransformer());
    public static TransformerType<DecimalSliderTransformer> DECIMAL_SLIDER = new TransformerType<>(new DecimalSliderTransformer());
}
