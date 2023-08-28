package me.gravityio.yaclutils.transformers;

import dev.isxander.yacl3.api.Option;
import me.gravityio.yaclutils.api.OptionData;

/**
 * An OptionTransformer is responsible for setting up an Option for YACL
 * using either an annotation or a field. <br><br>
 *
 * For example, all fields annotated with the @WholeField annotation have to somehow
 * be transformed into an Option for YACL to display, therefore... An Option Transformer
 */
public interface OptionTransformer {
    Option.Builder<?> setup(OptionData data);
}
