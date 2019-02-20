package de.team33.libs.provision.v1.sync;

import java.util.function.Supplier;

/**
 * Implements a supplier that provides a fixed value that is initialized as late as possible.
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
     * Returns the value that was determined using the
     * {@linkplain #Lazy(Supplier) initially specified supplier}.
     *
     * Ideally, the result is always the same, once determined value.
     *
     * However, if multiple concurrent calls from different threads are executed in parallel, it may happen
     * that the {@linkplain #Lazy(Supplier) initially specified supplier} is called multiple times before one
     * of the results is effectively available for further calls. In this case, the result remains
     * consistent, as long as the results of the supplier are consistent.
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
