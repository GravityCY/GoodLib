package me.gravityio.goodlib.dev;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.gravityio.goodlib.helper.*;
import me.gravityio.goodlib.lib.BetterCompass;
import me.gravityio.goodlib.lib.BetterLootRegistry;
import me.gravityio.goodlib.lib.MissingTranslation;
import me.gravityio.goodlib.mixin.interfaces.better_loot.ILootedStructure;
import me.gravityio.goodlib.mixin.interfaces.better_loot.IStructureLootable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.command.argument.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;

import static me.gravityio.goodlib.dev.CommandProcessor.*;

@Commands(namespace = "glib")
public class DevCommands {

    static Formatting[] FORMAT_COLORS = new Formatting[] {Formatting.WHITE, Formatting.GRAY, Formatting.DARK_GRAY};

    public static Text error(String string, Object... format) {
        return Text.literal(string.formatted(format)).formatted(Formatting.RED);
    }

    public static Text neutral(String string, Object... format) {
        return Text.literal(string.formatted(format));
    }

    public static Text success(String string, Object... format) {
        return Text.literal(string.formatted(format)).formatted(Formatting.GREEN);
    }

    @Commands(namespace = "nbt")
    public static class Nbt {
        @ArgumentHandler
        public static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getArgumentHandlers(String cmdName) {
            var def = IArgumentHandler.getDefaultServerHandlers();
            def.put(ServerPlayerEntity.class, (name, context) -> context.getSource().getPlayer());
            return def;
        }

        @TypeProvider
        public static Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProviders(String cmdName) {
            var def = IArgumentTypeProvider.getDefaultTypeProviders();
            def.put(String.class, StringArgumentType::word);
            return def;
        }
        @Command
        public static void copyInternalNbt(ServerPlayerEntity player, @Argument(name = "from") String from, @Argument(name = "to") String to) {
            ItemStack stack = player.getMainHandStack();
            NbtCompound nbt = stack.getNbt();
            if (nbt == null) {
                player.sendMessage(error("Item doesn't have any NBT!"));
            } else {
                boolean success = GoodNbtHelper.internalCopy(nbt, from, to);
                if (success) {
                    player.sendMessage(success("Successfully moved internal nbt '%s' to '%s'", from, to));
                } else {
                    player.sendMessage(error("Failed, likely '%s' nbt doesn't exist to even copy!", from));
                }
            }
        }

        @Command
        public static void getDeep(ServerPlayerEntity player, @Argument(name = "path") String path) {
            ItemStack stack = player.getMainHandStack();
            NbtElement elem = GoodNbtHelper.getDeep(stack.getNbt(), NbtElement.class, path);
            if (elem == null) {
                player.sendMessage(error("Current Item doesn't have any NBT at '%s'", path));
            } else {
                player.sendMessage(success("NBT of current Item at '%s' is {%s}", path, elem.asString()));
            }
        }

        @Command
        public static void containsDeep(ServerPlayerEntity player, @Argument(name = "namespacedKey") String path) {
            ItemStack stack = player.getMainHandStack();
            boolean contains = GoodNbtHelper.containsDeep(stack.getNbt(), path);
            if (!contains) {
                player.sendMessage(error("Current Item doesn't have any NBT at '%s'", path));
            } else {
                player.sendMessage(success("Current Item has NBT at '%s'", path));
            }
        }

        @Command
        public static void removeDeep(ServerPlayerEntity player, @Argument(name = "namespacedKey") String path) {
            ItemStack stack = player.getMainHandStack();
            boolean removed = GoodNbtHelper.removeDeep(stack.getNbt(), path);
            if (removed) {
                player.sendMessage(success("Removed NBT of Current Item at '%s'", path));
            } else {
                player.sendMessage(error("Current Item doesn't have any NBT at '%s'", path));
            }
        }

        @Command
        public static void putDeep(ServerPlayerEntity player, @Argument(name = "path") String path, @Argument(name = "putString") String putString) {
            ItemStack stack = player.getMainHandStack();
            GoodNbtHelper.putDeep(stack.getOrCreateNbt(), NbtString.of(putString), path);
            player.sendMessage(success("Put '%s' at '%s'", putString, path));
        }

        @Command
        public static void copyDeep(ServerPlayerEntity player, @Argument(name = "fromPath") String fromPath, @Argument(name = "toPath") String toPath) {
            ItemStack stack = player.getMainHandStack();
            boolean copied = GoodNbtHelper.copyDeep(stack.getNbt(), fromPath, toPath);
            if (copied) {
                player.sendMessage(success("Copied from '%s' to '%s'", fromPath, toPath));
            } else {
                player.sendMessage(error("'%s' doesn't exist!", fromPath));
            }
        }

