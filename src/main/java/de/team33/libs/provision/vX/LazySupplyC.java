package de.team33.libs.provision.vX;

import sun.reflect.generics.tree.Tree;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LazySupplyC<C> extends LazySupply<C> {

    private Map<Object, Object> map = Collections.emptyMap();

    public LazySupplyC(final C context) {
        super(context);
    }

    @Override
    public final <R> R get(final Key<? super C, R> key) {
        //noinspection unchecked
        return Optional.ofNullable((R) map.get(key)).orElseGet(() -> {
            try {
                synchronized (this) {
                    final R result = key.init(context);
                    final Map<Object, Object> newMap = new LinkedHashMap<>(map);
                    newMap.put(key, result);
                    map = newMap;
                    return result;
                }
            } catch (final RuntimeException caught) {
                throw caught;
            } catch (final Exception caught) {
                throw new EnvelopeException(caught);
            }
        });
    }
}
