package me.gravityio.yaclutils.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.gravityio.yaclutils.ConfigScreenBuilder;
import me.gravityio.yaclutils.ExampleConfig;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return p -> ConfigScreenBuilder.getScreen(ExampleConfig.GSON, p);
    }
}
