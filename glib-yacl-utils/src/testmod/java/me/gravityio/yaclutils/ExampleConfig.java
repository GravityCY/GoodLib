package me.gravityio.yaclutils;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import me.gravityio.yaclutils.annotations.Config;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;
import me.gravityio.yaclutils.annotations.elements.nums.DecimalSlider;
import me.gravityio.yaclutils.annotations.elements.nums.WholeSlider;
import me.gravityio.yaclutils.api.ConfigFrame;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@Config(namespace = "example")
public class ExampleConfig implements ConfigFrame<ExampleConfig> {
    public static final Path path = FabricLoader.getInstance().getConfigDir().resolve("example.json");
    public static GsonConfigInstance<ExampleConfig> GSON = GsonConfigInstance.createBuilder(ExampleConfig.class)
            .setPath(path)
            .build();

    @Override
    public void onBeforeBuildCategory(String category, ExampleConfig defaults, ConfigCategory.Builder builder) {
        System.out.println(defaults.toString());
    }

    // USING DEFAULTS
    @ConfigEntry
    @ScreenOption(index = 0, restart = true)
    public int someDefaultIntValue = 50;
    @ConfigEntry
    @ScreenOption(index = 1, restart = true)
    public float someDefaultDecimalValue = 25.50f;
    @ConfigEntry
    @ScreenOption(index = 2, restart = true)
    public double someDefaultDecimalValueAsADouble = 69.69d;
    @ConfigEntry
    @ScreenOption(index = 3, restart = true)
    public boolean someDefaultBooleanValue = true;
    // CUSTOM
    @ConfigEntry
    @WholeSlider
    @ScreenOption(index = 4, restart = true)
    public int someIntValue = 50;
    @ConfigEntry
    @DecimalSlider
    @ScreenOption(index = 5, restart = true)
    public float someDecimalValue = 25.50f;
    @ConfigEntry
    @DecimalSlider
    @ScreenOption(index = 6, restart = true)
    public double someDecimalValueAsADouble = 69.69d;

}
