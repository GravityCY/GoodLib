package me.gravityio.yaclutils.api;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;

import java.util.Map;

/**
 * Needs to be implemented by a Config
 */
public interface ConfigFrame {
    default void onBeforeBuildOptions(Map<String, Option.Builder<?>> options) {}
    default void onAfterBuildOptions(Map<String, Option<?>> options) {}
    default void onBeforeBuildCategory(String category, ConfigCategory.Builder builder) {}
    default void onFinishBuilding(YetAnotherConfigLib.Builder builder) {}

}
