package de.team33.libs.provision.v1;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

// Premature
public class FeatureMap implements Features
{

  private static final String NO_FEATURE = "no feature specified for key (%s)";

  private final Map<Key, Object> map;

  private FeatureMap(final Map<Key, Supplier> stage)
  {
    this.map = unmodifiableMap(
      stage.entrySet().stream().collect(toMap(
        Map.Entry::getKey,
        entry -> entry.getValue().get())));
  }

  @Override
  public <T> T get(final Key<T> key)
  {
    //noinspection unchecked
    return Optional.ofNullable((T) map.get(key))
                   .orElseThrow(() -> new IllegalArgumentException(String.format(NO_FEATURE, key)));
  }


  /**
   * Implements a builder for {@link Features}.
   */
  public static final class Builder {

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
     * Prepares new instances of {@link Features}.
     * Use {@link Supplier#get()} to finally get such an instance.
     */
    public final Supplier<Features> prepare() {
      final Map<Key, Supplier> stage = unmodifiableMap(new HashMap<>(map));
      return () -> new FeatureMap(stage);
    }

    /**
     * Builds a new instance of {@link Features}.
     */
    public final Features build() {
      return prepare().get();
    }
  }
}
