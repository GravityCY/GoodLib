package me.gravityio.goodlib.betterloot.mixins.inter;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

public interface ILootedStructure {
  ChunkPos getPos();
  Identifier getId();
  void setId(Identifier id);
  boolean isLooted();
  void setLooted(boolean isLooted);
}
