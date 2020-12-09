package io.github.stuff_stuffs.dungeon_generator.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    public static <T> T getRandom(final Collection<? extends T> collection, final Random random) {
        return getRandom(collection, collection.size(), random);
    }

    public static <T> T getRandom(final Iterable<? extends T> collection, final int size, final Random random) {
        final int i = random.nextInt(size);
        final Iterator<? extends T> iterator = collection.iterator();
        int count = 0;
        while (true) {
            if (count == i) {
                return iterator.next();
            }
            count++;
            iterator.next();
        }
    }

    public static <T> T getRandom(final List<? extends T> collection, final Random random) {
        if (collection.size() == 0) {
            return null;
        }
        final int pos = random.nextInt(collection.size());
        return collection.get(pos);
    }
}
