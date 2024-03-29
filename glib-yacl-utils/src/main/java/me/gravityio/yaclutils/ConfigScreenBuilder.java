package me.gravityio.yaclutils;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.ConfigInstance;
import me.gravityio.yaclutils.annotations.Config;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;
import me.gravityio.yaclutils.api.BuilderRegistry;
import me.gravityio.yaclutils.api.ConfigFrame;
import me.gravityio.yaclutils.api.OptionData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Builds a Config Screen for YACL based off of some basic annotations
 */
public class ConfigScreenBuilder {

    private static boolean isValidField(Field field) {
        return field.isAnnotationPresent(ScreenOption.class);
    }
    
    private static List<Field> getOrderedOptionFields(Class<?> conclass) {
        List<Field> out = new ArrayList<>();
        var fields = conclass.getDeclaredFields();
        for (Field field : fields) {
            if (!isValidField(field)) continue;
            out.add(field);
        }
        out.sort(Comparator.comparingInt(field -> {
            ScreenOption optionAnnot = field.getAnnotation(ScreenOption.class);
            return optionAnnot.index();
        }));
        return out;
    }

    /**
     * Gets the actual YACL Option using the OptionData
     */
    private static Option.Builder<?> getOption(OptionData data) {
        var builder = BuilderRegistry.getFromField(data.field());
        if (builder == null) return null;
        YaclUtils.DEBUG("Setting up Option for field {}", data.field());
        return builder.setup(data);
    }

    /**
     * Creates the config screen
     */
    public static <T extends ConfigFrame<T>> Screen getScreen(ConfigInstance<T> instance, Screen parent) {
        var config = instance.getConfig();
        Class<?> conclass = config.getClass();
        if (!conclass.isAnnotationPresent(Config.class)) {
            throw new MissingConfigAnnotationException("The class is missing the @Config annotation!");
        }

        ConfigFrame<T> configFrame = config;

        var configAnnot = conclass.getAnnotation(Config.class);
        var namespace = configAnnot.namespace();
        YaclUtils.DEBUG("[ConfigScreenBuilder] Creating Config Screen for class {}, with namespace {}", conclass, namespace);
        YaclUtils.DEBUG("[ConfigScreenBuilder] Config Frame {}", configFrame.getClass());
        YaclUtils.DEBUG("[ConfigScreenBuilder] Config Instance {}", instance.getClass());

        return YetAnotherConfigLib.create(instance, (yaclDefaults, yaclConfig, yaclBuilder) -> {
            yaclBuilder.title(Text.translatable("yacl.%s.title".formatted(namespace)));
            var category = ConfigCategory.createBuilder()
                    .name(Text.translatable("yacl.%s.title".formatted(namespace)));

            Map<String, Option.Builder<?>> unbuiltOptionsMap = new LinkedHashMap<>();
            Map<String, Option<?>> builtOptionsMap = new LinkedHashMap<>();
            var orderedOptionFields = getOrderedOptionFields(conclass);
            for (Field optionField : orderedOptionFields) {
                var data = OptionData.fromField(yaclDefaults, configFrame, optionField);
                var option = getOption(data);
                unbuiltOptionsMap.put(data.id(), option);
            }

            configFrame.onBeforeBuildOptions(unbuiltOptionsMap);
            for (Map.Entry<String, Option.Builder<?>> entry : unbuiltOptionsMap.entrySet()) {
                builtOptionsMap.put(entry.getKey(), entry.getValue().build());
            }
            configFrame.onAfterBuildOptions(builtOptionsMap);
            for (Option<?> value : builtOptionsMap.values())
                category.option(value);

            configFrame.onBeforeBuildCategory("main", yaclDefaults, category);
            yaclBuilder.category(category.build());

            configFrame.onFinishBuilding(yaclDefaults, yaclBuilder);
            return yaclBuilder;
        }).generateScreen(parent);
    }

    public static class MissingConfigAnnotationException extends RuntimeException {
        public MissingConfigAnnotationException(String message) {
            super(message);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }

        @Override
        public void printStackTrace() {
            System.out.println("MissingAnnotationException: " + getMessage());
            System.out.println("Please add the @Config annotation to your class.");
        }
    }

}
