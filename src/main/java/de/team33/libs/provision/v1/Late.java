package de.team33.libs.provision.v1;

import de.team33.libs.identification.v1.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Late {

    private Map<Key, Supplier> suppliers = new ConcurrentHashMap<>();

    private Late(final Map<Key, Supplier> initials) {
        initials.forEach((key, initial) -> suppliers.put(key, () -> {
            final Object result = initial.get();
            suppliers.put(key, () -> result);
            return result;
        }));
    }

    public static Builder builder() {
        return new Builder();
    }

    public final <T> T get(final Key<T> key) {
        return getSupplier(key).get();
    }

    @SuppressWarnings("unchecked")
    private <T> Supplier<T> getSupplier(final Key<T> key) {
        return Optional
                .ofNullable(suppliers.get(key))
                .orElseThrow(() -> new IllegalArgumentException("Unknown key: " + key));
    }

    public static class Builder {

        private Map<Key, Supplier> initials = new HashMap<>();

        public final <T> Builder add(final Key<T> key, final Supplier<T> initial) {
            initials.put(key, initial);
            return this;
        }

        public final Late build() {
            return new Late(initials);
        }
    }
}
