package me.gravityio.yaclutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks this class for producing a config screen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {
    /**
     * The translation key namespace of the config <br><br>
     *
     * Used to generate the translation key of all config options in the form of: <br>
     * yacl.%namespace%.%some_option_id%
     */
    String namespace();
}
