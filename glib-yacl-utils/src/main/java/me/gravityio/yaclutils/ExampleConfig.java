package me.gravityio.yaclutils;

import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.ConfigInstance;
import dev.isxander.yacl3.config.GsonConfigInstance;
import me.gravityio.yaclutils.annotations.Config;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;
import me.gravityio.yaclutils.annotations.elements.nums.DecimalSlider;
import me.gravityio.yaclutils.annotations.elements.nums.WholeSlider;

import java.nio.file.Path;

@Config(namespace = "example")
public class ExampleConfig implements ConfigScreenFrame{
    public static ConfigInstance<ExampleConfig> GSON = GsonConfigInstance.createBuilder(ExampleConfig.class)
            .setPath(Path.of("config", "example.json"))
            .build();

    // USING DEFAULTS
    @ConfigEntry
    @ScreenOption(index = 0)
    public int someDefaultIntValue = 50;
    @ConfigEntry
    @ScreenOption(index = 1)
    public float someDefaultDecimalValue = 25.50f;
    @ConfigEntry
    @ScreenOption(index = 2)
    public double someDefaultDecimalValueAsADouble = 69.69d;
    @ConfigEntry
    @ScreenOption(index = 3)
    public boolean someDefaultBooleanValue = true;
    // CUSTOM
    @ConfigEntry
    @WholeSlider
    @ScreenOption(index = 4)
    public int someIntValue = 50;
    @ConfigEntry
    @DecimalSlider
    @ScreenOption(index = 5)
    public float someDecimalValue = 25.50f;
    @ConfigEntry
    @DecimalSlider
    @ScreenOption(index = 6)
    public double someDecimalValueAsADouble = 69.69d;
}
