package me.gravityio.goodlib.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

/**
 * Utility functions for Inventory Related Stuff
 */
public class InventoryHelper {

    /**
     * Returns whether you can insert an {@link ItemStack} into a list of {@link DefaultedList slots}.
     * @param slots The {@link DefaultedList slots}
     * @param item The {@link ItemStack} to test for insertion
     * @return Whether the {@link ItemStack} can fit.
     */
    public static boolean canInsertInventory(DefaultedList<Slot> slots, ItemStack item) {
        return canInsertInventory(slots, item, 0);
    }

    /**
     * Returns whether you can insert an {@link ItemStack} into a list of {@link DefaultedList slots}.
     * @param slots The {@link DefaultedList slots}
     * @param item The {@link ItemStack} to test for insertion
     * @param startIndex The Start Index
     * @return Whether the {@link ItemStack} can fit.
     */
    public static boolean canInsertInventory(DefaultedList<Slot> slots, ItemStack item, int startIndex) {
        return canInsertInventory(slots, item, startIndex, slots.size());
    }

    /**
     * Returns whether you can insert an {@link ItemStack} into a list of {@link DefaultedList slots}.
     * @param slots The {@link DefaultedList slots}
     * @param item The {@link ItemStack} to test for insertion
     * @param startIndex The Start Index
     * @param endIndex The End Index
     * @return Whether the {@link ItemStack} can fit.
     */
    public static boolean canInsertInventory(DefaultedList<Slot> slots, ItemStack item, int startIndex, int endIndex) {
        if (item.isEmpty()) return false;
        for (int i = startIndex; i < endIndex; i++) {
            Slot slot = slots.get(i);
            ItemStack other = slot.getStack();
            if (other.isEmpty() && slot.canInsert(item)) {
                return true;
            }
            if (item.isStackable()) {
                if (ItemStack.canCombine(item, other)) {
                    int total = other.getCount() + item.getCount();
                    if (total < item.getMaxCount()) return true;
                    else if (other.getCount() < item.getMaxCount()) return true;
                }
            }
        }
        return false;
    }

}
