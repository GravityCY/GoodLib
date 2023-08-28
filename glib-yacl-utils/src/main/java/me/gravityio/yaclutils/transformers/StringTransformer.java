package me.gravityio.yaclutils.transformers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.StringController;
import me.gravityio.yaclutils.api.OptionData;

import java.util.function.Function;

public class StringTransformer extends GenericTransformer<String> {
    @Override
    public Function<Option<String>, Controller<String>> getController(OptionData data) {
        return StringController::new;
    }
}
