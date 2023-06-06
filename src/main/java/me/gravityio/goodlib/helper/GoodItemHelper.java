package me.gravityio.goodlib.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class with some static methods that should have been in the {@link ItemStack} class
 */
public class GoodItemHelper {

    /**
     * Set the Lore of an {@link ItemStack}.
     * @param stack The {@link ItemStack} to set the Lore to.
     * @param loreInput The {@link Text} to set the Lore to.
     */
    public static void setLore(ItemStack stack, Text... loreInput) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound display = GoodNbtHelper.getOrCreate(nbt, ItemStack.DISPLAY_KEY);
        NbtList loreList = GoodNbtHelper.getOrCreate(display, ItemStack.LORE_KEY, NbtList::new, NbtList.class);
        loreList.clear();
        for (Text loreLine : loreInput)
            loreList.add(NbtString.of(Text.Serializer.toJson(loreLine)));
    }

    /**
     * Get the lore of an {@link ItemStack} in the form of a {@link List} of Strings.
     * @param stack The {@link ItemStack} to get the Lore from.
     * @return {@link List} of Strings.
     */
    public static List<String> getLore(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return null;
        NbtCompound display = GoodNbtHelper.get(nbt, ItemStack.DISPLAY_KEY, NbtCompound.class);
        if (display == null) return null;
        NbtList loreList = GoodNbtHelper.get(display, ItemStack.LORE_KEY, NbtList.class);
        if (loreList == null) return null;
        List<String> loreArray = new ArrayList<>();
        for (NbtElement element : loreList)
            loreArray.add(element.asString());
        return loreArray;
    }

    /**
     * Get the lore of an {@link ItemStack} in the form of a {@link String}.
     * @param stack The {@link ItemStack} to get the Lore from.
     * @return {@link String}.
     */
    public static String getLoreAsString(ItemStack stack) {
        List<String> lore = getLore(stack);
        if (lore == null) return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            stringBuilder.append(line);
            if (i != lore.size() - 1) stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param stack
     * @param text
     */
    public static void setHotbarTooltip(ItemStack stack, Text text) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound display = GoodNbtHelper.getOrCreate(nbt, ItemStack.DISPLAY_KEY);
        display.putString("hotbar", Text.Serializer.toJson(text));
    }

    /**
     *
     * @param stack
     * @return
     */
    public static Text getHotbarTooltip(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return null;
        NbtCompound display = GoodNbtHelper.get(nbt, ItemStack.DISPLAY_KEY);
        if (display == null) return null;
        NbtString hotbarTooltip = GoodNbtHelper.get(display, "hotbar", NbtString.class);
        if (hotbarTooltip == null) return null;
        return Text.Serializer.fromLenientJson(hotbarTooltip.asString());
    }

    public static class NbtInventory {
        private static final String BLOCK_ENTITY_KEY = "BlockEntityTag";
        private static final String ITEMS_KEY = "Items";

        /**
         * Returns an ordered inventory from an ItemStack's NBT. <br>
         * Just maps the slots to the actual ItemStack instance. <br><br>
         * I guess I could also just put the slot in the ItemStack's NBT?
         * @param stack {@link ItemStack}
         * @return {@link Map}
         */
        public static Map<Integer, ItemStack> getOrderedInventory(ItemStack stack) {
            NbtList list = getNbtInventory(stack);
            if (list == null) return null;

            Map<Integer, ItemStack> items = new HashMap<>();
            for (int i = 0; i < list.size(); i++)
            {
                NbtCompound itemCompound = list.getCompound(i);
                int slot = itemCompound.getByte("Slot");
                items.put(slot, ItemStack.fromNbt(itemCompound));
            }
            return items;
        }

        /**
         * Returns an unordered inventory from an ItemStack's NBT<br>
         * This is because the NBT Inventory doesn't store it's ItemStacks contiguously, and stores them like so [{id:"a", Slot:20}, {id:"b", Slot:25}] <br><br>
         * Index 0 of the Array would be an Item at Slot: 20 <br>
         * Index 1 of the Array would be an Item at slot: 25 <br>
         * @param stack
         * @return
         */
        public static ItemStack[] getUnorderedInventory(ItemStack stack) {
            NbtList list = getNbtInventory(stack);
            if (list == null) return null;

            ItemStack[] items = new ItemStack[list.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] = ItemStack.fromNbt(list.getCompound(i));
            }
            return items;
        }

        /**
         * Checks if an {@link ItemStack} has Inventory NBT data <br>
         * This only works for Blocks that have Inventory NBT data, it just checks if there's a BlockEntityTag with Items tag inside of it.
         * @param stack The stack
         * @return Is Inventory
         */
        public static boolean isInventory(ItemStack stack) {
            NbtCompound nbt = stack.getNbt();
            if (nbt == null) return false;
            NbtCompound blockEntityTag = (NbtCompound) nbt.get(BLOCK_ENTITY_KEY);
            if (blockEntityTag == null) return false;
            NbtList itemsList = (NbtList) blockEntityTag.get("Items");
            return itemsList != null;
        }

        public static NbtList getNbtInventory(@NotNull ItemStack stack) {
            if (!isInventory(stack)) return null;
            return stack.getNbt().getCompound(BLOCK_ENTITY_KEY).getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        }
    }


}
