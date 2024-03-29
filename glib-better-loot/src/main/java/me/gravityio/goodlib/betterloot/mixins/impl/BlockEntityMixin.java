package me.gravityio.goodlib.betterloot.mixins.impl;

import me.gravityio.goodlib.betterloot.BetterLootMod;
import me.gravityio.goodlib.betterloot.mixins.inter.ILootedStructure;
import me.gravityio.goodlib.betterloot.mixins.inter.IStructureLootable;
import me.gravityio.goodlib.helper.GoodHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Makes BlockEntities get which structure they belong to in order to be able to tell if a chest with a loot table has been looted before or not within the structure
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {
    @Inject(method = "setWorld", at = @At("TAIL"))
    private void setStructure(World world, CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        if (world.isClient || !(self instanceof IStructureLootable blockStructureLootTable) || !blockStructureLootTable.hasLootable()) return;

        ServerWorld serverWorld = (ServerWorld) world;
        StructureStart start = GoodHelper.getStructureStart(serverWorld, self.getPos());
        if (start == null) {
            BetterLootMod.LOGGER.debug("Couldn't find a structure for Pos {}", self.getPos());
            return;
        }
        ILootedStructure structure = (ILootedStructure) (Object) start;
        if (structure.getId() == null)
            structure.setId(serverWorld.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(start.getStructure()));
        BetterLootMod.LOGGER.debug("Setting Structure of Pos {} to {}, {}", self.getPos(), structure.getId(), start);
        blockStructureLootTable.setStructure((ILootedStructure) (Object) start);
    }
}
