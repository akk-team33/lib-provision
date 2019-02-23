package de.team33.libs.provision.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

/**
 * Abstracts a kind of container with variable but well-typed properties that can be referenced using
 * {@linkplain Key special keys}.
 *
 * <p>To get an instance use {@link Builder#build()} or {@link Builder#prepare()} and {@link Supplier#get()}</p>
 */
public interface Features {

    /**
     * Retrieves a new {@link FeatureMap.Builder}.
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Retrieves a feature, specified by a given key.
     *
     * @throws IllegalArgumentException when there is no feature specified by the given key in this map.
     */
    <T> T get(final Key<T> key);

    /**
     * Abstracts a key type that is used to refer to features in this context.
     *
     * @param <T> The type of the feature, being referred.
     */
    interface Key<T> {
    }


    /**
     * Implements a builder for {@link Features}.
     */
    final class Builder {

        private static final String NO_FEATURE = "no feature specified for key (%s)";

        @SuppressWarnings("rawtypes")
        private final Map<Key, Supplier> map;

        private Builder() {
            map = new HashMap<>(0);
        }

        public final <T> Builder set(final Key<T> key, final T feature) {
            return setup(key, () -> feature);
        }

        public final <T> Builder setup(final Key<T> key, final Supplier<T> supplier) {
            map.put(key, supplier);
            return this;
        }


        /**
         * Prepares new instances of {@link Features}. Use {@link Supplier#get()} to finally get such an instance.
         */
        public final Supplier<Features> prepare() {
            final Map<Key, Supplier> stage = unmodifiableMap(new HashMap<>(map));
            return () -> new Impl(stage);
        }

        /**
         * Builds a new instance of {@link FeatureMap}.
         */
        public final Features build() {
            return prepare().get();
        }

        private class Impl implements Features {

            private final Map<Key, Object> map;

            private Impl(final Map<Key, Supplier> stage) {
                this.map = unmodifiableMap(
                        stage.entrySet().stream().collect(toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().get())));
            }

            @Override
            public <T> T get(final Key<T> key) {
                //noinspection unchecked
                return Optional.ofNullable((T) map.get(key))
                        .orElseThrow(() -> new IllegalArgumentException(String.format(NO_FEATURE, key)));
            }
        }
    }
}
