package de.team33.libs.provision.v1;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implements a kind of map that provides fixed values that are initialized as late as possible.
 *
 * @param <C> A context type that can be used to initialize values to be mapped.
 *            Typically a type containing a LazyMap instance as a field.
 */
public class LazyMap<C> {

    private final Map<Function, Object> map = new ConcurrentHashMap<>(0);
    private final C context;

    public LazyMap(final C context) {
        this.context = context;
    }

    public final <T> T get(final Function<C, T> key) {
        //noinspection unchecked
        return Optional.ofNullable((T) map.get(key)).orElseGet(() -> {
            final T result = key.apply(context);
            map.put(key, result);
            return result;
        });
    }

    public final void reset() {
        map.clear();
    }
}
