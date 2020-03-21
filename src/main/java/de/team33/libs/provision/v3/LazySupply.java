package de.team33.libs.provision.v3;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * <p>Implements a supply of virtually fixed values of individual types within a certain context.
 * These values are only actually determined when they are accessed for the first time (lazy initialization).</p>
 *
 * <p>Individual values are accessed similarly to a map via associated {@link Key Keys}, which in this case also define the
 * initialization of the respective value.</p>
 *
 * <p>This implementation cannot prevent the initialization code of a value from being executed multiple times in the
 * event of concurrent access from different threads.
 * However, it ensures that an affected value and the supply as a whole remain consistent as long as the
 * initialization code provides consistent values.</p>
 *
 * @param <C> A context type to which the provided values belong.
 */
public class LazySupply<C> {

    private final C context;
    private final Map<Object, Object> map = new ConcurrentHashMap<>(0);

    /**
     * Initializes a new instance giving the intended context.
     */
    public LazySupply(final C context) {
        this.context = context;
    }

    /**
     * Returns the value associated with the given {@link Key Key}.
     *
     * @throws EnvelopeException when the {@link Key#init(Object) Key's initialisation method} causes a checked exception.
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
     * Resets this instance so that any {@linkplain Key#init(Object) Key's initialisation method} will be called again the
     * next time it is applied to {@link #get(Key)}.
     */
    public final void reset() {
        map.clear();
    }

    /**
     * <p>Abstracts a key for access to a certain value in a {@link LazySupply} as well as a kind of function for the
     * initialisation of that value.</p>
     *
     * <p>An instance will typically be defined {@code static final} and have identity semantics.</p>
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
