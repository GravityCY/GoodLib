package me.gravityio.yaclutils;

import me.gravityio.yaclutils.annotations.elements.BooleanToggle;
import me.gravityio.yaclutils.annotations.elements.nums.DecimalField;
import me.gravityio.yaclutils.annotations.elements.nums.DecimalSlider;
import me.gravityio.yaclutils.annotations.elements.nums.WholeField;
import me.gravityio.yaclutils.annotations.elements.nums.WholeSlider;
import me.gravityio.yaclutils.builders.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BuilderRegistry {
    public static Map<Class<? extends Annotation>, OptionTransformer> transformers = new HashMap<>();
    public static Map<Class<?>, OptionTransformer> defaults = new HashMap<>();

    /**
     * Registers a transformer for an annotation
     */
    public static void registerTransformer(Class<? extends Annotation> annotation, TransformerType<?> builderType) {
        transformers.put(annotation, builderType.builder());
    }

    /**
     * Registers a default Option Transformer for 1 field type
     */
    public static void registerDefault(Class<?> fieldType, TransformerType<?> builderType) {
        defaults.put(fieldType, builderType.builder());
    }

    /**
     * Registers a default Option Transformer for 2 field types
     */

    private static void registerDefault(Class<?> fieldType, Class<?> fieldType1, TransformerType<?> builderType) {
        registerDefault(fieldType, builderType);
        registerDefault(fieldType1, builderType);
    }

    /**
     * Gets an Option Transformer from an annotation
     */
    public static OptionTransformer getTransformer(Class<? extends Annotation> annotation) {
        return transformers.get(annotation);
    }

    /**
     * Tries to get an Option Transformer for a field from either the fields
     * annotations or the default one depending on the class of the field
     */

    public static OptionTransformer getFromField(Field field) {
        for (Class<? extends Annotation> annotation : transformers.keySet()) {
            if (!field.isAnnotationPresent(annotation)) continue;
            return BuilderRegistry.getTransformer(annotation);
        }
        return BuilderRegistry.getDefault(field.getType());
    }

    /**
     * Gets a default Option Transformer for a field
     */
    public static OptionTransformer getDefault(Class<?> type) {
        return defaults.get(type);
    }

    /**
     * Whether a field is valid
     */
    public static boolean isValidField(Field field) {
        return BuilderRegistry.getFromField(field) != null;
    }

    static {
        registerDefault(Integer.class, int.class, TransformerType.WHOLE_FIELD);
        registerDefault(Float.class, float.class, TransformerType.DECIMAL_FIELD);
        registerDefault(Double.class, double.class, TransformerType.DECIMAL_FIELD);
        registerDefault(Boolean.class, boolean.class, TransformerType.BOOLEAN);

        registerTransformer(WholeSlider.class, TransformerType.WHOLE_SLIDER);
        registerTransformer(DecimalSlider.class, TransformerType.DECIMAL_SLIDER);
        registerTransformer(WholeField.class, TransformerType.WHOLE_FIELD);
        registerTransformer(DecimalField.class, TransformerType.DECIMAL_FIELD);
        registerTransformer(BooleanToggle.class, TransformerType.BOOLEAN);
    }


}
