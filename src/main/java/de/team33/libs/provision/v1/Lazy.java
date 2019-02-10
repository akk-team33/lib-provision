package de.team33.libs.provision.v1;

import java.util.function.Supplier;

/**
 * Implements a supplier that provides a fixed value that is initialized as late as possible.
 */
public class Lazy<T> implements Supplier<T> {

    private Supplier<T> backing;

    public Lazy(final Supplier<T> initial) {
        this.backing = new Initial(initial);
    }

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
