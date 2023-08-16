package me.gravityio.goodlib.helper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public class GoodEnchantHelper {

    public static final String ID_KEY = "id";
    public static final String LEVEL_KEY = "lvl";

    public static int getLevel(Enchantment enchantment, ItemStack stack) {
        NbtList enchantments = stack.getEnchantments();
        if (enchantments.isEmpty()) return -1;
        return getLevel(enchantment, enchantments);
    }

    public static int getLevel(Identifier enchantment, ItemStack stack) {
        NbtList enchantments = stack.getEnchantments();
        if (enchantments.isEmpty()) return -1;
        return getLevel(enchantment, enchantments);
    }

    public static int getLevel(String enchantment, ItemStack stack) {
        NbtList enchantments = stack.getEnchantments();
        if (enchantments.isEmpty()) return -1;
        return getLevel(enchantment, enchantments);
    }

    public static int getLevel(Enchantment enchantment, NbtList nbt) {
        return getLevel(GoodHelper.getIdentifier(enchantment), nbt);
    }

    public static int getLevel(Identifier enchantment, NbtList nbt) {
        return getLevel(enchantment.toString(), nbt);
    }

    public static int getLevel(String enchantment, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            NbtCompound comp = nbt.getCompound(i);
            String id = comp.getString(ID_KEY);
            if (id.equals(enchantment)) return comp.getInt(LEVEL_KEY);
        }
        return -1;
    }

    public static boolean hasEnchantment(Enchantment enchantment, NbtList nbt) {
        return hasEnchantment(GoodHelper.getIdentifier(enchantment), nbt);
    }

    public static boolean hasEnchantment(Identifier enchantmentKey, NbtList nbt) {
        return hasEnchantment(enchantmentKey.toString(), nbt);
    }

    public static boolean hasEnchantment(String enchantmentId, NbtList nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            if (isEnchantment(nbt.getCompound(i), enchantmentId)) return true;
        }
        return false;
    }

    private static boolean isEnchantment(NbtCompound nbt, String id) {
        return nbt.getString(ID_KEY).equals(id);
    }

}
