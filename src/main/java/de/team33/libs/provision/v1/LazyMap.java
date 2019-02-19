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

    /**
     * Initializes a new instance giving a context.
     */
    public LazyMap(final C context) {
        this.context = context;
    }

    /**
     * <p>Gets a value based on a given function. When this happens for the first time, the function is
     * called and the result is stored in a map using the function itself as a key. From the second time, the
     * result is found in the map based on the given function and returned directly without calling the
     * function again.</p>
     *
     * <p>If multiple concurrent calls occur in parallel from different threads, it may happen that the
     * function is called multiple times before one of the results is effectively put into the map.
     * In that case, the underlying LazyMap still remains consistent as long as the functional results are
     * consistent.</p>
     */
    public final <T> T get(final Function<C, T> key) {
        //noinspection unchecked
        return Optional.ofNullable((T) map.get(key)).orElseGet(() -> {
            final T result = key.apply(context);
            map.put(key, result);
            return result;
        });
    }

    /**
     * Resets the underlying map by completely emptying it.
     */
    public final void reset() {
        map.clear();
    }
}
