package de.team33.libs.provision.vX;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LazySupplyX<C> extends LazySupply<C> {

    public LazySupplyX(final C context) {
        super(context);
    }

    @Override
    public final <R> R get(final Key<? super C, R> key) {
        try {
            return key.init(context);
        } catch (final RuntimeException caught) {
            throw caught;
        } catch (final Exception caught) {
            throw new EnvelopeException(caught);
        }
    }
}
