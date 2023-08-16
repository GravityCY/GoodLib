package me.gravityio.goodlib.helper;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;

import java.util.Map;

/**
 * Uncategorized Helper Stuff
 */
public class GoodHelper {

    /**
     * Gets a global position relative to a global position based off its rotation and a local offset <br>
     * <pre>{@code
     * position = 10, 0, 0;
     * rotation = 0, 0;
     * offset = 0, 0, 5;
     * return = 15, 0, 0
     * }</pre>
     * @param position Global Position
     * @param rotation Rotation of the Position
     * @param offset Offset based off of the Position and Rotation
     * @return Local Position
     */
    public static Vec3d toAbsolutePos(Vec3d position, Vec2f rotation, Vec3d offset) {
        float f = MathHelper.cos((rotation.y + 90.0f) * ((float)Math.PI / 180));
        float g = MathHelper.sin((rotation.y + 90.0f) * ((float)Math.PI / 180));
        float h = MathHelper.cos(-rotation.x * ((float)Math.PI / 180));
        float i = MathHelper.sin(-rotation.x * ((float)Math.PI / 180));
        float j = MathHelper.cos((-rotation.x + 90.0f) * ((float)Math.PI / 180));
        float k = MathHelper.sin((-rotation.x + 90.0f) * ((float)Math.PI / 180));
        Vec3d vec3d2 = new Vec3d(f * h, i, g * h);
        Vec3d vec3d3 = new Vec3d(f * j, k, g * j);
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0);
        double d = vec3d2.x * offset.z + vec3d3.x * offset.y + vec3d4.x * offset.x;
        double e = vec3d2.y * offset.z + vec3d3.y * offset.y + vec3d4.y * offset.x;
        double l = vec3d2.z * offset.z + vec3d3.z * offset.y + vec3d4.z * offset.x;
        return new Vec3d(position.x + d, position.y + e, position.z + l);
    }

    public static BlockHitResult raycast(PlayerEntity player, float maxDistance) {
        return (BlockHitResult) player.raycast(maxDistance, 0, false);
    }

    public static StructureStart getStructureStart(ServerWorld world, BlockPos pos) {
        StructureAccessor accessor = world.getStructureAccessor();
        for (Map.Entry<Structure, LongSet> entry : accessor.getStructureReferences(pos).entrySet()) {
            for (Long l : entry.getValue()) {
                ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(new ChunkPos(l), world.getBottomSectionCoord());
                StructureStart start = world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS).getStructureStart(entry.getKey());
                if (start == null || !start.hasChildren() || !start.getBoundingBox().contains(pos)) continue;
                return start;
            }
        }
        return null;
    }

    // Biome
    public static Identifier getIdentifier(Biome biome, World world) {
        return world.getRegistryManager().get(RegistryKeys.BIOME).getId(biome);
    }

    // Structure
    public static Identifier getIdentifier(StructureStart start, World world) {
        return getIdentifier(start.getStructure(), world);
    }

    public static Identifier getIdentifier(Structure structure, World world) {
        return world.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(structure);
    }

    // Item
    public static Identifier getIdentifier(ItemStack stack) {
        return getIdentifier(stack.getItem());
    }

    public static Identifier getIdentifier(Item item) {
        return Registries.ITEM.getId(item);
    }

    // Enchant
    public static Identifier getIdentifier(Enchantment enchantment) {
        return Registries.ENCHANTMENT.getId(enchantment);
    }

}