        @Command
        public static void getOrCreateDeep(ServerPlayerEntity player, @Argument(name = "namespacedKey") String path, @Argument(name = "putString") String putString) {
            ItemStack stack = player.getMainHandStack();
            NbtElement b4 = GoodNbtHelper.getDeep(stack.getNbt(), NbtElement.class, path);
            NbtString nbtString = GoodNbtHelper.getOrCreateDeep(stack.getNbt(), () -> NbtString.of(putString), NbtString.class, path);
            NbtElement now = GoodNbtHelper.getDeep(stack.getNbt(), NbtElement.class, path);
            if (nbtString == null) {
                player.sendMessage(error("NbtString is null"));
            } else {
                player.sendMessage(neutral("Value of Old ItemStack NbtString {%s}", b4 == null ? "null" : b4.asString()));
                player.sendMessage(neutral("Value of New ItemStack NbtString {%s}", now == null ? "null" : now.asString()));
                player.sendMessage(neutral("Value of received NbtString {%s}", nbtString.asString()));

            }
        }
    }

    @Commands(namespace = "item")
    public static class Item {
        @ArgumentHandler
        private static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getArgumentHandler(String cmdName) {
            var def = IArgumentHandler.getDefaultServerHandlers();
            def.put(ServerPlayerEntity.class, (name, context) -> context.getSource().getPlayer());
            return def;
        }

        @TypeProvider
        private static Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProvider(String cmdName) {
            var def = IArgumentTypeProvider.getDefaultTypeProviders();
            return def;
        }
        @Command
        public static void clearLore(ServerPlayerEntity player) {
            GoodItemHelper.clearLore(player.getMainHandStack());
            player.sendMessage(success("Cleared lore!"));
        }

        @Command
        public static void setLore(ServerPlayerEntity player, @Argument(name = "lore") String lore) {
            ItemStack stack = player.getMainHandStack();
            GoodItemHelper.setLore(stack, Text.literal(lore));
            player.sendMessage(success("Set Lore to '%s'", lore));
        }

        @Command
        public static void getLore(ServerPlayerEntity player) {
            ItemStack stack = player.getMainHandStack();
            List<String> lore = GoodItemHelper.getLore(stack);
            if (lore == null) {
                player.sendMessage(error("No Lore!"));
            } else {
                player.sendMessage(success("Lore: "));
                for (String s : lore) {
                    player.sendMessage(neutral(s));
                }
            }
        }

        @Command
        public static void getLoreAsString(ServerPlayerEntity player) {
            ItemStack stack = player.getMainHandStack();
            String lore = GoodItemHelper.getLoreAsString(stack);
            if (lore == null) {
                player.sendMessage(error("No Lore!"));
            } else {
                player.sendMessage(neutral(lore));
            }
        }

        @Command
        public static void setLoreAtIndex(ServerPlayerEntity player, @Argument(name = "name") String lore, @Argument(name = "index") int index) {
            ItemStack stack = player.getMainHandStack();
            GoodItemHelper.setLore(stack, Text.literal(lore), index);
            player.sendMessage(success("Set Lore at Index: '%s' to '%s'", index, lore));
        }

        @Command
        public static void setHotbarTooltip(ServerPlayerEntity player, @Argument(name = "tooltip") String tooltip) {
            ItemStack stack = player.getMainHandStack();
            GoodItemHelper.setHotbarTooltip(stack, Text.literal(tooltip));
            player.sendMessage(success("Set Hotbar Tooltip to '%s'", tooltip));
        }

        @Command
        public static void getHotbarTooltip(ServerPlayerEntity player) {
            ItemStack stack = player.getMainHandStack();
            Text tooltip = GoodItemHelper.getHotbarTooltip(stack);
            if (tooltip != null) {
                player.sendMessage(tooltip);
            } else {
                player.sendMessage(error("Current Item doesn't have a Hotbar tooltip!"));
            }
        }

    }

    @Commands(namespace = "enchant")
    public static class Enchant {
        @ArgumentHandler
        private static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getArgumentHandlers(String cmdName) {
            var def = IArgumentHandler.getDefaultServerHandlers();
            def.put(ServerPlayerEntity.class, (name, context) -> context.getSource().getPlayer());
            def.put(Identifier.class, (name, context) -> context.getArgument(name, RegistryKey.class).getValue());
            return def;
        }

