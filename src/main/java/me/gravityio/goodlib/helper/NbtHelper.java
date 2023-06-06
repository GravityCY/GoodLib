package me.gravityio.goodlib.helper;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Some utilities regarding NBT that {@link net.minecraft.nbt.NbtHelper NbtHelper} doesn't have
 */
public class NbtHelper {

    /**
     * Copies from a -> b.
     * @param comp {@link NbtCompound}
     * @param from From Key.
     * @param to To Key.
     * @return Whether it succeeded
     */
    public static boolean internalCopy(NbtCompound comp, String from, String to) {
        if (comp.get(from) == null) return false;

        comp.put(to, comp.get(from).copy());
        return true;
    }

    /**
     * Experimental method. <br>
     * Essentially the same as {@link NbtHelper#getDeep NbtUtils#getDeep} but it tries to create the NbtCompounds along the way.
     *
     * @param comp {@link NbtCompound}.
     * @param typeSupplier A {@link Supplier} to supply a .
     * @param clazz A {@link Class} to cast the resulting {@link NbtElement} to.
     * @param orderedPaths
     * @return
     * @param <T>
     */
    public static <T extends NbtElement> T getOrCreateDeep(NbtCompound comp, Supplier<T> typeSupplier, Class<T> clazz, String... orderedPaths) {
        if (comp == null) return null;

        if (orderedPaths.length != 1)
            return getOrCreateDeep(NbtHelper.getOrCreate(comp, orderedPaths[0]), typeSupplier, clazz, Arrays.stream(orderedPaths).skip(1).toArray(String[]::new));
        return NbtHelper.getOrCreate(comp, orderedPaths[0], typeSupplier, clazz);
    }

    /**
     * Experimental Method. <br>
     * Essentially gets an {@link NbtElement} that is nested under multiple {@link NbtCompound NbtCompounds} <br><br>
     * Here we have an example of an {@link NbtCompound}
     * <pre>
     *  {
     *     Parent: {
     *       SubParent: {
     *         TheoreticalSubSubParent: {
     *           ThingYouWant: "You want me so bad"
     *         }
     *       }
     *     }
     *  }</pre><br>
     * So in THEORY with this method you would do
     * <pre>{@code
     * NbtUtils.getDeep(ParentCompound, NbtString.class, "SubParent", "TheoreticalSubSubParent", "ThingYouWant");
     * }</pre>
     * In theory this shouldn't break if any of them are null, it should just return null, that's why it's experimental.
     * @param comp {@link NbtCompound}
     * @param clazz A {@link Class} extending {@link NbtElement} to cast the found {@link NbtElement element} to.
     * @param orderedKeys The keys to walk down towards the result.
     * @return The resulting {@link NbtElement}. (hopefully)
     * @param <T> Class argument, should a class extending {@link NbtElement}
     */
    public static <T extends NbtElement> T getDeep(NbtCompound comp, Class<T> clazz, String... orderedKeys) {
        if (comp == null) return null;

        if (orderedKeys.length != 1)
            return getDeep(NbtHelper.get(comp, orderedKeys[0]), clazz, Arrays.stream(orderedKeys).skip(1).toArray(String[]::new));
        return clazz.cast(comp.get(orderedKeys[0]));
    }

    /**
     * Converts a {@link List} into an {@link NbtList}
     * @param list The List to convert to an NbtList
     * @param elementConverter a Function that receives your list elements and should return something that extends or is an NbtElement
     * @return {@link NbtList} from the {@link List}
     */
    public static <T> NbtList fromList(List<T> list, Function<T, NbtElement> elementConverter) {
        if (list == null || elementConverter == null) return null;

        NbtList nbtList = new NbtList();
        list.forEach(o -> {
            NbtElement converted = elementConverter.apply(o);
            if (converted != null)
                nbtList.add(converted);
        });
        return nbtList;
    }

    /**
     * Converts an {@link NbtList} into a {@link List}
     * @param nbtList The NbtList to convert to a List
     * @param list Empty List you want to convert to
     * @param elementConverter a Function that receives the NbtLists' NbtElements and converts them to your List Type
     * @return Converted {@link List}
     */
    public static <T> List<T> toList(NbtList nbtList, List<T> list, Function<NbtElement, T> elementConverter) {
        if (nbtList == null || list == null || elementConverter == null) return null;

        nbtList.forEach(t -> {
            T converted = elementConverter.apply(t);
            if (converted != null)
                list.add(converted);
        });
        return list;
    }

