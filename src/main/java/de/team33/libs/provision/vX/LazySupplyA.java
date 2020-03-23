package de.team33.libs.provision.vX;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LazySupplyA<C> extends LazySupply<C> {

    private final Map<Object, Object> map = new ConcurrentHashMap<>(0);

    public LazySupplyA(final C context) {
        super(context);
    }

    @Override
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
}