        @TypeProvider
        private static Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProviders(String cmdName) {
            var def = IArgumentTypeProvider.getDefaultTypeProviders();
            def.put(Identifier.class, () -> RegistryKeyArgumentType.registryKey(Registries.ENCHANTMENT.getKey()));
            return def;
        }
        @Command
        public static void hasEnchantmentObj(ServerPlayerEntity player, @Argument(name = "enchantment") Identifier enchantment) {
            ItemStack stack = player.getMainHandStack();
            boolean hasEnchant = false;
            NbtList nbtList = GoodNbtHelper.getDeep(stack.getNbt(), NbtList.class, "Enchantments");
            if (nbtList != null)
                hasEnchant = GoodEnchantHelper.hasEnchantment(Registries.ENCHANTMENT.get(enchantment), nbtList);
            player.sendMessage(neutral("Current Item has Enchantment: %s", hasEnchant));
        }

        @Command
        public static void hasEnchantmentId(ServerPlayerEntity player, @Argument(name = "enchantment") Identifier enchantment) {
            ItemStack stack = player.getMainHandStack();
            boolean hasEnchant = false;
            NbtList nbtList = GoodNbtHelper.getDeep(stack.getNbt(), NbtList.class, "Enchantments");
            if (nbtList != null)
                hasEnchant = GoodEnchantHelper.hasEnchantment(enchantment, nbtList);
            player.sendMessage(neutral("Current Item has Enchantment: %s", hasEnchant));
        }

        @Command
        public static void hasEnchantmentString(ServerPlayerEntity player, @Argument(name = "enchantment") Identifier enchantment) {
            ItemStack stack = player.getMainHandStack();
            boolean hasEnchant = false;
            NbtList nbtList = GoodNbtHelper.getDeep(stack.getNbt(), NbtList.class, "Enchantments");
            if (nbtList != null)
                hasEnchant = GoodEnchantHelper.hasEnchantment(enchantment.toString(), nbtList);
            player.sendMessage(neutral("Current Item has Enchantment: %s", hasEnchant));
        }
    }

    @Commands(namespace = "random")
    public static class Random {

        @ArgumentHandler
        public static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getArgumentHandlers(String cmdName) {
            var def = IArgumentHandler.getDefaultServerHandlers();
            def.put(ServerPlayerEntity.class, (name, context) -> context.getSource().getPlayer());
            def.put(Identifier.class, (name, context) -> context.getArgument(name, RegistryKey.class).getValue());
            return def;
        }

        @TypeProvider
        public static Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProviders(String cmdName) {
            var def = IArgumentTypeProvider.getDefaultTypeProviders();
            def.put(String.class, StringArgumentType::greedyString);
            def.put(Text.class, TextArgumentType::text);
            if (cmdName.equals("canInsertStackIntoInventory")) {
                def.put(Identifier.class, () -> RegistryKeyArgumentType.registryKey(Registries.ITEM.getKey()));
                def.put(int.class, () -> IntegerArgumentType.integer(1, 64));
            }
            return def;
        }

        @Command
        public static void canInsertStackIntoInventory(ServerPlayerEntity player, @Argument(name = "item") Identifier identifier, @Argument(name="count") int count) {
            ItemStack item = Registries.ITEM.get(identifier).getDefaultStack();
            item.setCount(Math.min(count, item.getMaxCount()));
            boolean canInsert = GoodInventoryHelper.canInsertInventory(player.playerScreenHandler.slots, item);
            player.sendMessage(neutral("Can Insert %d '%s' into player's inventory: %s", item.getCount(), identifier, canInsert));
        }

        @Command
        public static void capitalize(ServerPlayerEntity player, @Argument(name = "String") String input) {
            player.sendMessage(neutral("%s -> %s", input, GoodStringHelper.capitalize(input)));
        }

        @Command
        public static void toAbsolutePos(ServerPlayerEntity player, @Argument(name = "offset x") int x, @Argument(name = "offset y") int y, @Argument(name = "offset z") int z) {
            Vec3d vec = GoodHelper.toAbsolutePos(player.getPos(), player.getRotationClient(), new Vec3d(x, y, z));
            player.sendMessage(neutral("X:%.2f Y:%.2f Z:%.2f)", vec.x, vec.y, vec.z));
        }

        @Command
        public static void commands(ServerPlayerEntity player) {
            printCommandsRecursive(player, player.getServer().getCommandManager().getDispatcher().getRoot().getChild("glib"), 0);
        }

        @Command
        public static void translations(ServerPlayerEntity player) {
            for (Map.Entry<String, String> entry : MissingTranslation.cache.entrySet()) {
                player.sendMessage(neutral("Translation key '%s' points to a fallback of '%s'", entry.getKey(), entry.getValue()));
            }
        }

