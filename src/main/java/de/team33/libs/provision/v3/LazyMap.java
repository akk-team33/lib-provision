package de.team33.libs.provision.v3;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a kind of map that provides fixed values that are initialized as late as possible.
 *
 * @param <C> A context type that can be used to initialize values to be mapped.
 *            Typically a type containing a LazyMap instance as a field.
 */
public class LazyMap<C> {

    private final C context;
    private final Map<Object, Object> map = new ConcurrentHashMap<>(0);

    /**
     * Initializes a new instance giving a context.
     */
    public LazyMap(final C context) {
        this.context = context;
    }

    /**
     * <p>Gets a value based on a given {@link Key}. When this happens for the first time, the {@link Key}'s
     * {@link Key#init(Object) initialisation method} is called and the result is associated with the {@link Key} in
     * an underlying map. From the second time, the result is found in the map based on the given {@link Key} and
     * returned directly without calling the {@link Key}'s {@link Key#init(Object) initialisation method} again.</p>
     *
     * <p>If multiple concurrent calls occur in parallel from different threads, it may happen that the
     * function is called multiple times before one of the results is effectively stored in the map.
     * In that case, the underlying LazyMap still remains consistent as long as the functional results are
     * consistent.</p>
     */
    public final <R> R get(final Key<C, R> key) {
        //noinspection unchecked
        return Optional.ofNullable((R) map.get(key)).orElseGet(() -> {
            try {
                final R result = key.init(context);
                map.put(key, result);
                return result;
            } catch (final Exception caught) {
                throw new InitException(caught);
            }
        });
    }

    /**
     * Resets the underlying map by completely emptying it.
     */
    public final void reset() {
        map.clear();
    }

    /**
     * Abstracts a key to access a specific value within a {@link LazyMap}.
     * Such a key will typically have identity semantics.
     *
     * @param <C> A context type that can be used to initialize values to be mapped.
     *            Typically a type containing a LazyMap instance as a field.
     * @param <R> The type of the represented value within the {@link LazyMap}.
     */
    @FunctionalInterface
    public interface Key<C, R> {

        /**
         * Applies this key to a given context.
         */
        R init(C context) throws Exception;
    }

    /**
     * Defines a kind of {@link RuntimeException} that is thrown when the initialization of a lazy value fails.
     */
    public static final class InitException extends RuntimeException {

        private InitException(final Throwable cause) {
            super(cause.getMessage(), cause);
        }
    }
}