    /**
     * Converts a {@link Map} to an {@link NbtCompound}
     * @param map The {@link Map} to convert to an {@link NbtCompound}
     * @param keyConverter a {@link Function} that receives your maps' key and converts it into a {@link String}
     * @param valueConverter a {@link Function} that receives your maps' value and converts it into an {@link NbtElement}
     * @return The converted {@link NbtCompound}
     */
    public static <T, F> NbtCompound fromMap(Map<T, F> map, Function<T, String> keyConverter, Function<F, NbtElement> valueConverter) {
        if (map == null || keyConverter == null || valueConverter == null) return null;

        NbtCompound nbtCompound = new NbtCompound();
        map.forEach((o1, o2) -> {
            String convertedKey = keyConverter.apply(o1);
            NbtElement convertedValue = valueConverter.apply(o2);
            if (convertedKey != null && convertedValue != null)
                nbtCompound.put(convertedKey, convertedValue);
        });
        return nbtCompound;
    }

    /**
     * Converts an {@link NbtCompound} to a {@link Map}
     * @param nbtCompound The {@link NbtCompound} to convert to a {@link Map}
     * @param map Empty {@link Map} you want to convert to
     * @param keyConverter a {@link Function} that receives the {@link NbtCompound NbtCompounds'} key ({@link String}) and converts it into your {@link Map Maps'} Key Type
     * @param valueConverter a {@link Function} that receives the {@link NbtCompound NbtCompounds'} value ({@link NbtElement}) and converts it into your {@link Map Maps'} Key Type
     * @return The converted {@link Map}
     */
    public static <T, F> Map<T, F> toMap(NbtCompound nbtCompound, Map<T, F> map, Function<String, T> keyConverter, Function<NbtElement, F> valueConverter) {
        if (nbtCompound == null || map == null || keyConverter == null || valueConverter == null) return null;

        nbtCompound.getKeys().forEach(key -> {
            T convertedKey = keyConverter.apply(key);
            F convertedValue = valueConverter.apply(nbtCompound.get(key));
            if (convertedKey != null && convertedValue != null)
                map.put(convertedKey, convertedValue);
        });
        return map;
    }

    /**
     * Gets or creates any {@link NbtElement}
     * @param nbt The root NBT Compound to use to check if a sub element exists, etc.
     * @param key The element key to get or create.
     * @param typeSupplier A {@link Supplier} to provide the element you want to be created if it doesn't exist.
     * @return The element that has been either gotten or created.
     */
    public static <T extends NbtElement> T getOrCreate(NbtCompound nbt, String key, Supplier<T> typeSupplier, Class<T> clazz) {
        if (nbt == null) return null;
        NbtElement elem = nbt.get(key);
        if (!clazz.isInstance(elem))
            nbt.put(key, elem = typeSupplier.get());

        return clazz.cast(elem);
    }

    /**
     * Gets or creates an {@link NbtCompound}.
     * @param nbt The root NBT Compound to use to check if a sub element exists, etc.
     * @param key The key of the element to get or create.
     * @return The element that has been either gotten or created
     */
    public static NbtCompound getOrCreate(NbtCompound nbt, String key) {
        if (nbt == null) return null;
        NbtCompound ret = NbtHelper.get(nbt, key);
        if (ret == null)
            nbt.put(key, ret = new NbtCompound());
        return ret;
    }

    /**
     * Gets a NULLABLE element from the NBT <br><br>
     * You could also just do <span style="background-color:#222">NbtList theList = (NbtList) nbt.get("TheList")</span>
     * @param nbt The NBT to work on
     * @param key The key of the element to get.
     * @param clazz A class of what to return
     * @return Returns the type of the class parameter
     * @param <T> The class of what to return
     */
    public static <T extends NbtElement> @Nullable T get(NbtCompound nbt, String key, Class<T> clazz) {
        if (nbt == null) return null;
        return clazz.cast(nbt.get(key));
    }

    /**
     * Gets an NbtCompound from a key.
     * @param nbt The NBT to work on
     * @param key The key inside the NBT
     * @return Returns an
     */
    public static @Nullable NbtCompound get(NbtCompound nbt, String key) {
        if (nbt == null) return null;
        return (NbtCompound) nbt.get(key);
    }
}
