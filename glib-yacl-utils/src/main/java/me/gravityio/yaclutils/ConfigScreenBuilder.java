package me.gravityio.yaclutils;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.ConfigInstance;
import me.gravityio.yaclutils.annotations.Config;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;
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
        return builder.setup(data);
    }

    /**
     * Creates the config screen
     */
    public static Screen getScreen(ConfigInstance<?> instance, Screen parent) {
        var config = (ConfigScreenFrame) instance.getConfig();
        Class<?> conclass = config.getClass();
        if (!conclass.isAnnotationPresent(Config.class)) return null;
        var configAnnot = conclass.getAnnotation(Config.class);
        var namespace = configAnnot.namespace();

        return YetAnotherConfigLib.create(instance, (d, c, builder) -> {
            var category = ConfigCategory.createBuilder()
                    .name(Text.translatable("yacl.%s.title".formatted(namespace)));

            Map<String, Option.Builder<?>> unbuiltOptionsMap = new LinkedHashMap<>();
            Map<String, Option<?>> builtOptionsMap = new LinkedHashMap<>();
            var orderedOptionFields = getOrderedOptionFields(conclass);
            for (Field optionField : orderedOptionFields) {
                var data = OptionData.fromField(d, config, optionField);
                var option = getOption(data);
                unbuiltOptionsMap.put(data.id(), option);
            }

            config.onBeforeBuildOptions(unbuiltOptionsMap);
            for (Map.Entry<String, Option.Builder<?>> entry : unbuiltOptionsMap.entrySet()) {
                builtOptionsMap.put(entry.getKey(), entry.getValue().build());
            }
            config.onAfterBuildOptions(builtOptionsMap);
            for (Option<?> value : builtOptionsMap.values())
                category.option(value);


            builder.title(Text.translatable("yacl.%s.title".formatted(namespace)));
            builder.category(category.build());

            return builder;
        }).generateScreen(parent);
    }
}
