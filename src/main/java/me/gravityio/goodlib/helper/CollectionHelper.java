package me.gravityio.goodlib.helper;

import me.gravityio.goodlib.util.LookupMap;

import java.util.ArrayList;
import java.util.List;

public class CollectionHelper {

    public static <T> List<T> exclude(List<T> all, List<T> exclusions) {
        List<T> out = new ArrayList<>();
        LookupMap<T> exclusiveLookup = LookupMap.fromList(exclusions);
        for (T identifier : all) {
            if (!exclusiveLookup.contains(identifier))
                out.add(identifier);
        }
        return out;
    }

}
