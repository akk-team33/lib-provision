package de.team33.libs.provision.v3;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>Implements a supplier that provides a fixed value that is determined once but as late as possible,
 * and only if needed at all.</p>
 *
 * <p>This implementation ensures that the {@linkplain #Lazy(Supplier, Supplier, Consumer) initially specified supplier}
 * is called up to a maximum of once, even for concurrent accesses.</p>
 */
public class Lazy<T> implements Supplier<T> {

    private final Supplier<T> initial;
    private final Supplier<Supplier<T>> getter;
    private final Consumer<Supplier<T>> setter;

    /**
     * Initializes a new instance based on a supplier describing how the represented value will be determined.
     */
    public Lazy(final Supplier<T> initial, final Supplier<Supplier<T>> getter, final Consumer<Supplier<T>> setter) {
        this.initial = initial;
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * <p>Returns the value that has been determined using the
     * {@linkplain #Lazy(Supplier, Supplier, Consumer) initially specified supplier}.</p>
     *
     * <p>The result is always the same, once determined value..</p>
     */
    @Override
    public synchronized T get() {
        if (getter.get() == this) {
            final T result = initial.get();
            setter.accept(() -> result);
        }
        return getter.get().get();
    }
}