        public static void printCommandsRecursive(PlayerEntity player, CommandNode<?> parent, int depth) {
            if (depth == 0)
                player.sendMessage(Text.literal(parent.getName()));
            Formatting color = FORMAT_COLORS[depth % FORMAT_COLORS.length];
            for (CommandNode<?> child : parent.getChildren()) {
                if (!(child instanceof LiteralCommandNode)) continue;
                if (child.getChildren().isEmpty() || child.getChildren().iterator().next() instanceof ArgumentCommandNode<?,?>) {
                    player.sendMessage(Text.literal("| %s | - %s".formatted(" ".repeat(depth + 1), child.getName())).formatted(color));
                } else {
                    player.sendMessage(Text.literal("| %s %s".formatted("-".repeat(depth + 1), child.getName())).formatted(color));
                    printCommandsRecursive(player, child, depth + 1);
                }
            }
        }

    }

    @Commands(namespace = "compass")
    public static class Compass {

        @ArgumentHandler
        public static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getArgumentHandlers(String cmdName) {
            var def = IArgumentHandler.getDefaultServerHandlers();
            def.put(ServerPlayerEntity.class, (name, context) -> context.getSource().getPlayer());
            def.put(BlockPos.class, (name, context) -> context.getArgument(name, PosArgument.class).toAbsoluteBlockPos(context.getSource()));
            if (cmdName.equals("setPoint")) {
                def.put(Identifier.class, (name, context) -> context.getArgument(name, Identifier.class));
            }
            return def;
        }

        @TypeProvider
        public static Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProviders(String cmdName) {
            var def = IArgumentTypeProvider.getDefaultTypeProviders();
            if (cmdName.equals("setPoint")) {
                def.put(Identifier.class, DimensionArgumentType::dimension);
            }
            def.put(BlockPos.class, BlockPosArgumentType::blockPos);
            return def;
        }

        private static boolean isCompass(ServerPlayerEntity player, ItemStack stack) {
            if (!stack.isOf(Items.COMPASS)) {
                player.sendMessage(error("Item '%s' is not a Compass!", GoodHelper.getIdentifier(stack)));
                return false;
            }
            return true;
        }

        private static boolean isPointing(ServerPlayerEntity player, ItemStack stack) {
            if (!BetterCompass.isPointing(stack)) {
                player.sendMessage(error("Compass is not pointing at anything!"));
                return false;
            }
            return true;
        }

        private static boolean isPointingAtDimension(ServerPlayerEntity player, ItemStack stack) {
            if (!BetterCompass.isPointingAtDimension(stack)) {
                player.sendMessage(error("Compass is not pointing at a dimension!"));
                return false;
            }
            return true;
        }

        private static boolean isPointingAtPosition(ServerPlayerEntity player, ItemStack stack) {
            if (!BetterCompass.isPointingAtPosition(stack)) {
                player.sendMessage(error("Compass is not pointing at any position!"));
                return false;
            }
            return true;
        }

        @Command
        public static void setPoint(ServerPlayerEntity player, @Argument(name = "dimension") Identifier dimension, @Argument(name = "position") BlockPos pos) {
            ItemStack stack = player.getMainHandStack();
            if (!isCompass(player, stack)) return;

            BetterCompass.setPoint(stack, pos, dimension);
            player.sendMessage(success("Set Point NBT to; Position: %s, Dimension: %s", pos.toShortString(), dimension.toString()));
        }

        @Command
        public static void setPointPosition(ServerPlayerEntity player, @Argument(name = "position") BlockPos pos) {
            ItemStack stack = player.getMainHandStack();
            if (!isCompass(player, stack)) return;

            BetterCompass.setPointPosition(stack, pos);
            player.sendMessage(success("Set Point NBT to Position: %s", pos.toShortString()));
        }

        @Command
        public static void getPoint(ServerPlayerEntity player) {
            ItemStack stack = player.getMainHandStack();
            if (!isCompass(player, stack)) return;
            if (!isPointing(player, stack)) return;
            BlockPos pos = BetterCompass.getPointPosition(stack);
            Identifier dimension = BetterCompass.getPointDimension(stack);
            player.sendMessage(success("Compass is pointing at (%s) in dimension '%s'", pos.toShortString(), dimension.toString()));
        }

        @Command
        public static void getPointPosition(ServerPlayerEntity player) {
            ItemStack stack = player.getMainHandStack();
            if (!isCompass(player, stack)) return;
            if (!isPointingAtPosition(player, stack)) return;
            BlockPos pos = BetterCompass.getPointPosition(stack);
            player.sendMessage(success("Compass is pointing at (%s)", pos.toShortString()));
        }

