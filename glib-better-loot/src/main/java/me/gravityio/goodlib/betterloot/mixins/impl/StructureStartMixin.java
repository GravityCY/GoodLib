package me.gravityio.goodlib.betterloot.mixins.impl;

import me.gravityio.goodlib.betterloot.BetterLootMod;
import me.gravityio.goodlib.betterloot.mixins.inter.ILootedStructure;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureStart.class)
public class StructureStartMixin implements ILootedStructure {

  @Shadow @Final private ChunkPos pos;
  private boolean isLooted = false;
  private Identifier id;

  @Override
  public ChunkPos getPos() {
    return this.pos;
  }

  @Override
  public Identifier getId() {
    return this.id;
  }

  @Override
  public void setId(Identifier id) {
    this.id = id;
  }

  @Override
  public boolean isLooted() {
    return this.isLooted;
  }

  @Override
  public void setLooted(boolean isLooted) {
    this.isLooted = isLooted;
  }

  @Inject(method = "fromNbt", at = @At(value = "RETURN", ordinal = 2))
  private static void load(StructureContext context, NbtCompound nbt, long seed, CallbackInfoReturnable<StructureStart> cir) {
    ILootedStructure structure = (ILootedStructure) (Object) cir.getReturnValue();
    structure.setLooted(nbt.getBoolean("isLooted"));
    structure.setId(new Identifier(nbt.getString("id")));
    BetterLootMod.LOGGER.debug("[RETURN] Loading Structure Start {} at {}, Looted {}", structure.getId(), cir.getReturnValue().getPos(), structure.isLooted());
  }

  @Inject(method = "toNbt", at = @At(value = "RETURN", ordinal = 1))
  private void save(StructureContext context, ChunkPos chunkPos, CallbackInfoReturnable<NbtCompound> cir) {
    BetterLootMod.LOGGER.debug("[RETURN] Saving Structure Start {} at {} as Looted {}", this.id, this.pos, this.isLooted);
    NbtCompound nbt = cir.getReturnValue();
    nbt.putBoolean("isLooted", isLooted);
  }
}
