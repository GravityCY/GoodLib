package me.gravityio.goodlib.mixin.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import me.gravityio.goodlib.lib.BetterRecipeRegistry;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "INVOKE",
                     target = "java/util/Map.entrySet()Ljava/util/Set;",
                     ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onApply(Map<Identifier, JsonElement> map,
                         ResourceManager resourceManager,
                         Profiler profiler, CallbackInfo ci,
                         Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2,
                         ImmutableMap.Builder<Identifier, Recipe<?>> builder) {
        BetterRecipeRegistry.getRecipes().forEach(recipeData -> {
            RecipeType<? extends Recipe<?>> type = recipeData.type();
            Recipe<?> recipe = recipeData.recipe();
            map2.computeIfAbsent(type, recipeType -> ImmutableMap.builder()).put(recipe.getId(), recipe);
            builder.put(recipe.getId(), recipe);
        });
    }

}