        @Command
        public static void getPointDimension(ServerPlayerEntity player) {
            ItemStack stack = player.getMainHandStack();
            if (!isCompass(player, stack)) return;
            if (!isPointingAtDimension(player, stack)) return;
            Identifier dimension = BetterCompass.getPointDimension(stack);
            player.sendMessage(success("Compass is pointing at dimension '%s'", dimension.toString()));
        }

        @Command
        public static void getGlobalPosPoint(ServerPlayerEntity player) {
            ItemStack stack = player.getMainHandStack();
            if (!stack.isOf(Items.COMPASS)) {
                player.sendMessage(error("'%s' is not a Compass", GoodHelper.getIdentifier(stack).toString()));
                return;
            }

            GlobalPos pos = BetterCompass.getGlobalPosPoint(stack);
            if (pos != null) {
                player.sendMessage(success("Compass is pointing to dimension '%s' at position (%s)", pos.getDimension().getValue().toString(), pos.getPos().toString()));
            } else {
                player.sendMessage(error("Compass is not pointing to any global position."));
            }
        }

    }

    @Commands(namespace = "loot")
    public static class BetterLoot {
        @ArgumentHandler
        public static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getArgumentHandlers(String cmdName) {
            var def = IArgumentHandler.getDefaultServerHandlers();
            def.put(ServerPlayerEntity.class, (name, context) -> context.getSource().getPlayer());
            return def;
        }

        @TypeProvider
        public static Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProviders(String cmdName) {
            var def = IArgumentTypeProvider.getDefaultTypeProviders();
            return def;
        }

        @Command
        public static void isLooted(ServerPlayerEntity player) {
            StructureStart start = GoodHelper.getStructureStart(player.getWorld(), player.getBlockPos());
            if (start == null) {
                player.sendMessage(error("Not in a structure boundary!"));
            } else {
                ILootedStructure structure = (ILootedStructure) (Object) start;
                player.sendMessage(error("Structure '%s' at %s is Looted: %s", structure.getId().toString(), structure.getPos().toString(), structure.isLooted()));
            }
        }

        @Command
        public static void getStructure(ServerPlayerEntity player) {
            BlockHitResult res = GoodHelper.raycast(player, 5);
            BlockPos pos = res.getBlockPos();
            BlockState block = player.world.getBlockState(pos);
            BlockEntity blockEntity = player.world.getBlockEntity(pos);
            if (block == null || block.isAir()
                    || !(blockEntity instanceof LootableContainerBlockEntity lootableContainerBlockEntity)
                    || !(lootableContainerBlockEntity instanceof IStructureLootable structureLootable)) {
                String name = "minecraft:air";
                if (block != null)
                    name = String.valueOf(Registries.BLOCK.getId(block.getBlock()));
                player.sendMessage(error("Block '%s' is not a Structure Lootable Block!", name));
            } else {
                if (structureLootable.getStructure() != null) {
                    player.sendMessage(success("Parent Structure of Chest at (%s): %s", pos.toShortString(), structureLootable.getStructure().getId().toString()));
                } else {
                    if (structureLootable.hasLootable()) {
                        player.sendMessage(error("Chest at (%s) is not in any structure.", pos.toShortString()));
                    } else {
                        player.sendMessage(error("Chest at (%s) doesn't have any loot table.", pos.toShortString()));
                    }
                }
            }
        }

        @Command
        public static void getLootInStructure(ServerPlayerEntity player) {

            StructureStart start = GoodHelper.getStructureStart(player.getWorld(), player.getBlockPos());
            if (start == null) {
                player.sendMessage(error("Player is not in a structure boundary!"));
            } else {
                Identifier structureId = player.getWorld().getRegistryManager().get(RegistryKeys.STRUCTURE).getId(start.getStructure());
                var ids = BetterLootRegistry.getLoot(structureId);
                if (ids.isEmpty()) {
                    player.sendMessage(neutral("No loot for structure '%s'", structureId.toString()));
                } else {
                    player.sendMessage(success("Loot for structure '%s'", structureId.toString()));
                    for (Identifier id : ids) {
                        player.sendMessage(neutral("  Identifier: '%s'", id.toString()));
                    }
                }
            }

        }

        @Command
        public static void getRegisteredLoot(ServerPlayerEntity player) {
            for (Map.Entry<Identifier, List<Identifier>> entry : BetterLootRegistry.structureLootTables.entrySet()) {
                player.sendMessage(success("Loot Tables for Structure '%s':", entry.getKey().toString()));
                for (Identifier identifier : entry.getValue())
                    player.sendMessage(neutral(" %s", identifier.toString()));
            }
        }

    }

}
