package me.gravityio.yaclutils.api;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;

import java.util.Map;

/**
 * Needs to be implemented by a Config
 */
public interface ConfigFrame<T> {
    /**
     * Called before all Options are built
     */
    default void onBeforeBuildOptions(Map<String, Option.Builder<?>> options) {}
    /**
     * Called after all Options are built
     */
    default void onAfterBuildOptions(Map<String, Option<?>> options) {}

    /**
     * Called before a category is built <br>
     * This is literally just the main category right
     * now, since this doesn't support multiple categories with annotations
     */
    default void onBeforeBuildCategory(String category, T defaults, ConfigCategory.Builder builder) {}

    /**
     * Before finishing building the screen. <br><br>
     *
     * Add some custom stuff that the annotations don't support.
     */
    default void onFinishBuilding(T defaults, YetAnotherConfigLib.Builder builder) {}

}
