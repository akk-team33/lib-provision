package de.team33.libs.provision.v1.sync;

import java.util.function.Supplier;

/**
 * <p>Implements a supplier that provides a fixed value that is determined once but as late as possible,
 * and only if needed at all.</p>
 *
 * <p>This implementation ensures that the {@linkplain #Lazy(Supplier) initially specified supplier}
 * is called up to a maximum of once, even for concurrent accesses.</p>
 *
 * @see de.team33.libs.provision.v1.Lazy
 */
public class Lazy<T> implements Supplier<T> {

    private Supplier<T> backing;

    /**
     * Initializes a new instance based on a supplier describing how the represented value will be determined.
     */
    public Lazy(final Supplier<T> initial) {
        this.backing = new Initial(initial);
    }

    /**
     * <p>Returns the value that has been determined using the
     * {@linkplain #Lazy(Supplier) initially specified supplier}.</p>
     *
     * <p>Ideally, the result is always the same, once determined value.
     * However, if multiple concurrent calls from different threads are executed in parallel, it may
     * happen that the {@linkplain #Lazy(Supplier) initially specified supplier} is called multiple times
     * before one of the results is effectively available for further calls. In this case, the result remains
     * consistent, as long as the results of the supplier are consistent.</p>
     *
     * <p>When it comes to preventing this in any case, the {@linkplain de.team33.libs.provision.v1.sync.Lazy
     * synchronized lazy implementation} should be used instead.</p>
     */
    @Override
    public T get() {
        return backing.get();
    }

    private static class Final<T> implements Supplier<T> {

        private final T result;

        private Final(final T result) {
            this.result = result;
        }

        @Override
        public T get() {
            return result;
        }
    }

    private class Initial implements Supplier<T> {

        private final Supplier<T> initial;

        private Initial(final Supplier<T> initial) {
            this.initial = initial;
        }

        @Override
        public synchronized T get() {
            if (backing == this) {
                backing = new Final<>(initial.get());
            }
            return backing.get();
        }
    }
}
