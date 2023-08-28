package me.gravityio.yaclutils.annotations.elements;

import dev.isxander.yacl3.api.OptionFlag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Marks this field as an option to be present in the config screen,
 * will automatically generate a Translatable Text using the fields names, can be overriden by
 * specifying keyLabel or keyDescription
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ScreenOption {
    /**
     * Used For:
     * <ul>
     *     <li>Connecting getters and setters with this field</li>
     *     <li>
     *         Automatically generating translatable keys in the form of:
     *         <ul>
     *         <li>Label: yacl.$namespace.$id.label</li>
     *         <li>Description: yacl.$namespace.$id.description</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    String id() default "";

    /**
     * Overrides the automatically generated Translatable Label Key to use this explicit key
     */
    String labelKey() default "";
    /**
     * Overrides the automatically generated Translatable Description Key to use this explicit key
     */
    String descriptionKey() default "";

    /**
     * Used to add the fields a specific order to the GUI<br><br>
     *
     * Lowest index gets displayed first.
     */
    int index();

    /**
     * Whether the game needs to restart for this option
     */
    boolean restart() default false;

    /**
     * Some extra optional YACl flags for this option
     */
    OptionFlag[] flags() default {};

    enum OptionFlag {
        RESTART(dev.isxander.yacl3.api.OptionFlag.GAME_RESTART),
        RELOAD_CHUNKS(dev.isxander.yacl3.api.OptionFlag.RELOAD_CHUNKS),
        WORLD_RENDER_UPDATE(dev.isxander.yacl3.api.OptionFlag.WORLD_RENDER_UPDATE),
        ASSET_RELOAD(dev.isxander.yacl3.api.OptionFlag.ASSET_RELOAD);
        public final dev.isxander.yacl3.api.OptionFlag yaclFlag;

        OptionFlag(dev.isxander.yacl3.api.OptionFlag yaclFlag) {
            this.yaclFlag = yaclFlag;
        }

    }
}
