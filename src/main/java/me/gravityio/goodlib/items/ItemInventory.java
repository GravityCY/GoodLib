package me.gravityio.goodlib.items;

import me.gravityio.goodlib.helper.GoodItemHelper;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;


/**
 *      An Inventory based off of an ItemStack, for example Shulker Boxes <br>
 *      Gives the ability to proxy all Inventory functions to the NBT data of the ItemStack <br>
 *      This is kind of a raw class because it does not do any checks for if the inventory that the ItemStack is in is still there, meaning if you had an ItemStack in an inventory and that inventory's block get's destroyed this inventory would still be open <br><br>
 *      If you want to use a class that will do checks for whether the invnetory I recommend you extend this class and just
 *      <pre>{@code
 *      @override
 *      private boolean canPlayerUse(PlayerEntity entity) {
 *
 *      }}</pre>
 *
 */
@SuppressWarnings("ALL")
public class ItemInventory extends SimpleInventory {

    protected final ItemStack inventoryStack;
    protected final NbtInventory nbtInventory;

    /**
     * @param inventoryStack The Stack the inventory is going to based off
     * @param size The size of the inventory
     * @param canPlayerUse A Function that will determine whether the inventory should close
     */
    public ItemInventory(ItemStack inventoryStack, int size) {
        super(size);
        this.inventoryStack = inventoryStack;
        this.nbtInventory = new NbtInventory(GoodItemHelper.NbtInventory.getNbtInventory(inventoryStack));
        GoodItemHelper.NbtInventory.getOrderedInventory(inventoryStack).forEach(super.stacks::set);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) nbtInventory.removeStack(slot);
        else nbtInventory.setStack(slot, stack);
        super.setStack(slot, stack);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        nbtInventory.removeStack(slot, amount);
        return super.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        nbtInventory.removeStack(slot);
        return super.removeStack(slot);
    }

    @Override
    public void clear() {
        nbtInventory.clear();
        super.clear();
    }
}
