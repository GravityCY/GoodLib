package me.gravityio.goodlib.lib;

import me.gravityio.goodlib.GoodLib;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Registry for loot tables that should only spawn in any chest within a specified structure.
 */

public class BetterLootRegistry {
    public static final Identifier ALL = new Identifier("better_loot", "structures");

    /**
     * Maps Structure Identifiers -> List of Loot Table Identifiers<br>
     * When a {@link net.minecraft.block.entity.LootableContainerBlockEntity LootableContainerBlockEntity} gets looted it gets a list of all <br>
     * Loot Table Identifiers associated with its parent Structure and provides the loot tables items to the chest
     */
    public static Map<Identifier, List<Identifier>> structureLootTables = new HashMap<>();


    public static void registerLoot(Identifier structureKey, Identifier lootTableKey) {
        GoodLib.LOGGER.debug("[BetterLootRegistry] Registering Loot {} for Structure: {}", lootTableKey, structureKey);
        if (!structureLootTables.containsKey(structureKey))
            structureLootTables.put(structureKey, new ArrayList<>());
        structureLootTables.get(structureKey).add(lootTableKey);
    }

    public static List<Identifier> getLoot(Identifier structureKey) {
        GoodLib.LOGGER.debug("[BetterLootRegistry] Getting Loot for Structure: {}", structureKey);
        List<Identifier> merged = new ArrayList<>();
        List<Identifier> all = structureLootTables.get(ALL);
        List<Identifier> structure = structureLootTables.get(structureKey);
        if (all != null)
            merged.addAll(all);
        if (structure != null)
            merged.addAll(structure);
        return merged;
    }

}
