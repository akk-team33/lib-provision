package de.team33.libs.provision.vX;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LazySupply1<C> extends LazySupply<C> {

    private final Map<Object, Object> map = new HashMap<>(0);

    public LazySupply1(final C context) {
        super(context);
    }

    @Override
    public final synchronized <R> R get(final Key<? super C, R> key) {
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
