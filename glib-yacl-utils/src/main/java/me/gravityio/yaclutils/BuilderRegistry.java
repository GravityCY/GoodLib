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
    public static Map<Class<? extends Annotation>, AnnotBuilder> builders = new HashMap<>();
    public static Map<Class<?>, AnnotBuilder> defaults = new HashMap<>();

    public static void register(Class<? extends Annotation> annotation, AnnotBuilder builder) {
        builders.put(annotation, builder);
    }

    public static AnnotBuilder get(Class<? extends Annotation> annotation) {
        return builders.get(annotation);
    }

    public static AnnotBuilder getFromField(Field field) {
        for (Class<? extends Annotation> annotation : builders.keySet()) {
            if (!field.isAnnotationPresent(annotation)) continue;
            return BuilderRegistry.get(annotation);
        }
        return BuilderRegistry.getDefault(field.getType());
    }

    public static AnnotBuilder getDefault(Class<?> type) {
        return defaults.get(type);
    }

    public static boolean isValidField(Field field) {
        return BuilderRegistry.getFromField(field) != null;
    }

    static {
        defaults.put(int.class, new WholeFieldBuilder());
        defaults.put(Float.class, new DecimalFieldBuilder());
        defaults.put(float.class, new DecimalFieldBuilder());
        defaults.put(Double.class, new DecimalFieldBuilder());
        defaults.put(double.class, new DecimalFieldBuilder());
        defaults.put(Boolean.class, new BooleanToggleBuilder());
        defaults.put(boolean.class, new BooleanToggleBuilder());

        builders.put(WholeSlider.class, new WholeSliderBuilder());
        builders.put(DecimalSlider.class, new DecimalSliderBuilder());
        builders.put(WholeField.class, new WholeFieldBuilder());
        builders.put(DecimalField.class, new DecimalFieldBuilder());
        builders.put(BooleanToggle.class, new BooleanToggleBuilder());
    }
}
