package de.team33.libs.provision.v1;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * <p>Implements a cache for the results of a particular function</p>
 *
 * @param <P> The parameter type of the associated function.
 * @param <K> A unique key type for associating the results of the associated function.
 * @param <R> The result type of the associated function.
 */
public class Cache<P, K, R> {

    private final Map<K, R> cache = new ConcurrentHashMap<>(0);

    private final Function<P, K> toKey;

    private final Function<P, R> toResult;

    /**
     * <p>Initializes a new instance.</p>
     *
     * @param toKey    A function that generates a unique key from a parameter.
     * @param toResult The primary function that determines the actual result from a parameter.
     */
    public Cache(final Function<P, K> toKey, final Function<P, R> toResult) {
        this.toKey = toKey;
        this.toResult = toResult;
    }

  /**
   * <p>Returns the result of the associated function for the given parameter.</p>
   *
   * <p>Ideally, the associated function is executed only once for a given parameter. Each additional
   * application with the same parameter leads to the same result once determined.</p>
   *
   * <p>When multiple concurrent calls from parallel threads occur, the associated function may be called
   * multiple times. However, this usually does not pose a problem: the cache and its results remain
   * consistent if the results of the associated function are consistent.</p>
   */
    public R get(final P parameter) {
        final K key = toKey.apply(parameter);
        return Optional.ofNullable(cache.get(key)).orElseGet(() -> {
            final R result = toResult.apply(parameter);
            cache.put(key, result);
            return result;
        });
    }
}
