package me.gravityio.yaclutils.api;

import me.gravityio.yaclutils.Helper;
import me.gravityio.yaclutils.annotations.Config;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
* Data Containing everything the option needs to be created
*/
public record OptionData(ScreenOption option, String namespace, String id,  @Nullable String keyLabel, @Nullable String keyDescription, Object def, Field field, Supplier<Object> getter, Consumer<Object> setter) {

    public Text getLabel(String format) {
        return Text.translatable(this.getLabelKey(format));
    }

    public Text getDescription(String format) {
        return Text.translatable(this.getDescriptionKey(format));
    }

    public String getLabelKey(String format) {
        if (keyLabel != null)
            return keyLabel;
        return this.getNamespacedKey(format);
    }

    public String getDescriptionKey(String format) {
        if (keyDescription != null)
            return keyDescription;
        return this.getNamespacedKey(format);
    }

    public String getNamespacedKey(String format) {
        return format.formatted(namespace, id);
    }

    public static OptionData fromField(Object defConfig, Object configInstance, Field field) {
        var conclass = configInstance.getClass();

        var methods = conclass.getDeclaredMethods();

        var config = conclass.getAnnotation(Config.class);
        var namespace = config.namespace();

        ScreenOption optionAnnot = field.getAnnotation(ScreenOption.class);
        var id = optionAnnot.id();
        if (id.isEmpty()) id = null;
        var keyLabel = optionAnnot.labelKey();
        if (keyLabel.isEmpty()) keyLabel = null;
        var keyDescription = optionAnnot.descriptionKey();
        if (keyDescription.isEmpty()) keyDescription = null;

        if (id == null) id = field.getName();

        var def = Helper.doGetField(defConfig, field);
        var getter = Helper.getGetterMethod(methods, id);
        var setter = Helper.getSetterMethod(methods, id);
        Supplier<Object> supplier = getter == null ? () -> Helper.doGetField(configInstance, field) : Helper.getSupplier(configInstance, getter);
        Consumer<Object> consumer = setter == null ? (v) -> Helper.doSetField(configInstance, field, v) : Helper.getConsumer(configInstance, setter);
        return new OptionData(optionAnnot, namespace, id, keyLabel, keyDescription, def, field, supplier, consumer);
    }
}