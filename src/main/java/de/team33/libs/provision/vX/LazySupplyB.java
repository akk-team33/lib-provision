package de.team33.libs.provision.vX;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LazySupplyB<C> extends LazySupply<C> {

    @SuppressWarnings("rawtypes")
    private final Map<Key, Result> map = new ConcurrentHashMap<>(0);

    private static final class Result<R> {
        private final R result;

        private Result(final R result) {
            this.result = result;
        }
    }

    public LazySupplyB(final C context) {
        super(context);
    }

    @Override
    public final <R> R get(final Key<? super C, R> key) {
        //noinspection unchecked
        return (R) map.computeIfAbsent(key, this::test).result;
    }

    private <R> Result<R> test(final Key<C, R> key) {
        try {
            return new Result<>(key.init(context));
        } catch (final RuntimeException caught) {
            throw caught;
        } catch (final Exception caught) {
            throw new EnvelopeException(caught);
        }
    }
}
