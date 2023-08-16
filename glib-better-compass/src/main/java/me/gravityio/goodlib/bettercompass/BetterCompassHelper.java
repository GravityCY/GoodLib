package me.gravityio.goodlib.bettercompass;

import me.gravityio.goodlib.GoodLib;
import me.gravityio.goodlib.helper.GoodNbtHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utilities that helps with the tweak that makes compasses point to any block position
 */
public class BetterCompassHelper {

    public static final String POINTS_TO = "PointsTo";
    public static final String BLOCK_POS = "BlockPos";
    public static final String DIMENSION = "dimension";
    public static final String STRENGTH = "strength";
    public static final String RANDOM = "random";

    /**
     * Returns whether the {@link GlobalPos} has `PointsTo.BlockPos` and `PointsTo.dimension` NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static @Nullable GlobalPos getGlobalPosPoint(@NotNull ItemStack compass) {
        if (!BetterCompassHelper.isPointingAtPosition(compass)) return null;
        NbtCompound pointsToComp = compass.getNbt().getCompound(POINTS_TO);
        BlockPos blockPos = NbtHelper.toBlockPos(pointsToComp.getCompound(BLOCK_POS));
        NbtElement dimensionElem = pointsToComp.get(DIMENSION);

        Optional<RegistryKey<World>> world = World.CODEC.parse(NbtOps.INSTANCE, dimensionElem).result();
        return world.map(worldRegistryKey -> GlobalPos.create(worldRegistryKey, blockPos)).orElse(null);
    }

    /**
     * Gets or creates the `PointsTo` {@link NbtCompound}
     * @param compass {@link ItemStack}
     * @return {@link NbtCompound}
     */
    public static @NotNull NbtCompound getOrCreatePointsTo(@NotNull ItemStack compass) {
        return GoodNbtHelper.getOrCreate(compass.getOrCreateNbt(), POINTS_TO);
    }

    /**
     * Sets `PointsTo.dimension` and `PointsTo.BlockPos`
     * @param compass {@link ItemStack}
     * @param pos {@link BlockPos}
     * @param dimension {@link Identifier}
     */
    public static void setPoint(@NotNull ItemStack compass, @NotNull BlockPos pos, @NotNull Identifier dimension) {
        BetterCompassHelper.setPointPosition(compass, pos);
        BetterCompassHelper.setPointDimension(compass, dimension);
    }

    /**
     * Gets the `PointsTo` {@link NbtCompound}
     * @param compass {@link ItemStack}
     * @return {@link NbtCompound}
     */
    public static @Nullable NbtCompound getPointsTo(@NotNull ItemStack compass) {
        if (!isPointing(compass)) return null;
        return compass.getNbt().getCompound(POINTS_TO);
    }

    /**
     * Returns whether the compass has `PointsTo` NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointing(@NotNull ItemStack compass) {
        NbtCompound nbt = compass.getNbt();
        return nbt != null && nbt.contains(POINTS_TO, NbtElement.COMPOUND_TYPE);
    }

    /**
     * Sets the `PointsTo.dimension`
     * @param compass {@link ItemStack}
     * @param dimension {@link Identifier}
     */
    public static void setPointDimension(@NotNull ItemStack compass, @NotNull Identifier dimension) {
        GoodLib.LOGGER.debug("[CompassUtils] Setting compass point dimension to: {}", dimension);
        BetterCompassHelper.getOrCreatePointsTo(compass).putString(DIMENSION, dimension.toString());
    }

    /**
     * Gets the `PointsTo.dimension` {@link Identifier}
     * @param compass {@link ItemStack}
     * @return {@link Identifier}
     */
    public static Identifier getPointDimension(@NotNull ItemStack compass) {
        if (!BetterCompassHelper.isPointing(compass)) return null;
        return new Identifier(BetterCompassHelper.getPointsTo(compass).getString(DIMENSION));
    }

    /**
     * Returns whether the compass has `PointsTo` NBT and `PointsTo.dimension`
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointingAtDimension(@NotNull ItemStack compass) {
        return isPointing(compass) && getPointsTo(compass).contains(DIMENSION, NbtElement.STRING_TYPE);
    }

    /**
     * Sets the `PointsTo.BlockPos`
     * @param compass {@link ItemStack}
     * @param pos {@link BlockPos}
     */
    public static void setPointPosition(@NotNull ItemStack compass, @NotNull BlockPos pos) {
        GoodLib.LOGGER.debug("[CompassUtils] Setting compass point position to: {}", pos);
        BetterCompassHelper.getOrCreatePointsTo(compass).put(BLOCK_POS, NbtHelper.fromBlockPos(pos));
    }

    /**
     * Gets the `PointsTo.BlockPos` {@link BlockPos}
     * @param compass {@link ItemStack}
     * @return {@link BlockPos}
     */
    public static @Nullable BlockPos getPointPosition(@NotNull ItemStack compass) {
        if (!BetterCompassHelper.isPointingAtPosition(compass)) return null;
        return NbtHelper.toBlockPos(getPointsTo(compass).getCompound(BLOCK_POS));
    }

    /**
     * Returns whether the compass has `PointsTo.BlockPos` and `PointsTo.dimension` NBT
     * @param compass {@link ItemStack}
     * @return {@link Boolean}
     */
    public static boolean isPointingAtPosition(@NotNull ItemStack compass) {
        if (!BetterCompassHelper.isPointing(compass)) return false;
        NbtCompound pointsTo = compass.getSubNbt(POINTS_TO);
        return pointsTo.contains(BLOCK_POS) && pointsTo.contains(DIMENSION, NbtElement.STRING_TYPE);
    }

    public static void setPointStrength(@NotNull ItemStack compass, double strength) {
        GoodLib.LOGGER.debug("[CompassUtils] Setting compass point strength to: {}", strength);
        BetterCompassHelper.getOrCreatePointsTo(compass).putDouble(STRENGTH, strength);
    }

    public static double getPointStrength(@NotNull ItemStack compass) {
        if (!BetterCompassHelper.isPointing(compass)) return -1;
        return BetterCompassHelper.getPointsTo(compass).getDouble(STRENGTH);
    }

    public static boolean hasPointStrength(@NotNull ItemStack compass) {
        return BetterCompassHelper.isPointing(compass) && BetterCompassHelper.getPointsTo(compass).contains(STRENGTH, NbtElement.DOUBLE_TYPE);
    }

    public static void setPointsToRandom(@NotNull ItemStack compass, boolean random) {
        GoodLib.LOGGER.debug("[CompassUtils] Setting compass point random: {}", random);
        getOrCreatePointsTo(compass).putBoolean(RANDOM, random);
    }

    public static boolean isPointingRandom(@NotNull ItemStack compass) {
        if (!isPointing(compass)) return false;
        return getPointsTo(compass).contains(RANDOM, NbtElement.BYTE_TYPE);
    }

    public static @Nullable Boolean getRandom(@NotNull ItemStack compass) {
        if (!isPointingRandom(compass)) return null;
        return getPointsTo(compass).getBoolean(RANDOM);
    }

}
