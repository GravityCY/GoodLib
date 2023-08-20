package me.gravityio.yaclutils.annotations.elements.nums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DecimalField {
    double min() default Float.MIN_VALUE;
    double max() default Float.MAX_VALUE;
}
