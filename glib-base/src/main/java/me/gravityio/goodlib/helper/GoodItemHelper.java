package me.gravityio.goodlib.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.item.ItemStack.DISPLAY_KEY;
import static net.minecraft.item.ItemStack.LORE_KEY;

/**
 * A class with some static methods that should have been in the {@link ItemStack} class
 */
public class GoodItemHelper {

    public static final String HOTBAR_KEY = "hotbar";
    public static final String LORE_PATH = "%s.%s".formatted(DISPLAY_KEY, LORE_KEY);

    /**
     * Clears lore
     * @param stack
     */
    public static void clearLore(ItemStack stack) {
        NbtList loreList = getLoreNbt(stack);
        if (loreList != null)
            loreList.clear();
    }

    /**
     * Set the Lore of an {@link ItemStack}.
     * @param stack The {@link ItemStack} to set the Lore to.
     * @param loreInput The {@link Text} to set the Lore to.
     */
    public static void setLore(ItemStack stack, Text... loreInput) {
        NbtList loreList = getOrCreateLoreNbt(stack);
        for (int i = 0; i < loreInput.length; i++) {
            Text input = loreInput[i];
            if (input == null) continue;
            NbtString nbtString = NbtString.of(Text.Serializer.toJson(input));
            if (i < loreList.size())
                loreList.set(i, nbtString);
            else
                loreList.add(nbtString);
        }
    }

    /**
     * Set the lore
     * @param stack
     * @param loreInput
     * @param index
     */
    public static void setLore(ItemStack stack, Text loreInput, int index) {
        Text[] texts = new Text[index + 1];
        texts[index] = loreInput;
        setLore(stack, texts);
    }

    /**
     * Allows you to 1 Text Object that uses newlines to separate the Text into their own separate lore lines <br><br>
     * Usage Example:
     * <pre>{@code setLoreFromText(stack, Text.translatable("gui.modid.name\ngui.modid.description"))}</pre>
     * Compared to
     * <pre>{@code setLore(stack, Text.translatable("gui.modid.name"), Text.translatable("gui.modid.description")}</pre>
     * @param stack
     * @param loreInput
     */
    public static void setLoreFromText(ItemStack stack, Text loreInput) {
        String input = loreInput.getString();
        List<Text> loreList = new ArrayList<>();
        boolean translatable = loreInput.getContent() instanceof TranslatableTextContent;
        for (String split : input.split("\n")) {
            Text temp;
            if (translatable) {
                temp = Text.translatable(split);
            } else {
                temp = Text.literal(split);
            }
            loreList.add(temp);
        }
        setLore(stack, loreList.toArray(new Text[0]));
    }

    /**
     * Get the lore of an {@link ItemStack} in the form of a {@link List} of Strings.
     * @param stack The {@link ItemStack} to get the Lore from.
     * @return {@link List} of Strings.
     */
    public static List<String> getLore(ItemStack stack) {
        NbtList loreList = getLoreNbt(stack);
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
            Text text = Text.Serializer.fromJson(line);
            stringBuilder.append(text.getString());
            if (i != lore.size() - 1) stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * Gets the NbtList of the lore of an ItemStack
     * @param stack
     * @return
     */
    public static @Nullable NbtList getLoreNbt(@NotNull ItemStack stack) {
        return GoodNbtHelper.getDeep(stack.getNbt(), NbtList.class, DISPLAY_KEY, LORE_KEY);
    }

    /**
     * Gets or creates an NbtList for the lore of an ItemStack
     * @param stack
     * @return
     */
    public static @Nullable NbtList getOrCreateLoreNbt(@NotNull ItemStack stack) {
        return GoodNbtHelper.getOrCreateDeep(stack.getOrCreateNbt(), NbtList::new, NbtList.class,
                DISPLAY_KEY, LORE_KEY);
    }

    /**
     *
     * @param stack
     * @param text
     */
//    @Command
    public static void setHotbarTooltip(@NotNull final ItemStack stack, @NotNull final Text text) {
        NbtCompound display = GoodNbtHelper.getOrCreate(stack.getOrCreateNbt(), DISPLAY_KEY);
        display.putString(HOTBAR_KEY, Text.Serializer.toJson(text));
    }

    /**
     *
     * @param stack
     * @return
     */
//    @Command
    public static Text getHotbarTooltip(@NotNull final ItemStack stack) {
        NbtString hotbarTooltip = GoodNbtHelper.getDeep(stack.getNbt(), NbtString.class, DISPLAY_KEY, HOTBAR_KEY);
        if (hotbarTooltip == null) return null;
        return Text.Serializer.fromLenientJson(hotbarTooltip.asString());
    }

    /**
     * Clears the hotbar tooltip
     * @param stack
     */
    public static void clearHotbarTooltip(@NotNull final ItemStack stack) {
        NbtCompound displayKey = GoodNbtHelper.getDeep(stack.getNbt(), NbtCompound.class, DISPLAY_KEY);
        if (displayKey == null) return;
        displayKey.remove(HOTBAR_KEY);
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
        public static @Nullable Map<Integer, ItemStack> getOrderedInventory(@NotNull final ItemStack stack) {
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
        public static @Nullable ItemStack[] getUnorderedInventory(@NotNull final ItemStack stack) {
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
        public static boolean isInventory(final ItemStack stack) {
            return GoodNbtHelper.containsDeep(stack.getNbt(), NbtElement.LIST_TYPE, BLOCK_ENTITY_KEY, ITEMS_KEY);
        }

        public static @Nullable NbtList getNbtInventory(@NotNull final ItemStack stack) {
            return GoodNbtHelper.getDeep(stack.getNbt(), NbtList.class, BLOCK_ENTITY_KEY, ITEMS_KEY);
        }

        public static @NotNull NbtList getOrCreateNbtInventory(@NotNull final ItemStack stack) {
            return GoodNbtHelper.getOrCreateDeep(stack.getOrCreateNbt(), NbtList::new, NbtList.class, BLOCK_ENTITY_KEY, ITEMS_KEY);
        }

    }
}
