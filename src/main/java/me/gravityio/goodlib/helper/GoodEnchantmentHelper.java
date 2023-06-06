package me.gravityio.goodlib.helper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class GoodEnchantmentHelper extends net.minecraft.enchantment.EnchantmentHelper {

    public static final String ID_KEY = "id";
    public static final String LEVEL_KEY = "lvl";

    /**
     * Checks if an {@link NbtList} has a specific {@link Enchantment}.
     * @param enchantment The {@link Enchantment} to look for.
     * @param nbt The {@link NbtList} to search through.
     * @return If contains
     */
    public static boolean hasEnchantment(Enchantment enchantment, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            if (isEnchantment(nbt.getCompound(i), enchantment)) return true;
        }
        return false;
    }

    /**
     * Checks if an {@link NbtList}'s enchantments contains an id matching to the {@link Identifier} argument.
     * @param enchantmentKey The {@link Identifier} to look for.
     * @param nbt The {@link NbtList} to search through.
     * @return If contains
     */
    public static boolean hasEnchantment(Identifier enchantmentKey, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            if (isEnchantment(nbt.getCompound(i), enchantmentKey)) return true;
        }
        return false;
    }

    /**
     * Checks if an {@link NbtList}'s enchantments contains an id matching to the {@param String string } argument.
     * @param enchantmentId The id {@link String string} to look for.
     * @param nbt The {@link NbtList} to look through.
     * @return If contains
     */
    public static boolean hasEnchantment(String enchantmentId, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            if (isEnchantment(nbt.getCompound(i), enchantmentId)) return true;
        }
        return false;
    }

    private static boolean isEnchantment(NbtCompound nbt, Enchantment enchantment) {
        if (!nbt.contains(ID_KEY)) return false;
        return Objects.equals(Registries.ENCHANTMENT.get(new Identifier(nbt.getString(ID_KEY))), enchantment);
    }

    private static boolean isEnchantment(NbtCompound nbt, Identifier id) {
        return new Identifier(nbt.getString(ID_KEY)).equals(id);
    }

    private static boolean isEnchantment(NbtCompound nbt, String id) {
        return nbt.getString(ID_KEY).equals(id);
    }


}
