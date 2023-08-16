package me.gravityio.yaclutils.annotations.elements.nums;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Slider for Decimal Numbers
 * 0.0 -> 33.33 -> 66.66 -> 100.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DecimalSlider {
    double min() default 0;
    double max() default 100;

    double interval() default 1;
}
