package me.gravityio.goodlib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;


/**
 * Handles NBT modifications to a List of NBT Items
 */
public class NbtInventory {

    private final NbtList inventory;

    public NbtInventory(NbtList inventory) {
        this.inventory = inventory;
    }

    public void setStack(int slot, ItemStack stack) {
        NbtCompound stackCompound = stack.writeNbt(new NbtCompound());
        stackCompound.putByte("Slot", (byte) slot);
        int listSlot = this.getListSlot(slot);
        if (listSlot == -1) this.inventory.add(stackCompound);
        else this.inventory.set(listSlot, stackCompound);
    }
    public void removeStack(int slot, int amount) {
        int listSlot = getListSlot(slot);
        if (listSlot != -1) {
            NbtCompound itemCompound = this.inventory.getCompound(listSlot);
            int count = itemCompound.getByte("Count");
            int newCount = count - amount;
            if (newCount <= 0) this.inventory.remove(listSlot);
            else itemCompound.putByte("Count", (byte) newCount);
        }
    }
    public void removeStack(int slot) {
        int listSlot = getListSlot(slot);
        if (listSlot != -1) this.inventory.remove(listSlot);
    }
    public void clear() {
        this.inventory.clear();
    }
    private int getListSlot(int invSlot) {
        for (int i = 0; i < this.inventory.size(); i++)
        {
            NbtCompound itemCompound = this.inventory.getCompound(i);
            if (itemCompound.getByte("Slot") == invSlot) return i;
        }
        return -1;
    }
}
