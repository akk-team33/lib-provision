package de.team33.libs.provision.vX;

public abstract class LazySupply<C> {

    protected final C context;

    protected LazySupply(final C context) {
        this.context = context;
    }

    public abstract <R> R get(final Key<? super C, R> key);

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

        public EnvelopeException(final Throwable cause) {
            super(cause.getMessage(), cause);
        }
    }
}
