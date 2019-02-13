package de.team33.test.provision.v1;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.function.Function;

import de.team33.libs.provision.v1.Cache;
import org.junit.Test;


public class CacheTest
{

  private static final long LONG_A = 123456789L;

  private int count;

  private final Function<Long, Date> newDate;

  private final Cache<Long, Long, Date> cache;

  public CacheTest()
  {
    count = 0;
    newDate = value -> {
      count += 1;
      return new Date(value);
    };
    cache = new Cache<>(value -> value, newDate);
  }

  @Test
  public void getSame()
  {
    assertEquals(0, count);
    assertSame(cache.get(LONG_A), cache.get(LONG_A));
    assertSame(cache.get(LONG_A), cache.get(LONG_A));
    assertEquals(1, count);
  }
}
