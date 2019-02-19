package de.team33.libs.provision.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableMap;

/**
 * <p>Represents a kind of map that allows {@linkplain Key special keys} to {@linkplain #get(Key) access}
 * variable but well-typed features.</p>
 *
 * <p>To get an instance use {@link Builder#build()} or {@link Builder#prepare()} and {@link Stage#get()}</p>
 */
public final class FeatureMap
{

  private static final String NO_FEATURE = "no feature specified for key (%s)";

  @SuppressWarnings("rawtypes")
  private final Map<Key, Object> map;

  private FeatureMap(final Stage stage) {
    this.map = unmodifiableMap(
      stage.map.entrySet().stream()
               .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()))
    );
  }

  /**
   * Retrieves a new {@link Builder}.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Retrieves a feature, specified by a given key.
   *
   * @throws IllegalArgumentException when there is no feature specified by the given key in this map.
   */
  public final <T> T get(final Key<T> key) {
    //noinspection unchecked
    return Optional.ofNullable((T) map.get(key))
                   .orElseThrow(() -> new IllegalArgumentException(String.format(NO_FEATURE, key)));
  }

  /**
   * Abstracts a key type that is used to refer to features in this context.
   *
   * @param <T> The type of the feature, being referred.
   */
  public interface Key<T> {
  }

  /**
   * Represents a preliminary stage of {@link FeatureMap}.
   */
  public static final class Stage implements Supplier<FeatureMap> {

    @SuppressWarnings("rawtypes")
    private final Map<Key, Supplier> map;

    private Stage(final Builder builder) {
      this.map = unmodifiableMap(new HashMap<>(builder.map));
    }

    /**
     * Finally supplies a new instance of {@link FeatureMap}.
     */
    @Override
    public final FeatureMap get() {
      return new FeatureMap(this);
    }

    /**
     * Creates a new {@link Builder} based on this stage.
     */
    public final Builder builder() {
      return new Builder(this);
    }
  }

  /**
   * Represents a builder for {@link FeatureMap}.
   */
  public static final class Builder {

    @SuppressWarnings("rawtypes")
    private final Map<Key, Supplier> map;

    private Builder() {
      map = new HashMap<>(0);
    }

    private Builder(final Stage stage) {
      map = new HashMap<>(stage.map);
    }

    public final <T> Builder set(final Key<T> key, final T feature) {
      return setup(key, () -> feature);
    }

    public final <T> Builder setup(final Key<T> key, final Supplier<T> supplier) {
      map.put(key, supplier);
      return this;
    }


    /**
     * Prepares new instances of {@link FeatureMap}. Use {@link Stage#get()} to finally get such instances.
     */
    public final Stage prepare() {
      return new Stage(this);
    }

    /**
     * Builds a new instance of {@link FeatureMap}.
     */
    public final FeatureMap build() {
      return prepare().get();
    }
  }
}
