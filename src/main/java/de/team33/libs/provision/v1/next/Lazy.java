package de.team33.libs.provision.v1.next;

import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>Implements a supplier that provides a fixed value that is determined once but as late as possible,
 * and only if needed at all.</p>
 *
 * <p>Ideally, the {@linkplain #get() result} is always the same, once determined value.
 * However, if multiple concurrent calls from different threads are executed in parallel, it may
 * happen that the {@linkplain #Lazy(Supplier) initially specified supplier} is called multiple times
 * before one of the results is effectively available for further calls. In this case, the result remains
 * consistent, as long as the results of the supplier are consistent.</p>
 *
 * @see de.team33.libs.provision.v1.sync.Lazy
 */
public class Lazy<T> implements Supplier<T> {

    private Supplier<T> backing;

    /**
     * Initializes a new instance based on a supplier describing how the represented value once will be
     * determined.
     */
    public Lazy(final Supplier<T> initial) {
        this(Stringency.LOOSE, initial);
    }

    /**
     * @deprecated Intended as private, use {@link #loose(Supplier)} or {@link #strict(Supplier)} instead.
     */
    @Deprecated
    protected Lazy(final Stringency stringency, final Supplier<T> initial) {
        this.backing = stringency.initial(this).apply(initial);
    }

    public static <T> Lazy<T> loose(final Supplier<T> initial) {
        return new Lazy<>(Stringency.LOOSE, initial);
    }

    public static <T> Lazy<T> strict(final Supplier<T> initial) {
        return new Lazy<>(Stringency.STRICT, initial);
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

    private class LooseInitial implements Supplier<T> {

        private final Supplier<T> initial;

        private LooseInitial(final Supplier<T> initial) {
            this.initial = initial;
        }

        @Override
        public final T get() {
            backing = new Final<>(initial.get());
            return backing.get();
        }
    }

    private class StrictInitial implements Supplier<T> {

        private final Supplier<T> initial;

        private StrictInitial(final Supplier<T> initial) {
            this.initial = initial;
        }

        @Override
        public final synchronized T get() {
            if (backing == this) {
                backing = new Final<>(initial.get());
            }
            return backing.get();
        }
    }

    @Deprecated
    protected enum Stringency {

        LOOSE(lazy -> initial -> lazy.new LooseInitial(initial)),

        STRICT(lazy -> initial -> lazy.new StrictInitial(initial));

        private final Function wrapper;

        <T> Stringency(final Function<Lazy<T>, Function<Supplier<T>, Supplier<T>>> wrapper) {
            this.wrapper = wrapper;
        }

        @SuppressWarnings("unchecked")
        private <T> Function<Supplier<T>, Supplier<T>> initial(final Lazy<T> lazy) {
            return (Function<Supplier<T>, Supplier<T>>) wrapper.apply(lazy);
        }
    }
}
