package me.gravityio.goodlib.mixin.impl.better_loot;

import me.gravityio.goodlib.GoodLib;
import me.gravityio.goodlib.lib.BetterLootRegistry;
import me.gravityio.goodlib.mixin.interfaces.better_loot.ILootedStructure;
import me.gravityio.goodlib.mixin.interfaces.better_loot.IStructureLootable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin extends LockableContainerBlockEntity implements IStructureLootable {
    private ILootedStructure structure;
    @Shadow @Nullable protected Identifier lootTableId;
    @Shadow public abstract void setStack(int slot, ItemStack stack);
    @Shadow protected long lootTableSeed;

    protected LootableContainerBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public boolean hasLootable() {
        return this.lootTableId != null;
    }

    @Override
    public void setStructure(ILootedStructure structure) {
        this.structure = structure;
    }

    @Override
    public ILootedStructure getStructure() {
        return this.structure;
    }

    @Inject(method = "checkLootInteraction", at = @At("HEAD"))
    private void addCustomLoot(PlayerEntity player, CallbackInfo ci) {
        if (this.lootTableId == null || this.structure == null || this.structure.isLooted()) return;
        GoodLib.LOGGER.debug("Setting Chest at {} to looted.", this.pos);
        this.world.getChunk(structure.getPos().x, structure.getPos().z, ChunkStatus.STRUCTURE_STARTS).setNeedsSaving(true);
        this.structure.setLooted(true);
        for (Identifier lootTableKey : BetterLootRegistry.getLoot(this.structure.getId())) {
            LootTable lootTable = this.world.getServer().getLootManager().getTable(lootTableKey);
            LootContext.Builder builder = new LootContext.Builder((ServerWorld) this.world).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos)).random(this.lootTableSeed);
            if (player != null) {
                builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
            }
            lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }
    }
}
