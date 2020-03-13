package de.team33.libs.provision.v3;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a construct that can get and hold certain values of unique types within a given context.
 * It is used for the late initialization of values that are basically constant in the context but should only be
 * calculated once and only when they are actually needed (lazy initialization).
 *
 * @param <C> A context type to be used to initialize the provided values.
 */
public class LazySupply<C> {

    private final C context;
    private final Map<Object, Object> map = new ConcurrentHashMap<>(0);

    /**
     * Initializes a new instance with the intended context.
     */
    public LazySupply(final C context) {
        this.context = context;
    }

    /**
     * <p>Returns the value associated with the given {@link Key Key}.</p>
     *
     * <p>When this happens for the first time, the {@link Key#init(Object) Key's initialisation method} is performed
     * and the result is associated with the {@link Key Key}. From the second time, the result of the association with
     * the key is found and returned directly without calling the initialization method again.</p>
     *
     * <p>If multiple concurrent calls occur in parallel from different threads, it may happen that the
     * {@link Key#init(Object) Key's initialisation method} is called multiple times before one of the results is
     * effectively associated with the {@link Key Key}. In that case, this instance still remains consistent as
     * long as the method's results are consistent over several calls.</p>
     *
     * @throws EnvelopeException if the {@link Key#init(Object) Key's initialisation method} causes a checked exception.
     */
    public final <R> R get(final Key<? super C, R> key) {
        //noinspection unchecked
        return Optional.ofNullable((R) map.get(key)).orElseGet(() -> {
            try {
                final R result = key.init(context);
                map.put(key, result);
                return result;
            } catch (final RuntimeException caught) {
                throw caught;
            } catch (final Exception caught) {
                throw new EnvelopeException(caught);
            }
        });
    }

    /**
     * Resets this instance so that any {@link Key#init(Object) Key's initialisation method} will be called again the
     * next time it is applied to {@link #get(Key) get(Key)}.
     */
    public final void reset() {
        map.clear();
    }

    /**
     * <p>Abstracts a key for access to a certain value in a {@link LazySupply} as well as a kind of function for the
     * first calculation of that value.</p>
     *
     * <p>Such a key will typically have identity semantics.</p>
     *
     * @param <C> A context type to be used to initialize the provided values.
     * @param <R> The type of the represented value.
     */
    @FunctionalInterface
    public interface Key<C, R> {

        /**
         * Applies this key to a given context and returns the value to be associated with this key.
         */
        R init(C context) throws Exception;
    }

    /**
     * Defines a kind of {@link RuntimeException} that is thrown when the initialization of a provided value fails.
     */
    public static final class EnvelopeException extends RuntimeException {

        private EnvelopeException(final Throwable cause) {
            super(cause.getMessage(), cause);
        }
    }
}
