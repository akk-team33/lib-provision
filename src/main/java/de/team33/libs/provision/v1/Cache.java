package de.team33.libs.provision.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


public class Cache<P, K, R>
{

  private final Map<K, R> cache = new ConcurrentHashMap<>(0);

  private final Function<P, K> toKey;

  private final Function<P, R> newResult;

  public Cache(final Function<P, K> toKey, final Function<P, R> newResult)
  {
    this.toKey = toKey;
    this.newResult = newResult;
  }

  public R get(final P parameters)
  {
    final K key = toKey.apply(parameters);
    return Optional.ofNullable(cache.get(key)).orElseGet(() -> {
      final R result = newResult.apply(parameters);
      cache.put(key, result);
      return result;
    });
  }
}
