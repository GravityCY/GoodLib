package me.gravityio.goodlib.lib;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class BetterRecipeRegistry {
    private static final List<RecipeData> recipeList = new ArrayList<>();

    public static void register(RecipeType<? extends Recipe<?>> recipeType, Recipe<?>... recipes) {
        for (Recipe<?> recipe : recipes)
            recipeList.add(new RecipeData(recipeType, recipe));
    }

    public static List<RecipeData> getRecipes() {
        return recipeList;
    }

    public record RecipeData(RecipeType<? extends Recipe<?>> type, Recipe<?> recipe) {}
}
