package me.gravityio.yaclutils.builders;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import me.gravityio.yaclutils.OptionData;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.gravityio.yaclutils.Helper.DEFAULT_DESCRIPTION_FORMAT;
import static me.gravityio.yaclutils.Helper.DEFAULT_LABEL_FORMAT;

public abstract class GenericTransformer<T> implements OptionTransformer {

    public static <T> Option.Builder<T> getDefault(OptionData data) {
        Option.Builder<T> builder = Option.createBuilder();
        var label = data.getLabel(DEFAULT_LABEL_FORMAT);
        var description = data.getDescription(DEFAULT_DESCRIPTION_FORMAT);
        builder.name(label).description(OptionDescription.of(description));
        if (data.option().restart()) {
            builder.flag(OptionFlag.GAME_RESTART);
        }
        for (ScreenOption.OptionFlag flag : data.option().flags()) {
            builder.flag(flag.yaclFlag);
        }
        return builder;
    }

    @Override
    public Option.Builder<?> setup(OptionData data) {
        Option.Builder<T> builder = getDefault(data);
        builder.customController(this.getController(data))
                .binding((T) data.def(), (Supplier<T>) data.getter(), (Consumer<T>) data.setter());
        this.onSetup(builder);
        return builder;
    }

    public void onSetup(Option.Builder<T> builder) { }
    public abstract Function<Option<T>, Controller<T>> getController(OptionData data);
}
