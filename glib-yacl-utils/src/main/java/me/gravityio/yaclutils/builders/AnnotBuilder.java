package me.gravityio.yaclutils.builders;

import dev.isxander.yacl3.api.Option;
import me.gravityio.yaclutils.OptionData;

public interface AnnotBuilder {
    Option.Builder<?> setup(OptionData data);
}
